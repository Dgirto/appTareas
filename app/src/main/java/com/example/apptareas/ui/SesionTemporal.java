package com.example.apptareas.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ANDAMIAJE TEMPORAL DEL FRONTEND. No es la solucion definitiva.
 *
 * TareaDao exige un usuarioId en todos sus metodos, pero auth/SesionManager
 * (responsabilidad de B3, seccion 8 del plan) todavia no existe. Sin algo que
 * guarde quien es el usuario activo, la lista no puede pedir sus tareas y el
 * frontend queda bloqueado.
 *
 * Esta clase cubre ese hueco con lo minimo imprescindible y nada mas.
 * En cuanto B3 entregue auth/SesionManager hay que BORRAR este archivo y
 * sustituir sus llamadas, que estan localizadas en las cinco Activities.
 */
final class SesionTemporal {

    private static final String PREFS = "sesion_apptareas";
    private static final String KEY_USUARIO_ID = "usuario_id";
    private static final String KEY_NOMBRE = "usuario_nombre";

    /** Id que se usa mientras se navega como invitado. */
    static final long ID_INVITADO = 0L;

    private SesionTemporal() {
        // Clase utilitaria
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static void guardar(Context context, long usuarioId, String nombre) {
        prefs(context).edit()
                .putLong(KEY_USUARIO_ID, usuarioId)
                .putString(KEY_NOMBRE, nombre)
                .apply();
    }

    static long usuarioId(Context context) {
        return prefs(context).getLong(KEY_USUARIO_ID, ID_INVITADO);
    }

    static String nombre(Context context) {
        return prefs(context).getString(KEY_NOMBRE, null);
    }

    static boolean haySesion(Context context) {
        return prefs(context).contains(KEY_USUARIO_ID);
    }

    static void limpiar(Context context) {
        prefs(context).edit().clear().apply();
    }
}
