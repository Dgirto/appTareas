package com.example.apptareas.util;

import com.example.apptareas.model.EstadoTarea;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidacionesTest {

    @Test
    public void validarTitulo_conTituloVacioONulo_retornaError() {
        Resultado<Void> resNull = Validaciones.validarTitulo(null);
        assertFalse(resNull.esExitoso());
        assertEquals("El título es obligatorio.", resNull.getMensaje());

        Resultado<Void> resVacio = Validaciones.validarTitulo("   ");
        assertFalse(resVacio.esExitoso());
        assertEquals("El título es obligatorio.", resVacio.getMensaje());
    }

    @Test
    public void validarTitulo_conTituloValido_retornaExito() {
        Resultado<Void> res = Validaciones.validarTitulo("Comprar leche");
        assertTrue(res.esExitoso());
        assertNull(res.getMensaje());
    }

    @Test
    public void validarFechaVencimiento_conFechaVencimientoNulaOVacia_retornaExito() {
        Resultado<Void> resNull = Validaciones.validarFechaVencimiento("2026-09-01", null);
        assertTrue(resNull.esExitoso());

        Resultado<Void> resVacio = Validaciones.validarFechaVencimiento("2026-09-01", "  ");
        assertTrue(resVacio.esExitoso());
    }

    @Test
    public void validarFechaVencimiento_conFechaCreacionVacia_retornaError() {
        Resultado<Void> res = Validaciones.validarFechaVencimiento("", "2026-09-10");
        assertFalse(res.esExitoso());
        assertEquals("La fecha de creación es obligatoria.", res.getMensaje());
    }

    @Test
    public void validarFechaVencimiento_anteriorACreacion_retornaError() {
        Resultado<Void> res = Validaciones.validarFechaVencimiento("2026-09-10", "2026-09-05");
        assertFalse(res.esExitoso());
        assertEquals("La fecha de vencimiento no puede ser anterior a la fecha de creación.", res.getMensaje());
    }

    @Test
    public void validarFechaVencimiento_igualOPosteriorACreacion_retornaExito() {
        Resultado<Void> resIgual = Validaciones.validarFechaVencimiento("2026-09-10", "2026-09-10");
        assertTrue(resIgual.esExitoso());

        Resultado<Void> resPosterior = Validaciones.validarFechaVencimiento("2026-09-10", "2026-09-15");
        assertTrue(resPosterior.esExitoso());
    }

    @Test
    public void validarFechaVencimiento_conFormatoInvalido_retornaError() {
        Resultado<Void> res = Validaciones.validarFechaVencimiento("invalid", "2026-09-10");
        assertFalse(res.esExitoso());
        assertEquals("Las fechas no tienen un formato válido.", res.getMensaje());
    }

    @Test
    public void esTransicionEstadoValida_dePendiente() {
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.PENDIENTE, EstadoTarea.PENDIENTE));
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.PENDIENTE, EstadoTarea.EN_PROGRESO));
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.PENDIENTE, EstadoTarea.COMPLETADA));
    }

    @Test
    public void esTransicionEstadoValida_deEnProgreso() {
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.EN_PROGRESO, EstadoTarea.PENDIENTE));
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.EN_PROGRESO, EstadoTarea.EN_PROGRESO));
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.EN_PROGRESO, EstadoTarea.COMPLETADA));
    }

    @Test
    public void esTransicionEstadoValida_deCompletada() {
        assertTrue(Validaciones.esTransicionEstadoValida(EstadoTarea.COMPLETADA, EstadoTarea.COMPLETADA));
        assertFalse(Validaciones.esTransicionEstadoValida(EstadoTarea.COMPLETADA, EstadoTarea.PENDIENTE));
        assertFalse(Validaciones.esTransicionEstadoValida(EstadoTarea.COMPLETADA, EstadoTarea.EN_PROGRESO));
    }

    @Test
    public void esTransicionEstadoValida_conNulos() {
        assertFalse(Validaciones.esTransicionEstadoValida(null, EstadoTarea.PENDIENTE));
        assertFalse(Validaciones.esTransicionEstadoValida(EstadoTarea.PENDIENTE, null));
    }

    @Test
    public void validarTransicionEstado_transicionInvalida_retornaError() {
        Resultado<Void> res = Validaciones.validarTransicionEstado(EstadoTarea.COMPLETADA, EstadoTarea.PENDIENTE);
        assertFalse(res.esExitoso());
        assertEquals("Transición de estado no válida.", res.getMensaje());
    }
}
