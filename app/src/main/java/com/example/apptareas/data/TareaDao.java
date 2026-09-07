package com.example.apptareas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apptareas.data.TareaContract.TareaEntry;
import com.example.apptareas.model.Tarea;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DAO de acceso a la tabla "tareas".
 * Responsable: B2 (CRUD de tareas).
 *
 * Reglas acordadas en el plan de proyecto:
 * - fecha_creacion la asigna este DAO en insertar(), nunca el llamador.
 * - Toda consulta de lectura se filtra por usuarioId (cada usuario ve solo sus tareas).
 * - El estado se guarda siempre en minúsculas ("pendiente", "en progreso", "completada").
 * - Todo Cursor se cierra siempre, incluso si ocurre una excepción.
 */
public class TareaDao {

    private final DatabaseHelper dbHelper;

    private static final String[] TODAS_LAS_COLUMNAS = {
            TareaEntry._ID,
            TareaEntry.COLUMN_TITULO,
            TareaEntry.COLUMN_DESCRIPCION,
            TareaEntry.COLUMN_ESTADO,
            TareaEntry.COLUMN_FECHA_VENCIMIENTO,
            TareaEntry.COLUMN_FECHA_CREACION,
            TareaEntry.COLUMN_USUARIO_ASIGNADO,
            TareaEntry.COLUMN_USUARIO_ID
    };

    public TareaDao(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    /**
     * Inserta una tarea nueva. Asigna fecha_creacion automáticamente (hoy, ISO yyyy-MM-dd)
     * y fuerza el estado por defecto a "pendiente" si viene vacío.
     *
     * @return el id autogenerado, o -1 si la inserción falló.
     */
    public long insertar(Tarea tarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(TareaEntry.COLUMN_TITULO, tarea.getTitulo());
            values.put(TareaEntry.COLUMN_DESCRIPCION, tarea.getDescripcion());

            String estado = tarea.getEstado();
            values.put(TareaEntry.COLUMN_ESTADO,
                    (estado == null || estado.isEmpty()) ? TareaContract.Estado.PENDIENTE : estado);

            values.put(TareaEntry.COLUMN_FECHA_VENCIMIENTO, tarea.getFechaVencimiento());
            values.put(TareaEntry.COLUMN_FECHA_CREACION, obtenerFechaHoyIso());
            values.put(TareaEntry.COLUMN_USUARIO_ASIGNADO, tarea.getUsuarioAsignado());
            values.put(TareaEntry.COLUMN_USUARIO_ID, tarea.getUsuarioId());

            return db.insert(TareaEntry.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // UPDATE
    // ------------------------------------------------------------------

    /**
     * Actualiza todos los campos editables de una tarea existente, identificada por su id.
     * No toca fecha_creacion: esa nunca se reescribe.
     *
     * @return número de filas afectadas (0 o 1).
     */
    public int actualizar(Tarea tarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(TareaEntry.COLUMN_TITULO, tarea.getTitulo());
            values.put(TareaEntry.COLUMN_DESCRIPCION, tarea.getDescripcion());
            values.put(TareaEntry.COLUMN_ESTADO, tarea.getEstado());
            values.put(TareaEntry.COLUMN_FECHA_VENCIMIENTO, tarea.getFechaVencimiento());
            values.put(TareaEntry.COLUMN_USUARIO_ASIGNADO, tarea.getUsuarioAsignado());

            String selection = TareaEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(tarea.getId())};

            return db.update(TareaEntry.TABLE_NAME, values, selection, selectionArgs);
        } finally {
            db.close();
        }
    }

    /**
     * Cambia únicamente el estado de una tarea (atajo para marcar completada / en progreso).
     *
     * @return número de filas afectadas (0 o 1).
     */
    public int cambiarEstado(long id, String estado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(TareaEntry.COLUMN_ESTADO, estado);

            String selection = TareaEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};

            return db.update(TareaEntry.TABLE_NAME, values, selection, selectionArgs);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // DELETE
    // ------------------------------------------------------------------

    /**
     * Elimina una tarea por id.
     *
     * @return número de filas eliminadas (0 o 1).
     */
    public int eliminar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String selection = TareaEntry._ID + " = ?";
            String[] selectionArgs = {String.valueOf(id)};
            return db.delete(TareaEntry.TABLE_NAME, selection, selectionArgs);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    /** Obtiene una tarea por id, o null si no existe. */
    public Tarea obtenerPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TareaEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        try (Cursor cursor = db.query(
                TareaEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursorATarea(cursor);
            }
            return null;
        } finally {
            db.close();
        }
    }

    /** Lista todas las tareas de un usuario, más recientes primero. */
    public List<Tarea> listarTodas(long usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TareaEntry.COLUMN_USUARIO_ID + " = ?";
        String[] selectionArgs = {String.valueOf(usuarioId)};
        String orderBy = TareaEntry.COLUMN_FECHA_CREACION + " DESC";

        try (Cursor cursor = db.query(
                TareaEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, orderBy)) {

            return cursorAListaDeTareas(cursor);
        } finally {
            db.close();
        }
    }

    /** Lista las tareas de un usuario filtradas por estado exacto. */
    public List<Tarea> listarPorEstado(long usuarioId, String estado) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TareaEntry.COLUMN_USUARIO_ID + " = ? AND " +
                TareaEntry.COLUMN_ESTADO + " = ?";
        String[] selectionArgs = {String.valueOf(usuarioId), estado};
        String orderBy = TareaEntry.COLUMN_FECHA_CREACION + " DESC";

        try (Cursor cursor = db.query(
                TareaEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, orderBy)) {

            return cursorAListaDeTareas(cursor);
        } finally {
            db.close();
        }
    }

    /** Busca tareas de un usuario cuyo título contenga el texto dado (case-insensitive, parcial). */
    public List<Tarea> buscarPorTitulo(long usuarioId, String texto) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = TareaEntry.COLUMN_USUARIO_ID + " = ? AND " +
                TareaEntry.COLUMN_TITULO + " LIKE ?";
        String[] selectionArgs = {String.valueOf(usuarioId), "%" + texto + "%"};
        String orderBy = TareaEntry.COLUMN_FECHA_CREACION + " DESC";

        try (Cursor cursor = db.query(
                TareaEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, orderBy)) {

            return cursorAListaDeTareas(cursor);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // Helpers privados
    // ------------------------------------------------------------------

    private List<Tarea> cursorAListaDeTareas(Cursor cursor) {
        List<Tarea> tareas = new ArrayList<>();
        while (cursor.moveToNext()) {
            tareas.add(cursorATarea(cursor));
        }
        return tareas;
    }

    /** Mapea la fila actual del cursor a un objeto Tarea. Asume que el cursor está posicionado. */
    private Tarea cursorATarea(Cursor cursor) {
        Tarea tarea = new Tarea();
        tarea.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TareaEntry._ID)));
        tarea.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_TITULO)));
        tarea.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_DESCRIPCION)));
        tarea.setEstado(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_ESTADO)));
        tarea.setFechaVencimiento(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_FECHA_VENCIMIENTO)));
        tarea.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_FECHA_CREACION)));
        tarea.setUsuarioAsignado(cursor.getString(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_USUARIO_ASIGNADO)));
        tarea.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(TareaEntry.COLUMN_USUARIO_ID)));
        return tarea;
    }

    private String obtenerFechaHoyIso() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formato.format(new Date());
    }
}
