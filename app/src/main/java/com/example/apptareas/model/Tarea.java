package com.example.apptareas.model;

public class Tarea {
    private long id;
    private String titulo;
    private String descripcion;
    private String estado;          // "pendiente" | "en progreso" | "completada"
    private String fechaVencimiento;
    private String fechaCreacion;
    private String usuarioAsignado; // Campo de texto libre del enunciado
    private long usuarioId;        // Relación con el usuario que la creó

    public Tarea() {
        this.estado = EstadoTarea.PENDIENTE.getValor();
    }

    public Tarea(long id, String titulo, String descripcion, String estado,
                 String fechaVencimiento, String fechaCreacion,
                 String usuarioAsignado, long usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaCreacion = fechaCreacion;
        this.usuarioAsignado = usuarioAsignado;
        this.usuarioId = usuarioId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioAsignado() {
        return usuarioAsignado;
    }

    public void setUsuarioAsignado(String usuarioAsignado) {
        this.usuarioAsignado = usuarioAsignado;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }
}