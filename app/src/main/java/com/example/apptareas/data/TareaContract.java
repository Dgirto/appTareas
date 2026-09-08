package com.example.apptareas.data;

import android.provider.BaseColumns;

/**
 * Constantes de nombres de tablas y columnas para la base de datos SQLite.
 * Refleja el esquema de la sección 5 del plan de proyecto.
 *
 * NOTA: responsabilidad formal de B1. Esta versión existe para no bloquear
 * el desarrollo del TareaDao (B2); si B1 publica una versión distinta,
 * se reconcilia por PR antes de mergear a develop.
 */
public final class TareaContract {

    // Evita que se instancie
    private TareaContract() {
    }

    /** Tabla usuarios */
    public static final class UsuarioEntry implements BaseColumns {
        public static final String TABLE_NAME = "usuarios";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_CORREO = "correo";
        public static final String COLUMN_PASSWORD_HASH = "password_hash";
        public static final String COLUMN_PROVEEDOR = "proveedor";       // 'local' | 'google'
        public static final String COLUMN_GOOGLE_ID = "google_id";
        public static final String COLUMN_FOTO_URL = "foto_url";
        public static final String COLUMN_FECHA_REGISTRO = "fecha_registro";
    }

    /** Tabla tareas */
    public static final class TareaEntry implements BaseColumns {
        public static final String TABLE_NAME = "tareas";
        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_DESCRIPCION = "descripcion";
        public static final String COLUMN_ESTADO = "estado";
        public static final String COLUMN_FECHA_VENCIMIENTO = "fecha_vencimiento";
        public static final String COLUMN_FECHA_CREACION = "fecha_creacion";
        public static final String COLUMN_USUARIO_ASIGNADO = "usuario_asignado";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
    }

    /** Valores válidos para TareaEntry.COLUMN_ESTADO (siempre en minúsculas). */
    public static final class Estado {
        public static final String PENDIENTE = "pendiente";
        public static final String EN_PROGRESO = "en progreso";
        public static final String COMPLETADA = "completada";
    }
}
