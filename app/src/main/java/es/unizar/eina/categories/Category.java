package es.unizar.eina.categories;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import es.unizar.eina.notepadv3.NoteEdit;
import es.unizar.eina.notepadv3.Notepadv3;
import es.unizar.eina.notepadv3.NotesDbAdapter;
import es.unizar.eina.notepadv3.R;
import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;

import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.CATEGORY;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.NOTE;


public class Category extends AppCompatActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_NOTES = 2;

    private static final int PRUEBAS = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;

    private NotesDbAdapter mDbHelper;
    private ListView mList;

    private int checkedItem = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        Button btn_add= (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               createCategory();
            }
        });

        Button btn_ojo= (Button) findViewById(R.id.btn_ojo);
        btn_ojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView) findViewById(R.id.list);
        fillData();

        registerForContextMenu(mList);

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ver");
        String[] items = {"Notas","Categorías"};
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;
                switch (which) {
                    case 0: // NOTAS
                        fetchNotes();
                        break;
                    case 1: // CATEGORIAS
                        fillData();
                        break;
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", null);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllCategories();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] notas = new String[]{NotesDbAdapter.KEY_TITLE_CAT};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] notes_layout = new int[]{R.id.nom_categoria};
        //Log.i("TAG",Integer.toString(notes_layout.length));

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.categories_row, notesCursor, notas, notes_layout);
        mList.setAdapter(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        //menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        //menu.add(Menu.NONE, PRUEBAS, Menu.NONE, R.string.menu_pruebas);

        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.menu_category, menu);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.btn_add:
                Log.i("TAG","Se ha pulsado el boton de add");
                createNote();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_cat_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_cat_edit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case DELETE_ID:
                mDbHelper.deleteItem(CATEGORY, info.id);
                fillData();
                return true;
            case EDIT_ID:
                editCategory(info.position, info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createCategory() {
        Intent i = new Intent(this, CategoryEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    protected void editCategory(int position, long id) {
        Intent i = new Intent(this, CategoryEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void fetchNotes() {
        Intent i = new Intent(this, Notepadv3.class);
        startActivityForResult(i, ACTIVITY_NOTES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
