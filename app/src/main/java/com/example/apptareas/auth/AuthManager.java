package com.example.apptareas.auth;

import android.content.Context;
import android.util.Base64;

import com.example.apptareas.data.UsuarioDao;
import com.example.apptareas.model.Usuario;
import com.example.apptareas.util.Resultado;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Registro, inicio de sesion y cierre de sesion.
 *
 * Firma acordada en el contrato compartido (PLAN_PROYECTO_appTareas, seccion 6).
 * Responsable formal: B3.
 *
 * Se apoya en data/UsuarioDao, que es quien habla con SQLite y quien hashea las
 * contrasenas. Esta clase solo valida entradas y traduce el resultado a
 * Resultado<Usuario>, para que la UI pueda mostrar el motivo de un fallo.
 */
public class AuthManager {

    private static final int LARGO_MINIMO_PASSWORD = 6;

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /** Cuenta local de respaldo para el acceso sin credenciales. */
    private static final String CORREO_INVITADO = "invitado@apptareas.local";
    private static final String NOMBRE_INVITADO = "Invitado";

    private final UsuarioDao usuarioDao;
    private final SesionManager sesionManager;

    public AuthManager(Context context) {
        this.usuarioDao = new UsuarioDao(context);
        this.sesionManager = new SesionManager(context);
    }

    // ------------------------------------------------------------------
    // LOGIN LOCAL
    // ------------------------------------------------------------------

    public Resultado<Usuario> iniciarSesion(String correo, String password) {
        String correoLimpio = correo == null ? "" : correo.trim();

        if (correoLimpio.isEmpty() || password == null || password.isEmpty()) {
            return Resultado.error("Completa el correo y la contrasena.");
        }

        if (!usuarioDao.verificarPassword(correoLimpio, password)) {
            return Resultado.error("Correo o contrasena incorrectos.");
        }

        Usuario usuario = usuarioDao.buscarPorCorreo(correoLimpio);
        if (usuario == null) {
            return Resultado.error("Correo o contrasena incorrectos.");
        }

        sesionManager.guardarSesion(usuario);
        return Resultado.exito(usuario);
    }

    // ------------------------------------------------------------------
    // REGISTRO LOCAL
    // ------------------------------------------------------------------

    public Resultado<Usuario> registrar(String nombre, String correo, String password) {
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        String correoLimpio = correo == null ? "" : correo.trim();

        if (nombreLimpio.isEmpty() || correoLimpio.isEmpty()
                || password == null || password.isEmpty()) {
            return Resultado.error("Completa todos los campos.");
        }

        if (!PATRON_CORREO.matcher(correoLimpio).matches()) {
            return Resultado.error("El correo no tiene un formato valido.");
        }

        if (password.length() < LARGO_MINIMO_PASSWORD) {
            return Resultado.error(
                    "La contrasena debe tener al menos " + LARGO_MINIMO_PASSWORD + " caracteres.");
        }

        if (usuarioDao.existeCorreo(correoLimpio)) {
            return Resultado.error("Ese correo ya esta registrado.");
        }

        Usuario nuevo = new Usuario(nombreLimpio, correoLimpio, "local", null);
        long id = usuarioDao.registrar(nuevo, password);
        if (id <= 0) {
            return Resultado.error("No se pudo crear la cuenta.");
        }

        nuevo.setId(id);
        return Resultado.exito(nuevo);
    }

    // ------------------------------------------------------------------
    // LOGIN CON GOOGLE
    // ------------------------------------------------------------------

    /**
     * Da por buena una cuenta de Google a partir del idToken y, si es la primera
     * vez, la crea automaticamente.
     *
     * PENDIENTE: quien obtiene el idToken es auth/GoogleAuthClient, que necesita
     * el proyecto de Firebase, la SHA-1 y google-services.json (seccion 7 del
     * plan). Hasta que eso exista, este metodo no lo llama nadie desde la UI.
     *
     * AVISO: aqui solo se lee el payload del token; NO se verifica su firma.
     * Para una app local basta, pero si algun dia hay servidor, la validacion
     * tiene que hacerse alli contra las claves publicas de Google.
     */
    public Resultado<Usuario> iniciarSesionConGoogle(String idToken) {
        if (idToken == null || idToken.trim().isEmpty()) {
            return Resultado.error("No se recibio el token de Google.");
        }

        JSONObject payload = leerPayload(idToken);
        if (payload == null) {
            return Resultado.error("El token de Google no es valido.");
        }

        String googleId = payload.optString("sub", "");
        String correo = payload.optString("email", "");
        if (googleId.isEmpty() || correo.isEmpty()) {
            return Resultado.error("El token de Google no trae los datos esperados.");
        }

        String nombre = payload.optString("name", correo);
        String fotoUrl = payload.optString("picture", null);

        Usuario usuario = usuarioDao.buscarPorGoogleId(googleId);

        if (usuario == null) {
            if (usuarioDao.existeCorreo(correo)) {
                return Resultado.error(
                        "Ese correo ya tiene una cuenta local. Inicia sesion con tu contrasena.");
            }

            Usuario nuevo = new Usuario(nombre, correo, "google", fotoUrl);
            long id = usuarioDao.registrarConGoogle(nuevo, googleId);
            if (id <= 0) {
                return Resultado.error("No se pudo crear la cuenta de Google.");
            }
            nuevo.setId(id);
            usuario = nuevo;
        }

        sesionManager.guardarSesion(usuario);
        return Resultado.exito(usuario);
    }

    /** Decodifica el segundo segmento del JWT, que es JSON en base64url. */
    private JSONObject leerPayload(String idToken) {
        try {
            String[] partes = idToken.split("\\.");
            if (partes.length < 2) {
                return null;
            }
            byte[] json = Base64.decode(
                    partes[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return new JSONObject(new String(json, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // INVITADO
    // ------------------------------------------------------------------

    /**
     * Entra sin credenciales, para el boton "Continuar como invitado" del diseno.
     *
     * No basta con un id ficticio: tareas.usuario_id es clave foranea contra
     * usuarios(_id) y el esquema activa PRAGMA foreign_keys, asi que insertar
     * tareas con un id inexistente falla con SQLITE_CONSTRAINT. Por eso el
     * invitado se respalda en una cuenta local real, creada una sola vez.
     *
     * No esta en el contrato de la seccion 6: se anade porque el diseno de Figma
     * incluye ese boton.
     */
    public Resultado<Usuario> entrarComoInvitado() {
        Usuario invitado = usuarioDao.buscarPorCorreo(CORREO_INVITADO);

        if (invitado == null) {
            Usuario nuevo = new Usuario(NOMBRE_INVITADO, CORREO_INVITADO, "local", null);
            // Contrasena aleatoria: esta cuenta nunca se usa para iniciar sesion.
            long id = usuarioDao.registrar(nuevo, UUID.randomUUID().toString());
            if (id <= 0) {
                return Resultado.error("No se pudo entrar como invitado.");
            }
            nuevo.setId(id);
            invitado = nuevo;
        }

        sesionManager.guardarSesion(invitado);
        return Resultado.exito(invitado);
    }

    // ------------------------------------------------------------------
    // SESION
    // ------------------------------------------------------------------

    public void cerrarSesion() {
        sesionManager.limpiar();
    }

    public boolean haySesionActiva() {
        return sesionManager.haySesionActiva();
    }
}
