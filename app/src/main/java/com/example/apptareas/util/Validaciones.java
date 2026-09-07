package com.example.apptareas.util;

import com.example.apptareas.model.EstadoTarea;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Validaciones {

    private static final DateTimeFormatter FORMATO_ISO =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Validaciones() {
        // Clase utilitaria
    }

    public static Resultado<Void> validarTitulo(String titulo) {

        if (titulo == null || titulo.trim().isEmpty()) {
            return Resultado.error("El título es obligatorio.");
        }

        return Resultado.exito(null);
    }

    public static Resultado<Void> validarFechaVencimiento(
            String fechaCreacion,
            String fechaVencimiento
    ) {

        if (fechaVencimiento == null ||
                fechaVencimiento.trim().isEmpty()) {

            return Resultado.exito(null);
        }

        if (fechaCreacion == null ||
                fechaCreacion.trim().isEmpty()) {

            return Resultado.error(
                    "La fecha de creación es obligatoria."
            );
        }

        try {

            LocalDate creacion =
                    LocalDate.parse(fechaCreacion, FORMATO_ISO);

            LocalDate vencimiento =
                    LocalDate.parse(fechaVencimiento, FORMATO_ISO);

            if (vencimiento.isBefore(creacion)) {
                return Resultado.error(
                        "La fecha de vencimiento no puede ser anterior a la fecha de creación."
                );
            }

            return Resultado.exito(null);

        } catch (Exception e) {

            return Resultado.error(
                    "Las fechas no tienen un formato válido."
            );
        }
    }

    public static Resultado<Void> validarFechas(
            String fechaCreacion,
            String fechaVencimiento
    ) {
        return validarFechaVencimiento(
                fechaCreacion,
                fechaVencimiento
        );
    }

    public static boolean esTransicionEstadoValida(
            EstadoTarea actual,
            EstadoTarea nuevo
    ) {
        if (actual == null || nuevo == null) {
            return false;
        }

        if (actual == nuevo) {
            return true;
        }

        switch (actual) {
            case PENDIENTE:
                return nuevo == EstadoTarea.EN_PROGRESO
                        || nuevo == EstadoTarea.COMPLETADA;

            case EN_PROGRESO:
                return nuevo == EstadoTarea.PENDIENTE
                        || nuevo == EstadoTarea.COMPLETADA;

            case COMPLETADA:
                return false;

            default:
                return false;
        }
    }

    public static Resultado<Void> validarTransicionEstado(
            EstadoTarea actual,
            EstadoTarea nuevo
    ) {
        if (!esTransicionEstadoValida(actual, nuevo)) {
            return Resultado.error(
                    "Transición de estado no válida."
            );
        }

        return Resultado.exito(null);
    }
}
