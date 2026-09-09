package com.example.apptareas.auth;

import android.app.Activity;
import android.content.Context;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialCancellationException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.example.apptareas.R;
import com.example.apptareas.model.Usuario;
import com.example.apptareas.util.Resultado;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

/**
 * Obtiene un token de Google con Credential Manager y se lo entrega a
 * AuthManager, que es quien crea o recupera la cuenta.
 *
 * Responsable formal: B3 (seccion 8 del plan).
 *
 * Reparto de responsabilidades: esta clase solo consigue el idToken, hablando
 * con los servicios de Google. Todo lo que tiene que ver con la base de datos
 * vive en AuthManager.iniciarSesionConGoogle.
 *
 * Requisitos de configuracion externa (seccion 7 del plan):
 * - Proyecto de Firebase con el paquete com.example.apptareas registrado.
 * - Huella SHA-1 del keystore de cada equipo dada de alta en ese proyecto.
 *   Sin ella la peticion falla aunque el codigo sea correcto.
 * - El ID de cliente web, en res/values/auth_config.xml.
 */
public class GoogleAuthClient {

    /** Aviso del resultado. Siempre llega en el hilo principal. */
    public interface Callback {
        void onResultado(Resultado<Usuario> resultado);
    }

    private final Context context;
    private final CredentialManager credentialManager;
    private final AuthManager authManager;

    public GoogleAuthClient(Context context) {
        this.context = context.getApplicationContext();
        this.credentialManager = CredentialManager.create(this.context);
        this.authManager = new AuthManager(this.context);
    }

    /**
     * Abre el selector de cuentas de Google. El dialogo lo pinta el sistema,
     * por eso hace falta la Activity y no vale el contexto de aplicacion.
     */
    public void iniciarSesion(@NonNull Activity actividad, @NonNull Callback callback) {

        GetGoogleIdOption opcionGoogle = new GetGoogleIdOption.Builder()
                // false = ofrecer tambien cuentas con las que nunca se ha entrado,
                // necesario para que el primer inicio de sesion funcione.
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_web_client_id))
                // Que el usuario elija siempre, sin entrar solo con una cuenta.
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest peticion = new GetCredentialRequest.Builder()
                .addCredentialOption(opcionGoogle)
                .build();

        credentialManager.getCredentialAsync(
                actividad,
                peticion,
                new CancellationSignal(),
                // El callback vuelve al hilo principal: quien llama actualiza la UI.
                ContextCompat.getMainExecutor(context),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onResult(GetCredentialResponse respuesta) {
                        callback.onResultado(procesar(respuesta));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        callback.onResultado(Resultado.error(traducirError(e)));
                    }
                });
    }

    /** Saca el idToken de la credencial devuelta y lo pasa a AuthManager. */
    private Resultado<Usuario> procesar(GetCredentialResponse respuesta) {
        Credential credencial = respuesta.getCredential();

        boolean esTokenDeGoogle = credencial instanceof CustomCredential
                && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                        .equals(credencial.getType());

        if (!esTokenDeGoogle) {
            return Resultado.error("Google devolvio un tipo de credencial inesperado.");
        }

        try {
            GoogleIdTokenCredential credencialGoogle =
                    GoogleIdTokenCredential.createFrom(((CustomCredential) credencial).getData());

            return authManager.iniciarSesionConGoogle(credencialGoogle.getIdToken());

        } catch (Exception e) {
            return Resultado.error("No se pudo leer la credencial de Google.");
        }
    }

    /** Convierte las excepciones de Credential Manager en algo legible. */
    private String traducirError(GetCredentialException e) {
        if (e instanceof GetCredentialCancellationException) {
            return "Inicio de sesion cancelado.";
        }
        if (e instanceof NoCredentialException) {
            return "No hay ninguna cuenta de Google disponible en este dispositivo.";
        }
        // El caso tipico aqui es la SHA-1 sin registrar en el proyecto de Firebase.
        return "No se pudo iniciar sesion con Google.";
    }
}
