package com.example.apptareas.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptareas.R;
import com.example.apptareas.auth.AuthManager;
import com.example.apptareas.model.Usuario;
import com.example.apptareas.util.Resultado;

/** Registro de cuenta local. El alta con Google la resuelve AuthManager. */
public class RegistroActivity extends AppCompatActivity {

    private AuthManager authManager;

    private EditText etNombre;
    private EditText etCorreo;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        authManager = new AuthManager(this);

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
        // Las validaciones y el hash de la contrasena viven en auth/ y data/:
        // aqui solo se recogen los campos y se muestra el resultado.
        Resultado<Usuario> resultado = authManager.registrar(
                etNombre.getText().toString(),
                etCorreo.getText().toString(),
                etPassword.getText().toString());

        if (resultado.esExitoso()) {
            avisar(getString(R.string.msg_registro_ok));
            finish();
        } else {
            avisar(resultado.getMensaje());
        }
    }

    private void avisar(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
