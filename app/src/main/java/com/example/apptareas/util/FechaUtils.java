package com.example.apptareas.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class FechaUtils {

    private static final DateTimeFormatter FORMATO_ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter FORMATO_UI =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private FechaUtils() {
        // Clase utilitaria
    }

    public static String isoAUi(String fechaIso) {
        if (fechaIso == null || fechaIso.trim().isEmpty()) {
            return "";
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaIso, FORMATO_ISO);
            return fecha.format(FORMATO_UI);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static String uiAIso(String fechaUi) {
        if (fechaUi == null || fechaUi.trim().isEmpty()) {
            return "";
        }

        try {
            LocalDate fecha = LocalDate.parse(fechaUi, FORMATO_UI);
            return fecha.format(FORMATO_ISO);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    public static boolean esFechaIsoValida(String fechaIso) {
        if (fechaIso == null || fechaIso.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(fechaIso, FORMATO_ISO);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static void mostrarDatePicker(
            Context context,
            EditText campoFecha
    ) {
        String fechaActual = campoFecha.getText().toString().trim();
        LocalDate fechaInicial;

        if (!fechaActual.isEmpty()) {
            try {
                String iso = esFechaIsoValida(fechaActual) ? fechaActual : uiAIso(fechaActual);
                fechaInicial = LocalDate.parse(iso, FORMATO_ISO);
            } catch (Exception ignored) {
                fechaInicial = LocalDate.now();
            }
        } else {
            fechaInicial = LocalDate.now();
        }

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {

                    LocalDate fechaSeleccionada =
                            LocalDate.of(year, month + 1, dayOfMonth);

                    campoFecha.setText(
                            fechaSeleccionada.format(FORMATO_UI)
                    );
                },
                fechaInicial.getYear(),
                fechaInicial.getMonthValue() - 1,
                fechaInicial.getDayOfMonth()
        );

        dialog.show();
    }

    public static String hoyIso() {
        return LocalDate.now().format(FORMATO_ISO);
    }
}
