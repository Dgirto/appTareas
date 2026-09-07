package com.example.apptareas.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.apptareas.data.TareaContract.TareaEntry;
import com.example.apptareas.data.TareaContract.UsuarioEntry;

/**
 * SQLiteOpenHelper: crea y versiona la base de datos "tareas.db".
 * Esquema tomado de la sección 5 del plan de proyecto.
 *
 * NOTA: responsabilidad formal de B1. Versión provisional para que B2 pueda
 * desarrollar y probar el TareaDao sin bloquearse. Reconciliar por PR con
 * la versión final de B1 antes de mergear a develop.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tareas.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_USUARIOS =
            "CREATE TABLE " + UsuarioEntry.TABLE_NAME + " (" +
                    UsuarioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UsuarioEntry.COLUMN_NOMBRE + " TEXT NOT NULL," +
                    UsuarioEntry.COLUMN_CORREO + " TEXT NOT NULL UNIQUE," +
                    UsuarioEntry.COLUMN_PASSWORD_HASH + " TEXT," +
                    UsuarioEntry.COLUMN_PROVEEDOR + " TEXT NOT NULL DEFAULT 'local'," +
                    UsuarioEntry.COLUMN_GOOGLE_ID + " TEXT UNIQUE," +
                    UsuarioEntry.COLUMN_FOTO_URL + " TEXT," +
                    UsuarioEntry.COLUMN_FECHA_REGISTRO + " TEXT NOT NULL)";

    private static final String SQL_CREATE_TAREAS =
            "CREATE TABLE " + TareaEntry.TABLE_NAME + " (" +
                    TareaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TareaEntry.COLUMN_TITULO + " TEXT NOT NULL," +
                    TareaEntry.COLUMN_DESCRIPCION + " TEXT," +
                    TareaEntry.COLUMN_ESTADO + " TEXT NOT NULL DEFAULT 'pendiente'," +
                    TareaEntry.COLUMN_FECHA_VENCIMIENTO + " TEXT," +
                    TareaEntry.COLUMN_FECHA_CREACION + " TEXT NOT NULL," +
                    TareaEntry.COLUMN_USUARIO_ASIGNADO + " TEXT," +
                    TareaEntry.COLUMN_USUARIO_ID + " INTEGER REFERENCES " +
                    UsuarioEntry.TABLE_NAME + "(" + UsuarioEntry._ID + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_INDEX_ESTADO =
            "CREATE INDEX idx_tareas_estado ON " + TareaEntry.TABLE_NAME +
                    "(" + TareaEntry.COLUMN_ESTADO + ")";

    private static final String SQL_CREATE_INDEX_USUARIO =
            "CREATE INDEX idx_tareas_usuario ON " + TareaEntry.TABLE_NAME +
                    "(" + TareaEntry.COLUMN_USUARIO_ID + ")";

    private static final String SQL_DELETE_TAREAS =
            "DROP TABLE IF EXISTS " + TareaEntry.TABLE_NAME;

    private static final String SQL_DELETE_USUARIOS =
            "DROP TABLE IF EXISTS " + UsuarioEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Obligatorio para que el ON DELETE CASCADE de tareas.usuario_id funcione.
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USUARIOS);
        db.execSQL(SQL_CREATE_TAREAS);
        db.execSQL(SQL_CREATE_INDEX_ESTADO);
        db.execSQL(SQL_CREATE_INDEX_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Versión 1 -> por ahora no hay migraciones reales; si se agregan
        // columnas en el futuro, esto debe cambiar a ALTER TABLE en lugar
        // de borrar los datos.
        db.execSQL(SQL_DELETE_TAREAS);
        db.execSQL(SQL_DELETE_USUARIOS);
        onCreate(db);
    }
}
