package com.example.apptareas.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class FechaUtilsTest {

    @Test
    public void isoAUi_conversionCorrecta() {
        assertEquals("15/09/2026", FechaUtils.isoAUi("2026-09-15"));
    }

    @Test
    public void isoAUi_conEntradaInvalida_retornaVacio() {
        assertEquals("", FechaUtils.isoAUi(null));
        assertEquals("", FechaUtils.isoAUi(""));
        assertEquals("", FechaUtils.isoAUi("15-09-2026"));
    }

    @Test
    public void uiAIso_conversionCorrecta() {
        assertEquals("2026-09-15", FechaUtils.uiAIso("15/09/2026"));
    }

    @Test
    public void uiAIso_conEntradaInvalida_retornaVacio() {
        assertEquals("", FechaUtils.uiAIso(null));
        assertEquals("", FechaUtils.uiAIso(""));
        assertEquals("", FechaUtils.uiAIso("2026-09-15"));
    }

    @Test
    public void esFechaIsoValida_retornaResultadoEsperado() {
        assertTrue(FechaUtils.esFechaIsoValida("2026-09-15"));
        assertFalse(FechaUtils.esFechaIsoValida("15/09/2026"));
        assertFalse(FechaUtils.esFechaIsoValida("2026-13-45"));
        assertFalse(FechaUtils.esFechaIsoValida(null));
        assertFalse(FechaUtils.esFechaIsoValida(""));
    }

    @Test
    public void hoyIso_retornaFechaFormatoIso() {
        String hoy = FechaUtils.hoyIso();
        assertNotNull(hoy);
        assertTrue(FechaUtils.esFechaIsoValida(hoy));
    }
}
