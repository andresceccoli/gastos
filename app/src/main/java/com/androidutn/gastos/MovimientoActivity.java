package com.androidutn.gastos;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidutn.gastos.model.Categoria;
import com.androidutn.gastos.model.Movimiento;
import com.androidutn.gastos.model.Resumen;
import com.androidutn.gastos.model.ResumenCategoria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerInputEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerInputEditText;

public class MovimientoActivity extends AppCompatActivity {

    public static final String EXTRA_INGRESO = "movIngreso";

    private static final SimpleDateFormat MES_DB_FORMAT = new SimpleDateFormat("yyyyMM", Locale.getDefault());

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

        Calendar c = Calendar.getInstance();
        mFechaInput.onDateSet(null,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DATE));
        mHoraInput.onTimeSet(null,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE));

        ingreso = getIntent().getBooleanExtra(EXTRA_INGRESO, false);

        getSupportActionBar().setTitle(
                ingreso ?
                        getString(R.string.nuevo_ingreso) :
                        getString(R.string.nuevo_egreso));

        // para toolbar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cargarCategorias();
    }

    private void cargarCategorias() {
        String ref = ingreso ? "categorias_ingresos" : "categorias_egresos";
        DatabaseReference categorias = FirebaseDatabase.getInstance().getReference(ref);
        categorias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Categoria> cats = new ArrayList<Categoria>();

                for (DataSnapshot c : dataSnapshot.getChildren()) {
                    Categoria cat = c.getValue(Categoria.class);
                    cats.add(cat);
                }

                mCategoria.setAdapter(new ArrayAdapter<Categoria>(
                        MovimientoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cats));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            return;
        } else {
            mMonto.setError(null);
            mMonto.setErrorEnabled(false);
        }

        final Movimiento mov = new Movimiento();
        mov.setIngreso(ingreso);
        mov.setMonto(Double.parseDouble(mMonto.getEditText().getText().toString()));
        mov.setDescripcion(mDescripcion.getEditText().getText().toString());

        Categoria categoria = (Categoria) mCategoria.getSelectedItem();
        mov.setCategoriaKey(categoria.getId());
        mov.setCategoriaNombre(categoria.getNombre());

        Calendar fecha = mFechaInput.getDate();
        Calendar hora = mHoraInput.getTime();
        fecha.set(Calendar.HOUR_OF_DAY, hora.get(Calendar.HOUR_OF_DAY));
        fecha.set(Calendar.MINUTE, hora.get(Calendar.MINUTE));

        mov.setFecha(fecha.getTimeInMillis());
        mov.setFechaRev(-mov.getFecha());

        DatabaseReference movimientos = FirebaseDatabase.getInstance().getReference("movimientos");
        String key = movimientos.push().getKey();
        movimientos.child(key).setValue(mov, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    actualizarResumen(mov);
                }
            }
        });
    }

    private void actualizarResumen(final Movimiento mov) {
        FirebaseDatabase.getInstance().getReference("resumen")
                .child(MES_DB_FORMAT.format(new Date(mov.getFecha())))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Resumen resumen = dataSnapshot.getValue(Resumen.class);
                        if (resumen == null) {
                            resumen = new Resumen();
                        }

                        if (ingreso) {
                            resumen.setTotalIngresos(resumen.getTotalIngresos() + mov.getMonto());
                        } else {
                            resumen.setTotalEgresos(resumen.getTotalEgresos() + mov.getMonto());
                        }

                        dataSnapshot.getRef().setValue(resumen, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    actualizarResumenCategoria(mov);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void actualizarResumenCategoria(final Movimiento mov) {
        FirebaseDatabase.getInstance().getReference(
                ingreso ? "resumen_ingresos" : "resumen_egresos")
                .child(MES_DB_FORMAT.format(new Date(mov.getFecha())))
                .child(mov.getCategoriaKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ResumenCategoria resumenCat = dataSnapshot.getValue(ResumenCategoria.class);
                        if (resumenCat == null) {
                            resumenCat = new ResumenCategoria();
                        }

                        resumenCat.setNombre(mov.getCategoriaNombre());
                        resumenCat.setTotal(resumenCat.getTotal() + mov.getMonto());

                        dataSnapshot.getRef().setValue(resumenCat, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @OnClick(R.id.mov_nueva_categoria)
    public void onNuevaCategoria() {

    }
}
