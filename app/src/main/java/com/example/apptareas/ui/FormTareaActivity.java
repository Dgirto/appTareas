package com.example.apptareas.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apptareas.R;
import com.example.apptareas.auth.SesionManager;
import com.example.apptareas.data.TareaContract;
import com.example.apptareas.data.TareaDao;
import com.example.apptareas.model.Tarea;
import com.example.apptareas.util.FechaUtils;
import com.example.apptareas.util.Resultado;
import com.example.apptareas.util.Validaciones;
import com.google.android.material.chip.ChipGroup;

/** Pantalla 03: crear y editar tarea. */
public class FormTareaActivity extends AppCompatActivity {

    /** Id de la tarea a editar. Si no viene, se esta creando una nueva. */
    public static final String EXTRA_TAREA_ID = "extra_tarea_id";

    private TareaDao tareaDao;
    private SesionManager sesionManager;

    private EditText etTitulo;
    private EditText etDescripcion;
    private EditText etVencimiento;
    private EditText etAsignado;
    private ChipGroup chipGroupFormEstado;
    private TextView tvFechaCreacion;

    private long tareaId = 0L;
    private Tarea tareaEnEdicion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_tarea);

        tareaDao = new TareaDao(this);
        sesionManager = new SesionManager(this);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etVencimiento = findViewById(R.id.etVencimiento);
        etAsignado = findViewById(R.id.etAsignado);
        chipGroupFormEstado = findViewById(R.id.chipGroupFormEstado);
        tvFechaCreacion = findViewById(R.id.tvFechaCreacion);

        TextView tvTituloBarra = findViewById(R.id.tvTituloBarra);
        ImageButton btnCerrar = findViewById(R.id.btnCerrar);
        TextView btnGuardarBarra = findViewById(R.id.btnGuardarBarra);
        TextView btnGuardar = findViewById(R.id.btnGuardar);

        btnCerrar.setOnClickListener(v -> finish());
        btnGuardarBarra.setOnClickListener(v -> guardar());
        btnGuardar.setOnClickListener(v -> guardar());

        // El campo de fecha es focusable="false" a proposito: abre el DatePicker,
        // no el teclado. El picker lo escribe en formato dd/MM/yyyy.
        etVencimiento.setOnClickListener(v -> FechaUtils.mostrarDatePicker(this, etVencimiento));

        tareaId = getIntent().getLongExtra(EXTRA_TAREA_ID, 0L);
        if (tareaId > 0) {
            tvTituloBarra.setText(R.string.editar_tarea);
            cargarTarea();
        } else {
            tvFechaCreacion.setText(getString(
                    R.string.fecha_creacion_auto, FechaUtils.isoAUi(FechaUtils.hoyIso())));
        }
    }

    private void cargarTarea() {
        tareaEnEdicion = tareaDao.obtenerPorId(tareaId);
        if (tareaEnEdicion == null) {
            finish();
            return;
        }

        etTitulo.setText(tareaEnEdicion.getTitulo());
        etDescripcion.setText(tareaEnEdicion.getDescripcion());
        etVencimiento.setText(FechaUtils.isoAUi(tareaEnEdicion.getFechaVencimiento()));
        etAsignado.setText(tareaEnEdicion.getUsuarioAsignado());
        tvFechaCreacion.setText(getString(
                R.string.fecha_creacion_auto,
                FechaUtils.isoAUi(tareaEnEdicion.getFechaCreacion())));

        marcarChipDeEstado(tareaEnEdicion.getEstado());
    }

    private void marcarChipDeEstado(String estado) {
        if (TareaContract.Estado.EN_PROGRESO.equals(estado)) {
            chipGroupFormEstado.check(R.id.chipFormProgreso);
        } else if (TareaContract.Estado.COMPLETADA.equals(estado)) {
            chipGroupFormEstado.check(R.id.chipFormCompletada);
        } else {
            chipGroupFormEstado.check(R.id.chipFormPendiente);
        }
    }

    private String estadoSeleccionado() {
        int marcado = chipGroupFormEstado.getCheckedChipId();
        if (marcado == R.id.chipFormProgreso) {
            return TareaContract.Estado.EN_PROGRESO;
        }
        if (marcado == R.id.chipFormCompletada) {
            return TareaContract.Estado.COMPLETADA;
        }
        return TareaContract.Estado.PENDIENTE;
    }

    private void guardar() {
        String titulo = etTitulo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String asignado = etAsignado.getText().toString().trim();

        // El picker escribe dd/MM/yyyy; la BD guarda ISO.
        String vencimientoIso = FechaUtils.uiAIso(etVencimiento.getText().toString().trim());

        Resultado<Void> validacionTitulo = Validaciones.validarTitulo(titulo);
        if (!validacionTitulo.esExitoso()) {
            avisar(validacionTitulo.getMensaje());
            etTitulo.requestFocus();
            return;
        }

        String fechaCreacion = (tareaEnEdicion != null)
                ? tareaEnEdicion.getFechaCreacion()
                : FechaUtils.hoyIso();

        Resultado<Void> validacionFecha =
                Validaciones.validarFechaVencimiento(fechaCreacion, vencimientoIso);
        if (!validacionFecha.esExitoso()) {
            avisar(validacionFecha.getMensaje());
            return;
        }

        Tarea tarea = (tareaEnEdicion != null) ? tareaEnEdicion : new Tarea();
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        tarea.setEstado(estadoSeleccionado());
        tarea.setFechaVencimiento(vencimientoIso.isEmpty() ? null : vencimientoIso);
        tarea.setUsuarioAsignado(asignado.isEmpty() ? null : asignado);
        tarea.setUsuarioId(sesionManager.usuarioIdActivo());

        boolean ok;
        if (tareaId > 0) {
            tarea.setId(tareaId);
            ok = tareaDao.actualizar(tarea) > 0;
        } else {
            // fecha_creacion la pone el DAO, nunca el formulario.
            ok = tareaDao.insertar(tarea) > 0;
        }

        if (ok) {
            avisar(getString(R.string.msg_tarea_guardada));
            finish();
        } else {
            avisar(getString(R.string.msg_error_guardar));
        }
    }

    private void avisar(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
