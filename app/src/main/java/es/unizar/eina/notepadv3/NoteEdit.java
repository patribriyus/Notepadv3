package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.CATEGORY;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.NOTE;

public class NoteEdit extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mIdText;
    private Spinner spinner;
    private Long mRowId;
    private boolean ok = false;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mIdText = (EditText) findViewById(R.id.id_nota);
        spinner = (Spinner) findViewById(R.id.spinner);

        fillSpinner();

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
                ok = true;
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

    private void fillSpinner(){
        Cursor notesCursor = mDbHelper.fetchAllItems(CATEGORY);
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] notas = new String[]{NotesDbAdapter.KEY_TITLE_CAT};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] notes_layout = new int[]{android.R.id.text1};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                        notesCursor, notas, notes_layout, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        notes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(notes);
    }

    // Recupera los datos de la nota (titulo y cuerpo)
    private void populateFields() {
        if (mRowId != null) {
            setTitle(R.string.edit_note);
            Cursor note = mDbHelper.fetchItem(NOTE, mRowId);
            startManagingCursor(note);

            mIdText.setText(mRowId.toString());
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            Long id_cat = note.getLong(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORY));

            int i;
            for(i=0; i<=spinner.getAdapter().getCount() && spinner.getAdapter().getItemId(i)!=id_cat; i++)
            spinner.setSelection(i+1);
        } else {
            setTitle(R.string.create_note);
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
        Long category = spinner.getSelectedItemId();

        if(ok) {
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
}
