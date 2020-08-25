package es.unizar.eina.notepadv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.text.TextUtils.isEmpty;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public enum Item{
      NOTE, CATEGORY
    };

    public static final String KEY_TITLE = "title";
    public static final String KEY_TITLE_CAT = "title_cat";
    public static final String KEY_BODY = "body";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String NOTES_DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement not null, "
                    + "title text not null, body text not null, category integer DEFAULT 1);";
    private static final String CATEGORIES_DATABASE_CREATE =
            "create table categories (_id integer primary key autoincrement, "
                    + "title_cat text not null);";
    private static final String DATABASE_TRIGGER =
            "create trigger trigger_notes before delete on categories "
            + "for each row BEGIN "
            + "update notes set category = 1 where category = old._id; "
            + "END;";

    private static final String NOTES_DATABASE_TABLE = "notes";
    private static final String CATEGORIES_DATABASE_TABLE = "categories";

    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CATEGORIES_DATABASE_CREATE);
            db.execSQL(NOTES_DATABASE_CREATE);
            db.execSQL(DATABASE_TRIGGER);

            // Se inserta la categoria por defecto '--'
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_TITLE_CAT, "--");
            db.insert(CATEGORIES_DATABASE_TABLE, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            db.execSQL("DROP TABLE IF EXISTS categories");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @param body  the body of the note
     * @return rowId or -1 if failed
     */
    public long createItem(Item item, String title, String body, Long category) {
        if (title==null || isEmpty(title.replaceAll("\\s*", ""))
                || body==null || category==null || category < 1) return -1;

        ContentValues initialValues = new ContentValues();

        if(item == Item.NOTE) {
            initialValues.put(KEY_TITLE, title);
            initialValues.put(KEY_BODY, body);
            initialValues.put(KEY_CATEGORY, category);

            return mDb.insert(NOTES_DATABASE_TABLE, null, initialValues);
        }
        else {
            initialValues.put(KEY_TITLE_CAT, title);
            return mDb.insert(CATEGORIES_DATABASE_TABLE, null, initialValues);
        }

    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem(Item item, long rowId) {
        if(rowId > 0) {
            if (item == item.NOTE)
                return mDb.delete(NOTES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
            else return mDb.delete(CATEGORIES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        }
        else return false;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllItems(Item item) {
        String MY_QUERY;

        if(item == item.NOTE)  MY_QUERY = "select n._id, n.title, c.title_cat FROM notes n left join categories c on n.category = c._id order by n.title";
        else MY_QUERY = "SELECT _id, title_cat FROM categories";

        return mDb.rawQuery(MY_QUERY, null);
    }

    // Muestra las notas pertenecientes solo a la categoria con ID = id
    public Cursor fetchAllItems(Item item, Long id) {
        String MY_QUERY = "select n._id, n.title, c.title_cat FROM notes n left join categories c on n.category = c._id "
        + " WHERE n.category = " + id + " order by n.title";

        return mDb.rawQuery(MY_QUERY, null);
    }

    // Muestra las notas ordenadas por orden alfabético/categoría
    public Cursor fetchAllItems() {
        String MY_QUERY = "select n._id, n.title, c.title_cat FROM notes n left join categories c on n.category = c._id order by c.title_cat";

        return mDb.rawQuery(MY_QUERY, null);
    }

    // Función solo para mostrar todas las categorías menos la predeterminada
    public Cursor fetchAllCategories() {
        String MY_QUERY = "SELECT _id, title_cat FROM categories WHERE `_id` > 1";
        return mDb.rawQuery(MY_QUERY, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchItem(Item item, long rowId) throws SQLException {
        Cursor mCursor;
        if(item == item.NOTE) {
            mCursor =
                    mDb.query(true, NOTES_DATABASE_TABLE, new String[]{KEY_ROWID,
                                    KEY_TITLE, KEY_BODY, KEY_CATEGORY}, KEY_ROWID + "=" + rowId, null,
                            null, null, null, null);
        }
        else{
            mCursor =
                    mDb.query(true, CATEGORIES_DATABASE_TABLE, new String[]{KEY_ROWID,
                                    KEY_TITLE_CAT}, KEY_ROWID + "=" + rowId, null,
                            null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body  value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateItem(Item item, long rowId, String title, String body, Long category) {
        if (title==null || isEmpty(title.replaceAll("\\s*", ""))
                || body==null || category==null || category < 1 || rowId < 1) return false;

        ContentValues args = new ContentValues();

        if(item == item.NOTE){
            args.put(KEY_TITLE, title);
            args.put(KEY_BODY, body);
            args.put(KEY_CATEGORY, category);

            return mDb.update(NOTES_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
        else{
            args.put(KEY_TITLE_CAT, title);
            return mDb.update(CATEGORIES_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
    }
}