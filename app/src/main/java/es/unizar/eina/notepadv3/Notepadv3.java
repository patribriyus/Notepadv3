package es.unizar.eina.notepadv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import es.unizar.eina.categories.Category;
import es.unizar.eina.adapter.MyAdapter;
import es.unizar.eina.send.SendAbstraction;
import es.unizar.eina.send.SendAbstractionImpl;
import es.unizar.eina.test.Test;

import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.CATEGORY;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.NOTE;


public class Notepadv3 extends AppCompatActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_CATEGORIES = 2;

    private static final int DELETE_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.FIRST + 1;
    private static final int SEND_MAIL_ID = Menu.FIRST + 2;
    private static final int SEND_SMS_ID = Menu.FIRST + 3;

    private static final int PRUEBA_CAJA_NEGRA = Menu.FIRST + 4;
    private static final int PRUEBA_SISTEMA_VOLUMEN = Menu.FIRST + 5;
    private static final int PRUEBA_SISTEMA_VOLUMEN_FIN = Menu.FIRST + 6;
    private static final int PRUEBAS_SISTEMA_SOBRECARGA = Menu.FIRST + 7;

    private NotesDbAdapter mDbHelper;
    private ListView mList;

    private int checkedItem = 0;

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
               createNote();
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
        fillData(NOTE);

        registerForContextMenu(mList);

    }

    private void showDialogCategories(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Ver notas de la categoría");

        // Get all of the notes from the database and create the item list
        final Cursor notesCursor = mDbHelper.fetchAllItems(CATEGORY);
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] items = new String[]{NotesDbAdapter.KEY_TITLE_CAT};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] notes_layout = new int[]{R.id.nom_categoria};

        //Log.i("TAG",Integer.toString(notes_layout.length));

        // Now create an array adapter and set it to display using our row
        final SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.categories_row, notesCursor, items, notes_layout);

        alertDialog.setSingleChoiceItems(notesCursor, -1, NotesDbAdapter.KEY_TITLE_CAT,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // llamada a funcion para mostrar notas de la cat seleccionada
                        notesCursor.moveToPosition(which);
                        Long id = notesCursor.getLong(notesCursor.getColumnIndex(NotesDbAdapter.KEY_ROWID));
                        fillData(id);
                        dialog.dismiss();
                    }
                });

        alertDialog.setNegativeButton("Cancelar", null);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
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
                        fillData(NOTE);
                        break;
                    case 1: // CATEGORIAS
                        fetchCategories();
                        break;
                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", null);
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void fillData(NotesDbAdapter.Item item) {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllItems(item);

        MyAdapter adapter = new MyAdapter(this, notesCursor);
        mList.setAdapter(adapter);
    }

    // Muestra las notas pertenecientes solo a la categoria con ID = id
    private void fillData(Long id) {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllItems(CATEGORY, id);

        MyAdapter adapter = new MyAdapter(this, notesCursor);
        mList.setAdapter(adapter);
    }

    // Muestra las notas por orden alfabético/categoria
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllItems();

        MyAdapter adapter = new MyAdapter(this, notesCursor);
        mList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, PRUEBA_CAJA_NEGRA, Menu.NONE, R.string.caja_negra);
        menu.add(Menu.NONE, PRUEBA_SISTEMA_VOLUMEN, Menu.NONE, R.string.prueba_volumen);
        menu.add(Menu.NONE, PRUEBA_SISTEMA_VOLUMEN_FIN, Menu.NONE, R.string.prueba_volumen_fin);
        menu.add(Menu.NONE, PRUEBAS_SISTEMA_SOBRECARGA, Menu.NONE, R.string.prueba_sobrecarga);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter_note, menu);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.group_categories:
                showDialogCategories();
                return true;
            case R.id.by_categories:
                fillData();
                return true;
            case R.id.by_title:
                fillData(NOTE);
                return true;
            case PRUEBA_CAJA_NEGRA:
                pruebas(0);
                return true;
            case PRUEBA_SISTEMA_VOLUMEN:
                pruebas(1);
                return true;
            case PRUEBA_SISTEMA_VOLUMEN_FIN:
                pruebas(2);
                return true;
            case PRUEBAS_SISTEMA_SOBRECARGA:
                pruebas(3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_note_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_note_edit);
        menu.add(Menu.NONE, SEND_MAIL_ID, Menu.NONE, R.string.menu_note_send_mail);
        menu.add(Menu.NONE, SEND_SMS_ID, Menu.NONE, R.string.menu_note_send_sms);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case DELETE_ID:
                Log.i("TAG",String.valueOf(item.getItemId()));
                mDbHelper.deleteItem(NOTE, info.id);
                fillData(NOTE);
                return true;
            case EDIT_ID:
                editNote(info.position, info.id);
                return true;
            case SEND_MAIL_ID:
                sendNote(info.id, "mail");
            case SEND_SMS_ID:
                sendNote(info.id, "SMS");
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    protected void editNote(int position, long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    protected void sendNote(long ID, String method) {
        Cursor note = mDbHelper.fetchItem(NOTE, ID);
        String title = note.getString(
                note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String body = note.getString(
                note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
        SendAbstraction noteSend = new SendAbstractionImpl(this, method);
        noteSend.send(title, body);
    }

    private void fetchCategories() {
        Intent i = new Intent(this, Category.class);
        startActivityForResult(i, ACTIVITY_CATEGORIES);
    }

    public void pruebas(int prueba){
        Test test = new Test(mDbHelper);

        if (prueba == 0) test.caja_negra();
        else if (prueba == 1) test.volumen();
        else if (prueba == 2) test.volumenFin();
        else test.sobrecarga();

        fillData(NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(NOTE);
    }

}
