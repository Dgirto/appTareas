package com.example.apptareas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.apptareas.data.TareaContract.UsuarioEntry;
import com.example.apptareas.model.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * DAO de acceso a la tabla "usuarios".
 * Responsable formal: B3 (autenticación y usuarios).
 *
 * Firma tomada del contrato compartido (PLAN_PROYECTO_appTareas, sección 6).
 * Las contraseñas nunca se guardan en claro: se guarda "saltHex:hashHex"
 * (SHA-256 del password + salt) en la columna password_hash, tal como
 * pide la sección 5 del plan. Las cuentas de Google no usan password_hash
 * (queda NULL) y en su lugar guardan google_id.
 */
public class UsuarioDao {

    private final DatabaseHelper dbHelper;

    private static final String[] TODAS_LAS_COLUMNAS = {
            UsuarioEntry._ID,
            UsuarioEntry.COLUMN_NOMBRE,
            UsuarioEntry.COLUMN_CORREO,
            UsuarioEntry.COLUMN_PROVEEDOR,
            UsuarioEntry.COLUMN_FOTO_URL
    };

    public UsuarioDao(Context context) {
        this.dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    // ------------------------------------------------------------------
    // REGISTRO
    // ------------------------------------------------------------------

    /**
     * Registra un usuario local (correo + contraseña). El password se hashea
     * con SHA-256 + salt aleatorio antes de guardarse.
     *
     * @return el id autogenerado, o -1 si falló (por ejemplo, correo duplicado).
     */
    public long registrar(Usuario usuario, String passwordPlano) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String passwordHash = hashearPassword(passwordPlano);

            ContentValues values = new ContentValues();
            values.put(UsuarioEntry.COLUMN_NOMBRE, usuario.getNombre());
            values.put(UsuarioEntry.COLUMN_CORREO, usuario.getCorreo());
            values.put(UsuarioEntry.COLUMN_PASSWORD_HASH, passwordHash);
            values.put(UsuarioEntry.COLUMN_PROVEEDOR, "local");
            values.putNull(UsuarioEntry.COLUMN_GOOGLE_ID);
            values.put(UsuarioEntry.COLUMN_FOTO_URL, usuario.getFotoUrl());
            values.put(UsuarioEntry.COLUMN_FECHA_REGISTRO, obtenerFechaHoyIso());

            // insert() ya devuelve -1 si viola la restricción UNIQUE de correo.
            return db.insert(UsuarioEntry.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    /**
     * Registra (o vincula) un usuario que inició sesión con Google.
     * password_hash queda NULL: esta cuenta nunca usa login local.
     *
     * @return el id autogenerado, o -1 si falló (por ejemplo, googleId o correo duplicado).
     */
    public long registrarConGoogle(Usuario usuario, String googleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(UsuarioEntry.COLUMN_NOMBRE, usuario.getNombre());
            values.put(UsuarioEntry.COLUMN_CORREO, usuario.getCorreo());
            values.putNull(UsuarioEntry.COLUMN_PASSWORD_HASH);
            values.put(UsuarioEntry.COLUMN_PROVEEDOR, "google");
            values.put(UsuarioEntry.COLUMN_GOOGLE_ID, googleId);
            values.put(UsuarioEntry.COLUMN_FOTO_URL, usuario.getFotoUrl());
            values.put(UsuarioEntry.COLUMN_FECHA_REGISTRO, obtenerFechaHoyIso());

            return db.insert(UsuarioEntry.TABLE_NAME, null, values);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // CONSULTAS
    // ------------------------------------------------------------------

    /** Busca un usuario por correo. Devuelve null si no existe. */
    public Usuario buscarPorCorreo(String correo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = UsuarioEntry.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {correo};

        try (Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursorAUsuario(cursor);
            }
            return null;
        } finally {
            db.close();
        }
    }

    /** Busca un usuario por su google_id. Devuelve null si no existe. */
    public Usuario buscarPorGoogleId(String googleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = UsuarioEntry.COLUMN_GOOGLE_ID + " = ?";
        String[] selectionArgs = {googleId};

        try (Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                TODAS_LAS_COLUMNAS,
                selection,
                selectionArgs,
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return cursorAUsuario(cursor);
            }
            return null;
        } finally {
            db.close();
        }
    }

    /** true si ya existe un usuario registrado con ese correo. */
    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = UsuarioEntry.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {correo};

        try (Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry._ID},
                selection,
                selectionArgs,
                null, null, null)) {

            return cursor.getCount() > 0;
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // Verificación de contraseña (helper para AuthManager)
    // ------------------------------------------------------------------

    /**
     * Compara una contraseña en texto plano contra el hash guardado
     * (formato "saltHex:hashHex"). Pensado para que AuthManager.iniciarSesion()
     * lo use tras encontrar al usuario con buscarPorCorreo().
     *
     * NOTA: esta consulta trae el password_hash directamente porque
     * TODAS_LAS_COLUMNAS no lo incluye (para no exponerlo por accidente
     * en pantallas que solo necesitan mostrar datos del usuario).
     */
    public boolean verificarPassword(String correo, String passwordPlano) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = UsuarioEntry.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {correo};

        try (Cursor cursor = db.query(
                UsuarioEntry.TABLE_NAME,
                new String[]{UsuarioEntry.COLUMN_PASSWORD_HASH},
                selection,
                selectionArgs,
                null, null, null)) {

            if (!cursor.moveToFirst()) {
                return false;
            }
            String hashGuardado = cursor.getString(
                    cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_PASSWORD_HASH));
            if (hashGuardado == null) {
                // Cuenta de Google, no tiene password local.
                return false;
            }
            return coincideConHash(passwordPlano, hashGuardado);
        } finally {
            db.close();
        }
    }

    // ------------------------------------------------------------------
    // Helpers privados
    // ------------------------------------------------------------------

    private Usuario cursorAUsuario(Cursor cursor) {
        Usuario usuario = new Usuario();
        usuario.setId(cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioEntry._ID)));
        usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_NOMBRE)));
        usuario.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_CORREO)));
        usuario.setProveedor(cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_PROVEEDOR)));
        usuario.setFotoUrl(cursor.getString(cursor.getColumnIndexOrThrow(UsuarioEntry.COLUMN_FOTO_URL)));
        return usuario;
    }

    private String obtenerFechaHoyIso() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formato.format(new Date());
    }

    /** Genera "saltHex:hashHex" a partir de una contraseña en texto plano. */
    private String hashearPassword(String passwordPlano) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        byte[] hash = sha256(passwordPlano, salt);
        return bytesAHex(salt) + ":" + bytesAHex(hash);
    }

    /** Verifica si passwordPlano corresponde al "saltHex:hashHex" guardado. */
    private boolean coincideConHash(String passwordPlano, String saltYHashGuardado) {
        String[] partes = saltYHashGuardado.split(":");
        if (partes.length != 2) {
            return false;
        }
        byte[] salt = hexABytes(partes[0]);
        byte[] hashEsperado = hexABytes(partes[1]);
        byte[] hashCalculado = sha256(passwordPlano, salt);
        return MessageDigest.isEqual(hashEsperado, hashCalculado);
    }

    private byte[] sha256(String texto, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(texto.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            // SHA-256 y UTF-8 siempre existen en Android; esto no debería pasar nunca.
            throw new RuntimeException("No se pudo calcular el hash de la contraseña", e);
        }
    }

    private String bytesAHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format(Locale.US, "%02x", b));
        }
        return sb.toString();
    }

    private byte[] hexABytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
