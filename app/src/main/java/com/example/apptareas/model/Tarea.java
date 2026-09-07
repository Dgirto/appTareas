package com.example.apptareas.model;

import java.util.Objects;

public class Tarea {

    private long id;
    private String titulo;
    private String descripcion;
    private String fechaCreacion;
    private String fechaVencimiento;
    private EstadoTarea estado;

    public Tarea() {
    }

    public Tarea(long id, String titulo, String descripcion, String fechaCreacion, String fechaVencimiento, EstadoTarea estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public Tarea(String titulo, String descripcion, String fechaCreacion, String fechaVencimiento, EstadoTarea estado) {
        this(0, titulo, descripcion, fechaCreacion, fechaVencimiento, estado);
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

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id &&
                Objects.equals(titulo, tarea.titulo) &&
                Objects.equals(descripcion, tarea.descripcion) &&
                Objects.equals(fechaCreacion, tarea.fechaCreacion) &&
                Objects.equals(fechaVencimiento, tarea.fechaVencimiento) &&
                estado == tarea.estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, descripcion, fechaCreacion, fechaVencimiento, estado);
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion='" + fechaCreacion + '\'' +
                ", fechaVencimiento='" + fechaVencimiento + '\'' +
                ", estado=" + estado +
                '}';
    }
}
