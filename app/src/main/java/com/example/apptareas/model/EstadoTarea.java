package com.example.apptareas.model;

public enum EstadoTarea {
    PENDIENTE("pendiente"),
    EN_PROGRESO("en progreso"),
    COMPLETADA("completada");

    private final String valor;

    EstadoTarea(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoTarea desdeString(String texto) {
        if (texto == null) return PENDIENTE;
        for (EstadoTarea e : EstadoTarea.values()) {
            if (e.valor.equalsIgnoreCase(texto)) {
                return e;
            }
        }
        return PENDIENTE;
    }
}