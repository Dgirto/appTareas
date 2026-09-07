package com.example.apptareas.model;

public class Usuario {
    private long id;
    private String nombre;
    private String correo;
    private String passwordHash;
    private String proveedor;    // "local" | "google"
    private String googleId;
    private String fotoUrl;
    private String fechaRegistro;

    public Usuario() {
        this.proveedor = "local";
    }

    public Usuario(long id, String nombre, String correo, String passwordHash,
                   String proveedor, String googleId, String fotoUrl, String fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.proveedor = proveedor;
        this.googleId = googleId;
        this.fotoUrl = fotoUrl;
        this.fechaRegistro = fechaRegistro;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}