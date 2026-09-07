package com.example.apptareas.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResultadoTest {

    @Test
    public void exito_creaResultadoExitoso() {
        Resultado<String> resultado = Resultado.exito("ok");
        assertTrue(resultado.esExitoso());
        assertEquals("ok", resultado.getDatos());
        assertNull(resultado.getMensaje());
    }

    @Test
    public void error_creaResultadoConError() {
        Resultado<String> resultado = Resultado.error("Error al procesar");
        assertFalse(resultado.esExitoso());
        assertNull(resultado.getDatos());
        assertEquals("Error al procesar", resultado.getMensaje());
    }
}
