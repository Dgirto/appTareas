package com.example.apptareas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.apptareas.R;
import com.example.apptareas.data.TareaContract;
import com.example.apptareas.data.TareaDao;
import com.example.apptareas.model.EstadoTarea;
import com.example.apptareas.model.Tarea;
import com.example.apptareas.util.FechaUtils;
import com.example.apptareas.util.Resultado;
import com.example.apptareas.util.Validaciones;

/** Pantalla 02: detalle de una tarea con sus seis campos y acciones de estado. */
public class DetalleTareaActivity extends AppCompatActivity {

    public static final String EXTRA_TAREA_ID = "extra_tarea_id";

    private TareaDao tareaDao;
    private long tareaId;
    private Tarea tarea;

    private TextView tvEstadoChip;
    private View dotEstadoDetalle;
    private TextView tvTituloTarea;
    private TextView tvVence;
    private TextView tvDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_tarea);

        tareaDao = new TareaDao(this);
        tareaId = getIntent().getLongExtra(EXTRA_TAREA_ID, 0L);

        tvEstadoChip = findViewById(R.id.tvEstadoChip);
        dotEstadoDetalle = findViewById(R.id.dotEstadoDetalle);
        tvTituloTarea = findViewById(R.id.tvTituloTarea);
        tvVence = findViewById(R.id.tvVence);
        tvDescripcion = findViewById(R.id.tvDescripcion);

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        ImageButton btnEditar = findViewById(R.id.btnEditar);
        ImageButton btnEliminar = findViewById(R.id.btnEliminar);
        TextView btnCompletada = findViewById(R.id.btnCompletada);
        TextView btnPendiente = findViewById(R.id.btnPendiente);

        btnVolver.setOnClickListener(v -> finish());

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormTareaActivity.class);
            intent.putExtra(FormTareaActivity.EXTRA_TAREA_ID, tareaId);
            startActivity(intent);
        });

        btnEliminar.setOnClickListener(v -> {
            if (tarea == null) {
                return;
            }
            ConfirmarEliminarDialog.mostrar(this, tarea.getTitulo(), this::eliminar);
        });

        btnCompletada.setOnClickListener(v -> cambiarEstado(TareaContract.Estado.COMPLETADA));
        btnPendiente.setOnClickListener(v -> cambiarEstado(TareaContract.Estado.PENDIENTE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Al volver de editar hay que releer: los datos pueden haber cambiado.
        cargar();
    }

    private void cargar() {
        tarea = tareaDao.obtenerPorId(tareaId);
        if (tarea == null) {
            finish();
            return;
        }

        EstadoTarea estado = EstadoTarea.desdeString(tarea.getEstado());
        String textoEstado = getString(EstadosUi.textoDe(estado));

        tvEstadoChip.setText(textoEstado);
        dotEstadoDetalle.getBackground().mutate().setTint(
                ContextCompat.getColor(this, EstadosUi.colorDe(estado)));

        tvTituloTarea.setText(tarea.getTitulo());

        String vencimiento = FechaUtils.isoAUi(tarea.getFechaVencimiento());
        tvVence.setText(vencimiento.isEmpty()
                ? getString(R.string.detalle_sin_vencimiento)
                : getString(R.string.detalle_vence, vencimiento));

        String descripcion = tarea.getDescripcion();
        tvDescripcion.setText((descripcion == null || descripcion.trim().isEmpty())
                ? getString(R.string.sin_descripcion)
                : descripcion);

        // item_campo_detalle.xml es generico, asi que cada fila se rellena aqui.
        String asignado = tarea.getUsuarioAsignado();
        rellenarFila(R.id.filaEstado, R.drawable.ic_reloj, R.string.campo_estado, textoEstado);
        rellenarFila(R.id.filaVencimiento, R.drawable.ic_vencimiento, R.string.campo_vencimiento,
                vencimiento.isEmpty() ? getString(R.string.detalle_sin_vencimiento) : vencimiento);
        rellenarFila(R.id.filaCreacion, R.drawable.ic_creacion, R.string.campo_creacion,
                FechaUtils.isoAUi(tarea.getFechaCreacion()));
        rellenarFila(R.id.filaAsignado, R.drawable.ic_usuario, R.string.campo_asignado,
                (asignado == null || asignado.trim().isEmpty())
                        ? getString(R.string.sin_asignar) : asignado);
    }

    private void rellenarFila(int filaId, @DrawableRes int icono, @StringRes int etiqueta,
                              String valor) {
        View fila = findViewById(filaId);
        ImageView ivIcono = fila.findViewById(R.id.ivCampoIcono);
        TextView tvLabel = fila.findViewById(R.id.tvCampoLabel);
        TextView tvValor = fila.findViewById(R.id.tvCampoValor);

        ivIcono.setImageResource(icono);
        tvLabel.setText(etiqueta);
        tvValor.setText(valor);
    }

    private void cambiarEstado(String nuevoEstado) {
        if (tarea == null) {
            return;
        }

        // Las transiciones validas las decide B4 (util/Validaciones), no la pantalla.
        Resultado<Void> validacion = Validaciones.validarTransicionEstado(
                EstadoTarea.desdeString(tarea.getEstado()),
                EstadoTarea.desdeString(nuevoEstado));

        if (!validacion.esExitoso()) {
            avisar(validacion.getMensaje());
            return;
        }

        if (tareaDao.cambiarEstado(tareaId, nuevoEstado) > 0) {
            avisar(getString(R.string.msg_estado_actualizado));
            cargar();
        }
    }

    private void eliminar() {
        if (tareaDao.eliminar(tareaId) > 0) {
            avisar(getString(R.string.msg_tarea_eliminada));
            finish();
        }
    }

    private void avisar(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
