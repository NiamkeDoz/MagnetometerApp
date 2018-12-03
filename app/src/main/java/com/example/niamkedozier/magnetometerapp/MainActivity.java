package com.example.niamkedozier.magnetometerapp;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView time;
    private EditText txtFilename;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    private LocationManager locationManager;
    private Context context;
    Runnable updater;
    double elapsed;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        value = (TextView) findViewById(R.id.value);
        Button SaveBtn = findViewById(R.id.Save);
        time = findViewById(R.id.elapsedTime);
        txtFilename = findViewById(R.id.textFileName);


        //define decimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        DECIMAL_FORMATTER = new DecimalFormat("#.00", symbols);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


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
        final Handler timeHandler = new Handler();
        elapsed = 0.0;
            updater = new Runnable() {
            @Override
            public void run() {
                SaveData();
                timeHandler.postDelayed(updater, 500);
                elapsed += 0.5;

            }
        };
        timeHandler.post(updater);

    }

    public void SaveData(){
        value2 = findViewById(R.id.SavedValue);
        value2.setText(value.getText().toString());
        time.setText(String.valueOf(elapsed));
        String fileName = txtFilename.getText().toString();
        String contents = value2.getText().toString() + "\n";
        String test3 = "test3.txt";
        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        // Create text file
        File file = new File(getExternalFilesDir(null), fileName);
        FileOutputStream outputStream;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file, true);
            outputStream.write(contents.getBytes());
            outputStream.flush();
            outputStream.close();

            //display file saved message
//            Toast.makeText(getBaseContext(), "File saved successfully!",
////                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}