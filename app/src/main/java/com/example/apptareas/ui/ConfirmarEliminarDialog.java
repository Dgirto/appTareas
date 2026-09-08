package com.example.apptareas.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.apptareas.R;

/**
 * Dialogo de confirmacion antes de borrar una tarea (pantalla 04 del diseno).
 * Infla dialog_confirmar_eliminar.xml y pone el fondo transparente para que
 * se vea el radio de 28dp del drawable bg_dialogo.
 */
final class ConfirmarEliminarDialog {

    /** Se avisa solo cuando el usuario confirma; cancelar no hace nada. */
    interface OnConfirmarListener {
        void onConfirmar();
    }

    private ConfirmarEliminarDialog() {
        // Clase utilitaria
    }

    static void mostrar(Activity actividad, String tituloTarea, OnConfirmarListener listener) {
        View vista = LayoutInflater.from(actividad)
                .inflate(R.layout.dialog_confirmar_eliminar, null);

        TextView tvMensaje = vista.findViewById(R.id.tvMensaje);
        TextView btnConfirmar = vista.findViewById(R.id.btnConfirmar);
        TextView btnCancelar = vista.findViewById(R.id.btnCancelar);

        tvMensaje.setText(actividad.getString(R.string.eliminar_mensaje, tituloTarea));

        AlertDialog dialogo = new AlertDialog.Builder(actividad)
                .setView(vista)
                .create();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnConfirmar.setOnClickListener(v -> {
            dialogo.dismiss();
            if (listener != null) {
                listener.onConfirmar();
            }
        });
        btnCancelar.setOnClickListener(v -> dialogo.dismiss());

        dialogo.show();
    }
}
