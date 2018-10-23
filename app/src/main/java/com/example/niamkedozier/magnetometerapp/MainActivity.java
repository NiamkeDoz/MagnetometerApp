package com.example.niamkedozier.magnetometerapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView value;
    private TextView value2;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    public static DecimalFormat DECIMAL_FORMATTER2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        value = (TextView) findViewById(R.id.value);
        value2 = (TextView) findViewById(R.id.value2);
        //define decimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.00", symbols);
        DECIMAL_FORMATTER2 = new DecimalFormat("#.00000", symbols);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        String data = value.getText().toString();

        try{
            FileOutputStream fileOut = openFileOutput("Data.txt", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(data);
            outputWriter.write("\n");
            outputWriter.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        System.out.println(MainActivity.this.getFilesDir().getAbsoluteFile());
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            //Gets all axis
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            double magnitude2 = magnitude * .0001;
            value.setText((DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla"));
            value2.setText((DECIMAL_FORMATTER2.format(magnitude2) + " G (Gauss)"));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void BtnSave(View view){
        String data = value.getText().toString();

        //Tries to create a text file
        try{
            FileOutputStream fileOut = openFileOutput("Data.txt", MODE_APPEND);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(data);
            outputWriter.write("\n");
            outputWriter.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}