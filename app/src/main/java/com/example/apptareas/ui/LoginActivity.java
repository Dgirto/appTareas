package com.example.apptareas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptareas.R;
import com.example.apptareas.data.UsuarioDao;
import com.example.apptareas.model.Usuario;

import java.util.UUID;

/** Pantalla 01: inicio de sesion local, con acceso de invitado. */
public class LoginActivity extends AppCompatActivity {

    /** Cuenta local de respaldo para el acceso sin credenciales. */
    private static final String CORREO_INVITADO = "invitado@apptareas.local";

    private UsuarioDao usuarioDao;

    private EditText etUsuario;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sesion persistente: si ya hay una guardada, se salta el login.
        if (SesionTemporal.haySesion(this)) {
            irALista();
            return;
        }

        setContentView(R.layout.activity_login);

        usuarioDao = new UsuarioDao(this);

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

        // Google Sign-In es responsabilidad de B3 (auth/GoogleAuthClient) y aun no existe.
        btnGoogle.setOnClickListener(v -> avisar(getString(R.string.msg_google_pendiente)));
        tvOlvide.setOnClickListener(v -> avisar(getString(R.string.msg_recuperar_pendiente)));
    }

    private void iniciarSesion() {
        String correo = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (correo.isEmpty() || password.isEmpty()) {
            avisar(getString(R.string.msg_campos_obligatorios));
            return;
        }

        if (!usuarioDao.verificarPassword(correo, password)) {
            avisar(getString(R.string.msg_credenciales_invalidas));
            return;
        }

        Usuario usuario = usuarioDao.buscarPorCorreo(correo);
        if (usuario == null) {
            avisar(getString(R.string.msg_credenciales_invalidas));
            return;
        }

        // De momento la sesion se guarda siempre, marque o no "Recordarme".
        // Respetar ese check es cosa de auth/SesionManager (B3), que aun no existe.
        SesionTemporal.guardar(this, usuario.getId(), usuario.getNombre());
        irALista();
    }

    /**
     * Entra sin credenciales. No basta con un id ficticio: tareas.usuario_id es
     * clave foranea contra usuarios(id) y el esquema activa PRAGMA foreign_keys,
     * asi que insertar tareas con un id inexistente falla con SQLITE_CONSTRAINT.
     * Por eso el invitado se respalda en una cuenta local real, creada una sola vez.
     */
    private void entrarComoInvitado() {
        Usuario invitado = usuarioDao.buscarPorCorreo(CORREO_INVITADO);

        if (invitado == null) {
            Usuario nuevo = new Usuario(
                    getString(R.string.invitado_nombre), CORREO_INVITADO, "local", null);
            // Contrasena aleatoria: esta cuenta nunca se usa para iniciar sesion.
            long id = usuarioDao.registrar(nuevo, UUID.randomUUID().toString());
            if (id <= 0) {
                avisar(getString(R.string.msg_error_invitado));
                return;
            }
            SesionTemporal.guardar(this, id, getString(R.string.invitado_nombre));
        } else {
            SesionTemporal.guardar(this, invitado.getId(), invitado.getNombre());
        }

        irALista();
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
