package com.androidutn.gastos;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidutn.gastos.model.Movimiento;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andres on 10/3/17.
 */

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.ViewHolder> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    private List<Movimiento> datos;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setMovimiento(datos.get(position));
    }

    @Override
    public int getItemCount() {
        return datos != null ? datos.size() : 0;
    }

    public void setDatos(List<Movimiento> datos) {
        this.datos = datos;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_mov_categoria) TextView mCategoria;
        @BindView(R.id.item_mov_descripcion) TextView mDescripcion;
        @BindView(R.id.item_mov_fecha) TextView mFecha;
        @BindView(R.id.item_mov_monto) TextView mMonto;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setMovimiento(Movimiento movimiento) {
            mCategoria.setText(movimiento.getCategoriaNombre());
            mDescripcion.setText(movimiento.getDescripcion());
            mFecha.setText(DATE_FORMAT.format(new Date(movimiento.getFecha())));
            mMonto.setText(NumberFormat.getCurrencyInstance().format(movimiento.getMonto()));
            if (movimiento.isIngreso()) {
                mMonto.setTextColor(ContextCompat.getColor(mMonto.getContext(), R.color.ingreso));
            } else {
                mMonto.setTextColor(ContextCompat.getColor(mMonto.getContext(), R.color.egreso));
            }
        }
    }

}
