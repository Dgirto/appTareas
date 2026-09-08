package com.example.apptareas.util;

public class Resultado<T> {

    private final boolean exito;
    private final T datos;
    private final String mensaje;

    private Resultado(boolean exito, T datos, String mensaje) {
        this.exito = exito;
        this.datos = datos;
        this.mensaje = mensaje;
    }

    public static <T> Resultado<T> exito(T datos) {
        return new Resultado<>(true, datos, null);
    }

    public static <T> Resultado<T> error(String mensaje) {
        return new Resultado<>(false, null, mensaje);
    }

    public boolean esExitoso() {
        return exito;
    }

    public T getDatos() {
        return datos;
    }

    public String getMensaje() {
        return mensaje;
    }
}
