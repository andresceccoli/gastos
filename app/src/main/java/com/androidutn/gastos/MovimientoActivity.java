package com.androidutn.gastos;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.androidutn.gastos.model.Categoria;
import com.androidutn.gastos.model.Movimiento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        Movimiento mov = new Movimiento();
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
        movimientos.child(key).setValue(mov);
    }

    @OnClick(R.id.mov_nueva_categoria)
    public void onNuevaCategoria() {

    }
}
