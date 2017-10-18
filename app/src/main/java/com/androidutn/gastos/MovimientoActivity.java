package com.androidutn.gastos;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Spinner;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerInputEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerInputEditText;

public class MovimientoActivity extends AppCompatActivity {

    public static final String EXTRA_INGRESO = "movIngreso";

    @BindView(R.id.mov_categoria) Spinner mCategoria;
    @BindView(R.id.mov_monto) TextInputLayout mMonto;
    @BindView(R.id.mov_descripcion) TextInputLayout mDescripcion;
    @BindView(R.id.mov_fecha) TextInputLayout mFecha;
    @BindView(R.id.mov_hora) TextInputLayout mHora;
    @BindView(R.id.mov_fecha_input) DatePickerInputEditText mFechaInput;
    @BindView(R.id.mov_hora_input) TimePickerInputEditText mHoraInput;

    private boolean ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);

        ButterKnife.bind(this);

        mFechaInput.setManager(getSupportFragmentManager());
        mHoraInput.setManager(getSupportFragmentManager());

        mFechaInput.setDate(Calendar.getInstance());
        mHoraInput.setTime(Calendar.getInstance());

        ingreso = getIntent().getBooleanExtra(EXTRA_INGRESO, false);

        getSupportActionBar().setTitle(
                ingreso ?
                        getString(R.string.nuevo_ingreso) :
                        getString(R.string.nuevo_egreso));

        // para toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.mov_cancelar)
    public void onCancelar() {
        finish();
    }

    @OnClick(R.id.mov_aceptar)
    public void onAceptar() {
        if (TextUtils.isEmpty(mMonto.getEditText().getText())) {
            mMonto.setError(getString(R.string.mov_monto_error));
            mMonto.setErrorEnabled(true);
        } else {
            mMonto.setError(null);
            mMonto.setErrorEnabled(false);
        }
    }

    @OnClick(R.id.mov_nueva_categoria)
    public void onNuevaCategoria() {

    }
}
