package com.example.apptareas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptareas.R;
import com.example.apptareas.auth.AuthManager;
import com.example.apptareas.model.Usuario;
import com.example.apptareas.util.Resultado;

/** Pantalla 01: inicio de sesion local, con acceso de invitado. */
public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;

    private EditText etUsuario;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = new AuthManager(this);

        // Sesion persistente: si ya hay una guardada, se salta el login.
        if (authManager.haySesionActiva()) {
            irALista();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);

        TextView btnIniciar = findViewById(R.id.btnIniciar);
        TextView btnGoogle = findViewById(R.id.btnGoogle);
        TextView btnInvitado = findViewById(R.id.btnInvitado);
        TextView tvRegistrate = findViewById(R.id.tvRegistrate);
        TextView tvOlvide = findViewById(R.id.tvOlvide);

        btnIniciar.setOnClickListener(v -> iniciarSesion());
        btnInvitado.setOnClickListener(v -> entrarComoInvitado());

        tvRegistrate.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));

        // AuthManager.iniciarSesionConGoogle ya existe, pero quien consigue el
        // idToken es auth/GoogleAuthClient, que necesita el proyecto de Firebase
        // y google-services.json (seccion 7 del plan). Hasta entonces, aviso.
        btnGoogle.setOnClickListener(v -> avisar(getString(R.string.msg_google_pendiente)));
        tvOlvide.setOnClickListener(v -> avisar(getString(R.string.msg_recuperar_pendiente)));
    }

    private void iniciarSesion() {
        Resultado<Usuario> resultado = authManager.iniciarSesion(
                etUsuario.getText().toString(),
                etPassword.getText().toString());

        if (resultado.esExitoso()) {
            irALista();
        } else {
            avisar(resultado.getMensaje());
        }
    }

    private void entrarComoInvitado() {
        Resultado<Usuario> resultado = authManager.entrarComoInvitado();

        if (resultado.esExitoso()) {
            irALista();
        } else {
            avisar(resultado.getMensaje());
        }
    }

    private void irALista() {
        Intent intent = new Intent(this, ListaTareasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void avisar(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
