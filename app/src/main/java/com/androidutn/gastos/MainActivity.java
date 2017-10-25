package com.androidutn.gastos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidutn.gastos.model.Resumen;
import com.androidutn.gastos.model.ResumenCategoria;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity {

    private static final SimpleDateFormat MES_FORMAT = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat MES_DB_FORMAT = new SimpleDateFormat("yyyyMM", Locale.getDefault());

    @BindView(R.id.mes_anterior) ImageView mMesAnterior;
    @BindView(R.id.mes_siguiente) ImageView mMesSiguiente;
    @BindView(R.id.mes_text) TextView mMesText;
    @BindView(R.id.text_ingresos) TextView mTextIngresos;
    @BindView(R.id.text_egresos) TextView mTextEgresos;
    @BindView(R.id.text_saldo) TextView mTextSaldo;

    @BindView(R.id.grafico_egresos) PieChartView mGraficoEgresos;
    @BindView(R.id.grafico_ingresos) PieChartView mGraficoIngresos;

    private Calendar mesActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mGraficoEgresos.setCircleFillRatio(0.7f);
        mGraficoIngresos.setCircleFillRatio(0.7f);

        mesActual = Calendar.getInstance();
        mostrarMes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        cargarInfo();
    }

    private void cargarInfo() {
        FirebaseDatabase.getInstance().getReference("resumen")
                .child(MES_DB_FORMAT.format(mesActual.getTime()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Resumen resumen = dataSnapshot.getValue(Resumen.class);

                        if (resumen != null) {
                            mTextIngresos.setText(
                                    NumberFormat.getCurrencyInstance().format(
                                            resumen.getTotalIngresos()));

                            mTextEgresos.setText(
                                    NumberFormat.getCurrencyInstance().format(
                                            resumen.getTotalEgresos()));

                            mTextSaldo.setText(NumberFormat.getCurrencyInstance().format(
                                    resumen.getTotalIngresos() - resumen.getTotalEgresos()));
                        } else {
                            mTextIngresos.setText(null);
                            mTextEgresos.setText(null);
                            mTextSaldo.setText(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        FirebaseDatabase.getInstance().getReference("resumen_egresos")
                .child(MES_DB_FORMAT.format(mesActual.getTime()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mostrarGrafico(mGraficoEgresos, dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
        FirebaseDatabase.getInstance().getReference("resumen_ingresos")
                .child(MES_DB_FORMAT.format(mesActual.getTime()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mostrarGrafico(mGraficoIngresos, dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    private void mostrarGrafico(PieChartView grafico, DataSnapshot data) {
        List<SliceValue> values = new ArrayList<>();

        for (DataSnapshot ds : data.getChildren()) {
            ResumenCategoria resumenCat = ds.getValue(ResumenCategoria.class);
            SliceValue slice = new SliceValue((float) resumenCat.getTotal(), ChartUtils.nextColor());
            slice.setLabel(resumenCat.getNombre());
            values.add(slice);
        }

        PieChartData pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(true)
                .setHasLabelsOutside(true)
                .setHasLabelsOnlyForSelected(false);
        grafico.setPieChartData(pieChartData);
    }

    private void mostrarMes() {
        mMesText.setText(MES_FORMAT.format(mesActual.getTime()));
    }

    @OnClick(R.id.mes_anterior)
    public void onMesAnterior() {
        mesActual.add(Calendar.MONTH, -1);
        mostrarMes();
        cargarInfo();
    }

    @OnClick(R.id.mes_siguiente)
    public void onMesSiguiente() {
        mesActual.add(Calendar.MONTH, 1);
        mostrarMes();
        cargarInfo();
    }

    @OnClick(R.id.nuevo_ingreso)
    public void onNuevoIngreso() {
        Intent intent = new Intent(this, MovimientoActivity.class);
        intent.putExtra(MovimientoActivity.EXTRA_INGRESO, true);
        startActivity(intent);
    }

    @OnClick(R.id.nuevo_egreso)
    public void onNuevoEgreso() {
        Intent intent = new Intent(this, MovimientoActivity.class);
        intent.putExtra(MovimientoActivity.EXTRA_INGRESO, false);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
