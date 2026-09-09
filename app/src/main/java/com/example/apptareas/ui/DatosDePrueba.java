package com.example.apptareas.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.example.apptareas.data.TareaContract;
import com.example.apptareas.data.TareaDao;
import com.example.apptareas.data.UsuarioDao;
import com.example.apptareas.model.Tarea;
import com.example.apptareas.model.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Siembra una cuenta de demostracion con tareas de ejemplo la primera vez que
 * se abre la app, para poder enseñarla y hacer capturas sin tener que crear
 * todo a mano.
 *
 * Solo se ejecuta en compilaciones de depuracion y solo si esa cuenta no
 * existe todavia: nunca toca los datos de un usuario real ni se repite.
 *
 * Las credenciales estan documentadas en el README.
 */
final class DatosDePrueba {

    static final String CORREO = "demo@doit.pe";
    static final String PASSWORD = "demo1234";
    private static final String NOMBRE = "Demo doit";

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DatosDePrueba() {
        // Clase utilitaria
    }

    static void sembrarSiHaceFalta(Context context) {
        if (!esCompilacionDeDepuracion(context)) {
            return;
        }

        UsuarioDao usuarioDao = new UsuarioDao(context);
        if (usuarioDao.existeCorreo(CORREO)) {
            return;
        }

        long usuarioId = usuarioDao.registrar(
                new Usuario(NOMBRE, CORREO, "local", null), PASSWORD);
        if (usuarioId <= 0) {
            return;
        }

        TareaDao tareaDao = new TareaDao(context);

        insertar(tareaDao, usuarioId,
                "Revisar el informe tecnico",
                "Repasar el informe y las actas antes de la entrega final.",
                TareaContract.Estado.EN_PROGRESO, dentroDe(7), "Mayra Mallqui");

        insertar(tareaDao, usuarioId,
                "Preparar la presentacion de Canva",
                "Incluir las pantallas terminadas y el flujo de autenticacion.",
                TareaContract.Estado.PENDIENTE, dentroDe(3), "Dorian Giron");

        insertar(tareaDao, usuarioId,
                "Registrar la huella SHA-1 en Firebase",
                "Cada integrante debe dar de alta la suya o el login con Google le fallara.",
                TareaContract.Estado.COMPLETADA, null, "Daniel Leon");

        insertar(tareaDao, usuarioId,
                "Probar el APK en un celular real",
                null,
                TareaContract.Estado.PENDIENTE, dentroDe(5), "Piero Llamocca");

        insertar(tareaDao, usuarioId,
                "Documentar el esquema de la base de datos",
                "Explicar cada campo de las tablas usuarios y tareas para el PDF.",
                TareaContract.Estado.EN_PROGRESO, dentroDe(10), "Matias Melendez");
    }

    private static void insertar(TareaDao dao, long usuarioId, String titulo,
                                 String descripcion, String estado,
                                 String vencimientoIso, String asignado) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstado(estado);
        tarea.setFechaVencimiento(vencimientoIso);
        tarea.setUsuarioAsignado(asignado);
        tarea.setUsuarioId(usuarioId);
        dao.insertar(tarea);
    }

    private static String dentroDe(int dias) {
        return LocalDate.now().plusDays(dias).format(ISO);
    }

    /**
     * Se mira la bandera del propio paquete en vez de BuildConfig porque el
     * proyecto no tiene activada la generacion de BuildConfig.
     */
    private static boolean esCompilacionDeDepuracion(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
