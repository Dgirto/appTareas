package com.example.apptareas.model;

import java.util.Objects;

/**
 * POJO que representa una tarea.
 *
 * Firma acordada en el contrato compartido (PLAN_PROYECTO_appTareas, sección 6).
 */
public class Tarea {
    private long id;
    private String titulo;
    private String descripcion;
    private String estado;              // "pendiente" | "en progreso" | "completada"
    private String fechaVencimiento;    // ISO yyyy-MM-dd, puede ser null
    private String fechaCreacion;       // ISO yyyy-MM-dd, la asigna el DAO
    private String usuarioAsignado;     // texto libre, puede ser null
    private long usuarioId;             // FK real hacia usuarios.id

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

    /** Constructor de conveniencia para crear una tarea nueva (sin id ni fechaCreacion todavía). */
    public Tarea(String titulo, String descripcion, String estado,
                 String fechaVencimiento, String usuarioAsignado, long usuarioId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaVencimiento = fechaVencimiento;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id &&
                usuarioId == tarea.usuarioId &&
                Objects.equals(titulo, tarea.titulo) &&
                Objects.equals(descripcion, tarea.descripcion) &&
                Objects.equals(estado, tarea.estado) &&
                Objects.equals(fechaVencimiento, tarea.fechaVencimiento) &&
                Objects.equals(fechaCreacion, tarea.fechaCreacion) &&
                Objects.equals(usuarioAsignado, tarea.usuarioAsignado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, descripcion, estado, fechaVencimiento, fechaCreacion, usuarioAsignado, usuarioId);
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaVencimiento='" + fechaVencimiento + '\'' +
                ", usuarioId=" + usuarioId +
                '}';
    }
}