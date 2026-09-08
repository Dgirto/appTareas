package com.example.apptareas.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptareas.R;
import com.example.apptareas.model.EstadoTarea;
import com.example.apptareas.model.Tarea;
import com.example.apptareas.util.FechaUtils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter del RecyclerView de la lista de tareas.
 * Pinta item_tarea.xml: icono, punto de estado, titulo y usuario asignado.
 */
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    /** Aviso de que se ha pulsado una tarjeta. */
    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea);
    }

    /**
     * El diseno alterna el color de fondo de las tarjetas. Como el modelo no
     * guarda ningun color, se reparten ciclicamente por posicion.
     */
    private static final int[] COLORES_TARJETA = {
            R.color.tarjeta_azul,
            R.color.tarjeta_amarillo,
            R.color.tarjeta_menta,
            R.color.tarjeta_lila,
            R.color.tarjeta_lima,
            R.color.tarjeta_rosa
    };

    /** Lo mismo con los iconos: el modelo no elige uno, asi que se alternan. */
    private static final int[] ICONOS_TARJETA = {
            R.drawable.ic_tarea_documento,
            R.drawable.ic_tarea_bolsa,
            R.drawable.ic_tarea_carpeta,
            R.drawable.ic_tarea_caja,
            R.drawable.ic_tarea_limpieza
    };

    private final List<Tarea> tareas = new ArrayList<>();
    private final OnTareaClickListener listener;

    public TareaAdapter(OnTareaClickListener listener) {
        this.listener = listener;
    }

    /** Reemplaza el contenido de la lista y repinta. */
    public void setTareas(List<Tarea> nuevas) {
        tareas.clear();
        if (nuevas != null) {
            tareas.addAll(nuevas);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        holder.enlazar(tareas.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView tarjeta;
        private final ImageView ivIcono;
        private final View dotEstado;
        private final TextView tvEstado;
        private final TextView tvTitulo;
        private final TextView tvAsignado;

        TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            tarjeta = itemView.findViewById(R.id.cardTarea);
            ivIcono = itemView.findViewById(R.id.ivIcono);
            dotEstado = itemView.findViewById(R.id.dotEstado);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAsignado = itemView.findViewById(R.id.tvAsignado);
        }

        void enlazar(Tarea tarea, int position, OnTareaClickListener listener) {
            android.content.Context contexto = itemView.getContext();

            tarjeta.setCardBackgroundColor(ContextCompat.getColor(
                    contexto, COLORES_TARJETA[position % COLORES_TARJETA.length]));
            ivIcono.setImageResource(ICONOS_TARJETA[position % ICONOS_TARJETA.length]);

            EstadoTarea estado = EstadoTarea.desdeString(tarea.getEstado());
            String textoEstado = contexto.getString(EstadosUi.textoDe(estado));

            // El punto reutiliza bg_dot_estado, asi que hay que mutar el drawable
            // antes de tenirlo o todas las tarjetas compartirian el mismo color.
            dotEstado.getBackground().mutate().setTint(
                    ContextCompat.getColor(contexto, EstadosUi.colorDe(estado)));

            String vencimiento = FechaUtils.isoAUi(tarea.getFechaVencimiento());
            if (vencimiento.isEmpty()) {
                tvEstado.setText(textoEstado);
            } else {
                tvEstado.setText(contexto.getString(
                        R.string.tarjeta_estado_vence, textoEstado, vencimiento));
            }

            tvTitulo.setText(tarea.getTitulo());

            String asignado = tarea.getUsuarioAsignado();
            if (asignado == null || asignado.trim().isEmpty()) {
                tvAsignado.setText(R.string.sin_asignar);
            } else {
                tvAsignado.setText(contexto.getString(R.string.tarjeta_asignada, asignado));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTareaClick(tarea);
                }
            });
        }
    }
}
