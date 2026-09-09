package com.example.apptareas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptareas.R;
import com.example.apptareas.auth.AuthManager;
import com.example.apptareas.auth.SesionManager;
import com.example.apptareas.data.TareaContract;
import com.example.apptareas.data.TareaDao;
import com.example.apptareas.model.Tarea;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Pantalla 00: lista de tareas con buscador, chips de filtro y FAB. */
public class ListaTareasActivity extends AppCompatActivity
        implements TareaAdapter.OnTareaClickListener {

    private TareaDao tareaDao;
    private SesionManager sesionManager;
    private AuthManager authManager;
    private TareaAdapter adapter;

    private RecyclerView rvTareas;
    private TextView tvVacio;
    private EditText etBuscar;
    private ChipGroup chipGroupEstado;

    /** null = sin filtro de estado (chip "Todas"). */
    private String filtroEstado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tareas);

        tareaDao = new TareaDao(this);
        sesionManager = new SesionManager(this);
        authManager = new AuthManager(this);

        rvTareas = findViewById(R.id.rvTareas);
        tvVacio = findViewById(R.id.tvVacio);
        etBuscar = findViewById(R.id.etBuscar);
        chipGroupEstado = findViewById(R.id.chipGroupEstado);

        adapter = new TareaAdapter(this);
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        rvTareas.setAdapter(adapter);

        chipGroupEstado.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filtroEstado = estadoDelChip(checkedIds.isEmpty() ? View.NO_ID : checkedIds.get(0));
            cargarTareas();
        });

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cargarTareas();
            }
        });

        ExtendedFloatingActionButton fabNueva = findViewById(R.id.fabNueva);
        fabNueva.setOnClickListener(v ->
                startActivity(new Intent(this, FormTareaActivity.class)));

        // La flecha superior izquierda cierra la sesion y vuelve al login.
        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> cerrarSesion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al volver del formulario o del detalle los datos pueden haber cambiado.
        cargarTareas();
    }

    private String estadoDelChip(int chipId) {
        if (chipId == R.id.chipPendientes) {
            return TareaContract.Estado.PENDIENTE;
        }
        if (chipId == R.id.chipProgreso) {
            return TareaContract.Estado.EN_PROGRESO;
        }
        if (chipId == R.id.chipCompletadas) {
            return TareaContract.Estado.COMPLETADA;
        }
        return null;
    }

    private void cargarTareas() {
        long usuarioId = sesionManager.usuarioIdActivo();
        String busqueda = etBuscar.getText().toString().trim();

        List<Tarea> resultado;
        if (busqueda.isEmpty()) {
            resultado = (filtroEstado == null)
                    ? tareaDao.listarTodas(usuarioId)
                    : tareaDao.listarPorEstado(usuarioId, filtroEstado);
        } else {
            // buscarPorTitulo no filtra por estado, asi que el chip se aplica encima.
            resultado = filtrarPorEstado(tareaDao.buscarPorTitulo(usuarioId, busqueda));
        }

        adapter.setTareas(resultado);
        // El RecyclerView no se oculta: tvVacio esta anclado a sus bordes en el
        // layout, y ponerlo GONE lo colapsaria a un punto y descolocaria el mensaje.
        tvVacio.setVisibility(resultado.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private List<Tarea> filtrarPorEstado(List<Tarea> origen) {
        if (filtroEstado == null || origen == null) {
            return origen == null ? new ArrayList<>() : origen;
        }
        List<Tarea> filtradas = new ArrayList<>();
        for (Tarea t : origen) {
            if (t.getEstado() != null
                    && t.getEstado().toLowerCase(Locale.ROOT).equals(filtroEstado)) {
                filtradas.add(t);
            }
        }
        return filtradas;
    }

    @Override
    public void onTareaClick(Tarea tarea) {
        Intent intent = new Intent(this, DetalleTareaActivity.class);
        intent.putExtra(DetalleTareaActivity.EXTRA_TAREA_ID, tarea.getId());
        startActivity(intent);
    }

    private void cerrarSesion() {
        authManager.cerrarSesion();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
