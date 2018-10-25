package com.example.niamkedozier.magnetometerapp;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView value;
    private TextView value2;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        value = (TextView) findViewById(R.id.value);
        Button SaveBtn = findViewById(R.id.Save);

        //define decimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.00", symbols);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
            value.setText((DECIMAL_FORMATTER.format(magnitude)));           // + " \u00B5Tesla"

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void BtnPress(View view){
        System.out.println("Button Pressed");
        System.out.println(MainActivity.this.getFilesDir().getAbsoluteFile());
        SaveData();
    }

    public void SaveData(){
        value2 = findViewById(R.id.SavedValue);
        value2.setText(value.getText().toString());
        String fileName = "hello.txt";
        String contents = value2.getText().toString() + ",";
        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        File file = new File(getExternalFilesDir(null), fileName);
        FileOutputStream outputStream;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);
            outputStream.write(contents.getBytes());
            outputStream.flush();
            outputStream.close();

            //display file saved message
            Toast.makeText(getBaseContext(), "File saved successfully!",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}