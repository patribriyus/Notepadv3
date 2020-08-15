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
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, category integer not null,"
                    + "foreign key(category) references categories(_id) on delete set DEFAULT);";
    private static final String CATEGORIES_DATABASE_CREATE =
            "create table categories (_id integer primary key autoincrement, "
                    + "title_cat text not null);";

    private static final String NOTES_DATABASE_TABLE = "notes";
    private static final String CATEGORIES_DATABASE_TABLE = "categories";

    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 5;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(NOTES_DATABASE_CREATE);
            db.execSQL(CATEGORIES_DATABASE_CREATE);

            // Se inserta la categoria por defecto
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
    public long createItem(Item item, String title, String body, long category) {
        if (isEmpty(title.replaceAll("\\s*", ""))) return -1;

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);

        if(item == Item.NOTE) {
            initialValues.put(KEY_BODY, body);
            initialValues.put(KEY_CATEGORY, category);

            return mDb.insert(NOTES_DATABASE_TABLE, null, initialValues);
        }
        else return mDb.insert(CATEGORIES_DATABASE_TABLE, null, initialValues);

    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteItem(Item item, long rowId) {
        if(item == item.NOTE) return mDb.delete(NOTES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
        else return mDb.delete(CATEGORIES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllItems(Item item) {
        if(item == item.NOTE){
            String MY_QUERY = "select * FROM notes n left join categories c on n.category = c.`_id`";
            return mDb.rawQuery(MY_QUERY, null);
        }
        else{
            return mDb.query(CATEGORIES_DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE},
                    null, null, null, null, KEY_TITLE);
        }
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
                                    KEY_TITLE}, KEY_ROWID + "=" + rowId, null,
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
    public boolean updateItem(Item item, long rowId, String title, String body, long category) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);

        if(item == item.NOTE){
            args.put(KEY_BODY, body);
            args.put(KEY_CATEGORY, category);

            return mDb.update(NOTES_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
        else{
            return mDb.update(CATEGORIES_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
        }
    }
}