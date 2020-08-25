package es.unizar.eina.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import es.unizar.eina.notepadv3.NotesDbAdapter;

import static android.util.Log.d;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item;

public class Test {
    private NotesDbAdapter mDbHelper;

    public Test(NotesDbAdapter mDbHelper) {
        this.mDbHelper = mDbHelper;
        mDbHelper.open();
    }

    @SuppressLint("LongLogTag")
    public void caja_negra(){

        // ----------------------- PRUEBAS CREATENOTE() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "CREATENOTE");

// ******** Se cubren las clases: 1,2,5,7 VÁLIDAS
        d("clases--> 1,2,5,7", "COMPROBACIÓN");
        createNote("Nota1", "Descripcion", Long.valueOf(1));

// ******** Se cubre la clase: 8 NO VÁLIDA
        d("clase--> 8", "COMPROBACIÓN");
        createNote("Nota2", "Descripcion", null);

// ******** Se cubre la clase: 6 NO VÁLIDA
        d("clase--> 6", "COMPROBACIÓN");
        createNote("Nota3", null, Long.valueOf(1));

// ******** Se cubre la clase: 9 NO VÁLIDA
        d("clase--> 9", "COMPROBACIÓN");
        createNote("Nota4", "Descripcion", Long.valueOf(0));

// ******** Se cubre la clase: 3 NO VÁLIDA
        d("clase--> 3", "COMPROBACIÓN");
        createNote("", "Descripcion", Long.valueOf(1));

// ******** Se cubre la clase: 4 NO VÁLIDA
        d("clase--> 4", "COMPROBACIÓN");
        createNote(null, "Descripcion", Long.valueOf(1));
    }

    public void volumen(){

    }

    public void sobrecarga(){

    }

    /**
     * Crea una nueva nota a partir del título y texto proporcionados. Si la
     * nota se crea correctamente, devuelve el nuevo rowId de la nota; en otro
     * caso, devuelve -1 para indicar el fallo.
     *
     * @param title
     * el título de la nota;
     * title != null y title.length() > 0
     * @param body
     * el texto de la nota;
     * body != null
     * @param category
     * la categoría de la nota
     * @return rowId de la nueva nota o -1 si no se ha podido crear
     */
    public long createNote(String title, String body, Long category){
        try{
            long id = mDbHelper.createItem(Item.NOTE, title, body, category);
            d("+", " RESULTADO CORRECTO");
            return id;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(-2);
        }
    }

    /**
     * Borra la nota cuyo rowId se ha pasado como parámetro
     *
     * @param rowId
     * el identificador de la nota que se desea borrar;
     * rowId > 0
     * @return true si y solo si la nota se ha borrado
     */
    public boolean deleteNote(long rowId){
        try {
            boolean salida = mDbHelper.deleteItem(Item.NOTE, rowId);
            d("", "+ RESULTADO CORRECTO");
            return salida;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(false);
        }
    }

    /**
     * Actualiza una nota a partir de los valores de los parámetros. La nota que
     * se actualizará es aquella cuyo rowId coincida con el valor del parámetro.
     * Su título, texto y categoría se modificarán con los valores de title, body y category
     * respectivamente.
     *
     * @param rowId
     * el identificador de la nota que se desea actualizar;
     * rowId > 0
     * @param title
     * el título de la nota;
     * title != null y title.length() > 0
     * @param body
     * el texto de la nota;
     * body != null
     * @param category
     * la categoría de la nota
     * @return true si y solo si la nota se actualizó correctamente
     */
    public boolean updateNote(long rowId, String title, String body, long category){
        try {
            boolean salida = mDbHelper.updateItem(Item.NOTE, rowId, title, body, category);
            d("", "+ RESULTADO CORRECTO");
            return salida;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(false);
        }
    }
}
