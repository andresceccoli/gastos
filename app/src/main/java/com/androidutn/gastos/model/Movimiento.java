package com.androidutn.gastos.model;

/**
 * Created by andres on 9/27/17.
 */

public class Movimiento {

    private boolean ingreso;
    private long fecha;
    private long fechaRev;
    private String categoriaKey;
    private String categoriaNombre;
    private double monto;
    private String descripcion;

    public boolean isIngreso() {
        return ingreso;
    }

    public void setIngreso(boolean ingreso) {
        this.ingreso = ingreso;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public long getFechaRev() {
        return fechaRev;
    }

    public void setFechaRev(long fechaRev) {
        this.fechaRev = fechaRev;
    }

    public String getCategoriaKey() {
        return categoriaKey;
    }

    public void setCategoriaKey(String categoriaKey) {
        this.categoriaKey = categoriaKey;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
