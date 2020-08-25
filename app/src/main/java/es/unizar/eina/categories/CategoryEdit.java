package es.unizar.eina.categories;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import es.unizar.eina.notepadv3.Notepadv3;
import es.unizar.eina.notepadv3.NotesDbAdapter;
import es.unizar.eina.notepadv3.R;

import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.CATEGORY;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item.NOTE;

public class CategoryEdit extends AppCompatActivity {

    private EditText mTitleText;
    private EditText mIdText;
    private Long mRowId;
    private boolean ok = false;

    private NotesDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.category_edit);

        mTitleText = (EditText) findViewById(R.id.title_cat);
        mIdText = (EditText) findViewById(R.id.id_cat);

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

    // Recupera los datos de la categoria
    private void populateFields() {
        if (mRowId != null) {
            setTitle(R.string.edit_cat);
            Cursor note = mDbHelper.fetchItem(CATEGORY, mRowId); // Recupera la categoria
            startManagingCursor(note);
            mIdText.setText(mRowId.toString());
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE_CAT)));
        } else {
            setTitle(R.string.create_cat);
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

        if(ok) {
            if (mRowId == null) {
                long id = mDbHelper.createItem(CATEGORY, title, "", Long.valueOf(1));
                if (id > 0) {
                    mRowId = id;
                }
            } else {
                mDbHelper.updateItem(CATEGORY, mRowId, title, "", Long.valueOf(1));
            }
        }
    }
}
