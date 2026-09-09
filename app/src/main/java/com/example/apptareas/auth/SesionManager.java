package com.example.apptareas.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.apptareas.model.Usuario;

/**
 * Guarda quien es el usuario activo entre aperturas de la app.
 *
 * Firma acordada en el contrato compartido (PLAN_PROYECTO_appTareas, seccion 6).
 * Responsable formal: B3.
 *
 * Solo persiste datos no sensibles del usuario. La contrasena nunca sale de
 * la tabla usuarios, donde UsuarioDao la guarda hasheada.
 */
public class SesionManager {

    private static final String PREFS = "sesion_apptareas";

    private static final String KEY_ID = "usuario_id";
    private static final String KEY_NOMBRE = "usuario_nombre";
    private static final String KEY_CORREO = "usuario_correo";
    private static final String KEY_PROVEEDOR = "usuario_proveedor";
    private static final String KEY_FOTO = "usuario_foto";

    private final SharedPreferences prefs;

    public SesionManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardarSesion(Usuario usuario) {
        if (usuario == null) {
            limpiar();
            return;
        }

        prefs.edit()
                .putLong(KEY_ID, usuario.getId())
                .putString(KEY_NOMBRE, usuario.getNombre())
                .putString(KEY_CORREO, usuario.getCorreo())
                .putString(KEY_PROVEEDOR, usuario.getProveedor())
                .putString(KEY_FOTO, usuario.getFotoUrl())
                .apply();
    }

    /** Devuelve el usuario activo, o null si no hay sesion. */
    public Usuario obtenerUsuarioActivo() {
        if (!haySesionActiva()) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(prefs.getLong(KEY_ID, 0L));
        usuario.setNombre(prefs.getString(KEY_NOMBRE, null));
        usuario.setCorreo(prefs.getString(KEY_CORREO, null));
        usuario.setProveedor(prefs.getString(KEY_PROVEEDOR, "local"));
        usuario.setFotoUrl(prefs.getString(KEY_FOTO, null));
        return usuario;
    }

    public boolean haySesionActiva() {
        return prefs.contains(KEY_ID);
    }

    public void limpiar() {
        prefs.edit().clear().apply();
    }

    /**
     * Atajo para la capa de UI: el id que TareaDao necesita en todas sus
     * consultas. Devuelve 0 si no hay sesion.
     */
    public long usuarioIdActivo() {
        return prefs.getLong(KEY_ID, 0L);
    }
}
