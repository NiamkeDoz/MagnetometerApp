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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView magneticReading;
    private TextView previousMagneticReading;
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
        //links variables from Designer to variables in MainActivity.java
        magneticReading = (TextView) findViewById(R.id.magneticReading);
        Button SaveBtn = findViewById(R.id.Save);
        time = findViewById(R.id.elapsedTime);
        txtFilename = findViewById(R.id.textFileName);


        //define decimal
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        //Format how many decimal places
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
            //Retrieve x, y, z axis
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            //Store result in magnitude
            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            //Formats magnitude in correct decimal format.
            magneticReading.setText((DECIMAL_FORMATTER.format(magnitude)));           // + " \u00B5Tesla"

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    //Program starts when user clicks Save.
    public void BtnPress(View view){
        System.out.println("Button Pressed");
        /*This line will print to the console where your file will be located on your device.
            This will allow you to navigate to the file using your file manager on your device.
         */
        System.out.println(MainActivity.this.getFilesDir().getAbsoluteFile());
        /*
            This will save data every 1/2 second and update the elapsed time counter.
            The elapsed variable is not associated with a time library. It will need to be updated to work with a time
            library. If this program runs for a long time it could possibly throw an error for since a double variable is being used
            instead of time function.

            https://stackoverflow.com/questions/46839264/get-sensor-data-every-1-10th-second
         */
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
        //Retrieves that last magnetic reading saved to the file.
        previousMagneticReading = findViewById(R.id.SavedValue);
        previousMagneticReading.setText(magneticReading.getText().toString());
        //Displays elapsed time
        time.setText(String.valueOf(elapsed));
        //Retrieves filename from user and saves data to the that filename.
        String fileName = txtFilename.getText().toString();
        String contents = previousMagneticReading.getText().toString() + "\n";
        //String test3 = "test3.txt";

        //Checking the availability state of the External Storage.
        /*Storing a file locally on the Android will need to be External
          Saving files to device storage:
          https://developer.android.com/training/data-storage/files
        */
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        // Basic I/O functionality
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