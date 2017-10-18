package com.androidutn.gastos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final SimpleDateFormat MES_FORMAT = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat MES_DB_FORMAT = new SimpleDateFormat("yyyyMM", Locale.getDefault());

    @BindView(R.id.mes_anterior) ImageView mMesAnterior;
    @BindView(R.id.mes_siguiente) ImageView mMesSiguiente;
    @BindView(R.id.mes_text) TextView mMesText;
    @BindView(R.id.text_ingresos) TextView mTextIngresos;
    @BindView(R.id.text_egresos) TextView mTextEgresos;
    @BindView(R.id.text_saldo) TextView mTextSaldo;

    private Calendar mesActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mesActual = Calendar.getInstance();
        mostrarMes();

    }

    private void mostrarMes() {
        mMesText.setText(MES_FORMAT.format(mesActual.getTime()));
    }

    @OnClick(R.id.mes_anterior)
    public void onMesAnterior() {
        mesActual.add(Calendar.MONTH, -1);
        mostrarMes();
    }

    @OnClick(R.id.mes_siguiente)
    public void onMesSiguiente() {
        mesActual.add(Calendar.MONTH, 1);
        mostrarMes();
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
