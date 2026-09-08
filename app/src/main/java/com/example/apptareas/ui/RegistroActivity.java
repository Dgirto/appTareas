package com.example.apptareas.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptareas.R;
import com.example.apptareas.data.UsuarioDao;
import com.example.apptareas.model.Usuario;

/** Registro de cuenta local. El alta con Google la cubre B3. */
public class RegistroActivity extends AppCompatActivity {

    private static final int LARGO_MINIMO_PASSWORD = 6;

    private UsuarioDao usuarioDao;

    private EditText etNombre;
    private EditText etCorreo;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioDao = new UsuarioDao(this);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        TextView btnRegistrarme = findViewById(R.id.btnRegistrarme);
        TextView tvIniciaSesion = findViewById(R.id.tvIniciaSesion);

        btnVolver.setOnClickListener(v -> finish());
        tvIniciaSesion.setOnClickListener(v -> finish());
        btnRegistrarme.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            avisar(getString(R.string.msg_campos_obligatorios));
            return;
        }

        if (password.length() < LARGO_MINIMO_PASSWORD) {
            avisar(getString(R.string.msg_password_corta));
            return;
        }

        if (usuarioDao.existeCorreo(correo)) {
            avisar(getString(R.string.msg_correo_registrado));
            return;
        }

        // El hash de la contrasena lo hace UsuarioDao: aqui nunca se guarda en claro.
        Usuario usuario = new Usuario(nombre, correo, "local", null);
        if (usuarioDao.registrar(usuario, password) > 0) {
            avisar(getString(R.string.msg_registro_ok));
            finish();
        } else {
            avisar(getString(R.string.msg_error_registro));
        }
    }

    private void avisar(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
