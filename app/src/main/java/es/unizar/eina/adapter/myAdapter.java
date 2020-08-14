package es.unizar.eina.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import es.unizar.eina.notepadv3.NotesDbAdapter;
import es.unizar.eina.notepadv3.R;

public class myAdapter extends CursorAdapter {
    public myAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.notes_row, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView nom_nota = (TextView) view.findViewById(R.id.nom_nota);
        TextView nom_categoria = (TextView) view.findViewById(R.id.nom_categoria);
        // Extract properties from cursor
        String body = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE_CAT));
        // Populate fields with extracted properties
        nom_nota.setText(body);
        nom_categoria.setText(priority);
    }
}
