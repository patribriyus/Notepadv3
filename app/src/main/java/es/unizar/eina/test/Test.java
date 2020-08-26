package es.unizar.eina.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

import es.unizar.eina.notepadv3.NotesDbAdapter;

import static android.util.Log.d;
import static es.unizar.eina.notepadv3.NotesDbAdapter.Item;

public class Test {
    static private final int NUM_NOTAS = 1000;
    static private long idRow;
    static private final String prefijo = "Nota_";

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


        // ----------------------- PRUEBAS UPDATENOTE() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "UPDATENOTE");

// ******** Se cubren las clases: 1,2,5,7,9,10 VÁLIDAS
        d("clases--> 1,2,5,7,9,10", "COMPROBACIÓN");
        updateNote(1, "Update1", "Descripcion", Long.valueOf(1));

// ******** Se cubre la clase: 11 NO VÁLIDA
        d("clase--> 11", "COMPROBACIÓN");
        updateNote(1, "Update2", "Descripcion", null);

// ******** Se cubren lad clased: 3 NO VÁLIDA
        d("clases--> 3", "COMPROBACIÓN");
        updateNote(1, "", "Descripcion", Long.valueOf(1));

// ******** Se cubren lad clased: 4 NO VÁLIDA
        d("clases--> 4", "COMPROBACIÓN");
        updateNote(1, null, "Descripcion", Long.valueOf(1));

// ******** Se cubre la clase: 6 NO VÁLIDA
        d("clase--> 6", "COMPROBACIÓN");
        updateNote(1, "Update3", null, Long.valueOf(1));

// ******** Se cubre la clase: 8 NO VÁLIDA
        d("clase--> 8", "COMPROBACIÓN");
        updateNote(0, "Update4", "Descripcion", Long.valueOf(1));

// ******** Se cubre la clase: 12 NO VÁLIDA
        d("clase--> 12", "COMPROBACIÓN");
        updateNote(1, "Update5", "Descripcion", Long.valueOf(0));


        // ----------------------- PRUEBAS DELETENOTE() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "DELETENOTE");

// ******** Se cubre la clase: 1 VÁLIDA
        d("clase--> 1", "COMPROBACIÓN");
        deleteNote(1);

// ******** Se cubre la clase: 2 NO VÁLIDA
        d("clase--> 2", "COMPROBACIÓN");
        deleteNote(0);


        // ----------------------- PRUEBAS CREATECATEGORY() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "CREATECATEGORY");

// ******** Se cubren las clases: 1,2 VÁLIDAS
        d("clase--> 1,2", "COMPROBACIÓN");
        createCategory("Categoría1");

// ******** Se cubre la clase: 3 VÁLIDA
        d("clase--> 3", "COMPROBACIÓN");
        createCategory("");

// ******** Se cubre la clase: 4 VÁLIDA
        d("clase--> 4", "COMPROBACIÓN");
        createCategory(null);


        // ----------------------- PRUEBAS UPDATECATEGORY() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "UPDATECATEGORY");

// ******** Se cubren las clases: 1,2,5 VÁLIDAS
        d("clase--> 1,2,5", "COMPROBACIÓN");
        updateCategory(2,"UpdateCat1");

// ******** Se cubre la clase: 6 NO VÁLIDA
        d("clase--> 6", "COMPROBACIÓN");
        updateCategory(0,"UpdateCat2");

// ******** Se cubre la clase: 4 NO VÁLIDA
        d("clase--> 4", "COMPROBACIÓN");
        updateCategory(2,null);

// ******** Se cubre la clase: 3 NO VÁLIDA
        d("clase--> 3", "COMPROBACIÓN");
        updateCategory(2,"");

        // ----------------------- PRUEBAS DELETECATEGORY() --------------------
        d("//////////////////////////////////FUNCIÓN A EJECUTAR", "DELETECATEGORY");

// ******** Se cubre la clase: 1 VÁLIDA
        d("clase--> 1", "COMPROBACIÓN");
        deleteCategory(2);

// ******** Se cubre la clase: 2 NO VÁLIDA
        d("clase--> 2", "COMPROBACIÓN");
        deleteCategory(0);
    }

    /**
     * Prueba de volumen
     *  - Creación de 1000 notas Nota_1,Nota_2,...Nota_1000
     */
    @SuppressLint("LongLogTag")
    public void volumen(){
        try {
            for (Integer i = 1; i <= NUM_NOTAS; i++) {
                idRow = createNote(prefijo + i.toString(), "CUERPO_TEST", Long.valueOf(1));
                d("Se ha creado la nota número", i.toString());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressLint("LongLogTag")
    public void volumenFin(){
        try {
            for (Integer i = 0; i < NUM_NOTAS; i++) {
                deleteNote();
                d("Se ha eliminado la nota número", i.toString());
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prueba de sobrecarga
     *  - Calcula la longitud máxima que el texto de una nota es capaz de soportar, en cuanto
     *  a número de caracteres.
     *  Se crean notas Nota_1,Nota_2,... con el cuerpo compuesto por diferentes longitudes de
     *  asteriscos (*), incrementando esta longitud por 2
     */
    @SuppressLint("LongLogTag")
    public void sobrecarga(){
        Integer longitudTxt = 1, i = 1;
        long ID = Long.valueOf(0);

        try {
            while (true) {
                char[] chars = new char[longitudTxt];
                Arrays.fill(chars, '*');
                ID = createNote(prefijo + i.toString(), "", Long.valueOf(1));
                d("Se ha creado nota con longitud", longitudTxt.toString());

                longitudTxt *= 2; i++;
            }
        } catch (RuntimeException e) {
            d("No se ha podido crear la nota con longitud", longitudTxt.toString());
            System.out.println(e.getMessage());
        }
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
            d("+", " RESULTADO CORRECTO");
            return salida;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(false);
        }
    }
    // Prueba volumen
    public boolean deleteNote(){
        try {
            boolean salida = mDbHelper.deleteItem();
            d("+", " RESULTADO CORRECTO");
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
    public boolean updateNote(long rowId, String title, String body, Long category){
        try {
            boolean salida = mDbHelper.updateItem(Item.NOTE, rowId, title, body, category);
            d("+", " RESULTADO CORRECTO");
            return salida;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(false);
        }
    }

    /**
     * Crea una nueva nota a partir del título y texto proporcionados. Si la
     * nota se crea correctamente, devuelve el nuevo rowId de la nota; en otro
     * caso, devuelve -1 para indicar el fallo.
     *
     * @param title
     * el título de la nota;
     * title != null y title.length() > 0
     * @return rowId de la nueva nota o -1 si no se ha podido crear
     */
    public long createCategory(String title){
        try{
            long id = mDbHelper.createItem(Item.CATEGORY, title, "", Long.valueOf(1));
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
    public boolean deleteCategory(long rowId){
        try {
            boolean salida = mDbHelper.deleteItem(Item.CATEGORY, rowId);
            d("+", " RESULTADO CORRECTO");
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
     * @return true si y solo si la nota se actualizó correctamente
     */
    public boolean updateCategory(long rowId, String title){
        try {
            boolean salida = mDbHelper.updateItem(Item.CATEGORY, rowId, title, "", Long.valueOf(1));
            d("+", " RESULTADO CORRECTO");
            return salida;
        } catch (Throwable t){
            d("EXCEPCION/ERROR", "- RESULTADO INCORRECTO", t);
            return(false);
        }
    }
}
