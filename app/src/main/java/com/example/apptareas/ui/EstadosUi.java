package com.example.apptareas.ui;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.example.apptareas.R;
import com.example.apptareas.model.EstadoTarea;

/**
 * Traduce un EstadoTarea a los recursos que le corresponden en pantalla.
 * Vive en ui/ porque es decision de presentacion, no de negocio: el backend
 * solo conoce las cadenas "pendiente", "en progreso" y "completada".
 */
final class EstadosUi {

    private EstadosUi() {
        // Clase utilitaria
    }

    /** Etiqueta en singular, para la tarjeta y el detalle. */
    @StringRes
    static int textoDe(EstadoTarea estado) {
        switch (estado) {
            case EN_PROGRESO:
                return R.string.estado_progreso_txt;
            case COMPLETADA:
                return R.string.estado_completada_txt;
            case PENDIENTE:
            default:
                return R.string.estado_pendiente_txt;
        }
    }

    /** Color del punto que acompana al estado. */
    @ColorRes
    static int colorDe(EstadoTarea estado) {
        switch (estado) {
            case EN_PROGRESO:
                return R.color.estado_progreso;
            case COMPLETADA:
                return R.color.estado_completada;
            case PENDIENTE:
            default:
                return R.color.estado_pendiente;
        }
    }
}
