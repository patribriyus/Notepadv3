package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.NOTE;

public class NoteEdit extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mIdText;
    private Spinner spinner;
    private Long mRowId;

    List<String> listaCategories;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mIdText = (EditText) findViewById(R.id.id_nota);
        spinner = (Spinner) findViewById(R.id.spinner);

        /*//Implemento el setOnItemSelectedListener: para realizar acciones cuando se seleccionen los ítems
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //Convierto la variable List<> en un ArrayList<>()
        listaCategories = new ArrayList<>();
        //Almaceno el tamaño de la lista getAllPaises()
        int sizeListaCategories = mDbHelper.getAllCategories().size();
        //Agrego los nombres de los países obtenidos y lo almaceno en  `listaPaisesSql`
        for(int i = 0; i < sizeListaCategories; i++){
            listaCategories.add(mDbHelper.getAllCategories().get(i).getNombrePais());
        }
        //Implemento el adapter con el contexto, layout, listaPaisesSql
        ArrayAdapter<String> comboAdapterSql = new ArrayAdapter<>(this, this, listaCategories);
        //Cargo el spinner con los datos
        spinner.setAdapter(comboAdapterSql);*/


        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }

        });

        Button discard= (Button) findViewById(R.id.discard);
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Recupera los datos de la nota (titulo y cuerpo)
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchItem(NOTE, mRowId);
            startManagingCursor(note);

            mIdText.setText(mRowId.toString());
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            //spinner.set;
        } else {
            mIdText.setText("***");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        //int category = spinner.getText().toString();
        int category = 1;
        if (mRowId == null) {
            long id = mDbHelper.createItem(NOTE, title, body, category);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateItem(NOTE, mRowId, title, body, category);
        }
    }
}
