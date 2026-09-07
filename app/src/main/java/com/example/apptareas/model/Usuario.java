package com.example.apptareas.model;

/**
 * POJO que representa un usuario.
 * Firma acordada en el contrato compartido (PLAN_PROYECTO_appTareas, sección 6).
 * Responsable formal: B3.
 */
public class Usuario {

    private long id;
    private String nombre;
    private String correo;
    private String proveedor;   // "local" | "google"
    private String fotoUrl;     // puede ser null en cuentas locales

    public Usuario() {
    }

    public Usuario(long id, String nombre, String correo, String proveedor, String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.proveedor = proveedor;
        this.fotoUrl = fotoUrl;
    }

    /** Constructor de conveniencia para registrar un usuario nuevo (sin id todavía). */
    public Usuario(String nombre, String correo, String proveedor, String fotoUrl) {
        this.nombre = nombre;
        this.correo = correo;
        this.proveedor = proveedor;
        this.fotoUrl = fotoUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", proveedor='" + proveedor + '\'' +
                '}';
    }
}
