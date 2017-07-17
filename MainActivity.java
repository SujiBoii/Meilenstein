package com.example.kerem.meilenstein;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    public SensorManager mSensorManager;
    public boolean accel_cb, grav_cb, gyro_cb;
    public TextView  accLive,gyroLive,gravLive,richtungTV;
    public long accelt,gravt,gyrot;
    public Sensor accel,grav,gyro;

    StringBuilder builder = new StringBuilder();

    float [] history = new float[2];
    String [] direction = {"NONE","NONE"};

    

    FileOutputStream outputStream;

    private static final int PERMISSION_REQUEST_CODE = 1;

    File root = Environment.getExternalStorageDirectory();
    File dir = new File(root.getAbsolutePath()+"/Meilenstein");
    File file = new File(dir,"test.csv");

    Timestamp time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        initTextView();


    }

    public void initTextView(){
        accLive = (TextView)findViewById(R.id.textView5);
        gyroLive = (TextView)findViewById(R.id.textView6);
        gravLive = (TextView)findViewById(R.id.textView7);
        richtungTV = (TextView)findViewById(R.id.richtungTV);


    }

    public void registrieren(){

        if(accel_cb == true){
                accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
                accelt = System.currentTimeMillis();
        }
        if(grav_cb == true){
            grav = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            mSensorManager.registerListener(this, grav, SensorManager.SENSOR_DELAY_NORMAL);
            gravt= System.currentTimeMillis();
        }
        if(gyro_cb == true){
            gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mSensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);
            gyrot= System.currentTimeMillis();
        }




    }

    public void check(){
        accel_cb =  ((CheckBox)findViewById(R.id.accel_cb)).isChecked();
        grav_cb =  ((CheckBox)findViewById(R.id.grav_cb)).isChecked();
        gyro_cb =  ((CheckBox)findViewById(R.id.gyro_cb)).isChecked();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        HashMap<String, Double> values = new HashMap<String, Double>();





        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            values.put("x",x);
            values.put("y",y);
            values.put("z",z);
            accLive.setText(values.toString());
            //String teest = test.getText().toString();
            accLive.setText(values.toString());

            try {

                //outputStream.write(values.toString().getBytes());

                    String xx =time +","+ x + "," + y + "," + z;
                    outputStream.write(xx.getBytes());





            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*try {
                outputStream.write(values.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            float xChange = history[0] - event.values[0];
            history[0] = event.values[0];
            if (xChange > 2){
                direction[0] = "LEFT";
            }
            else if (xChange < -2){
                direction[0] = "RIGHT";
            }

            builder.setLength(0);
            builder.append("x: ");
            builder.append(direction[0]);

            richtungTV.setText(builder.toString());



            accelt=System.currentTimeMillis();
        }
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY ) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            values.put("x",x);
            values.put("y",y);
            values.put("z",z);
            gravLive.setText(values.toString());
            try {

                //outputStream.write(values.toString().getBytes());
                String yy = x+","+y+","+z+"\n";
                outputStream.write(yy.getBytes());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gravt=System.currentTimeMillis();
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE ) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            values.put("x",x);
            values.put("y",y);
            values.put("z",z);
            gyroLive.setText(values.toString());
            try {

               // outputStream.write(values.toString().getBytes());
                String zz = x+","+y+","+z+"\n";
                outputStream.write(zz.getBytes());


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gyrot=System.currentTimeMillis();

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Start Button
    public void startTracking(View view) {
        check();
        registrieren();


        String state;
        state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state))
        {

            if(!dir.exists()){
                dir.mkdir();
            }
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Am Speichern...", Toast.LENGTH_SHORT).show();



        }
        else
        {
         Toast.makeText(getApplicationContext(),"External SD Card nicht gefunden",Toast.LENGTH_LONG).show();
        }







    }


    //Stop Button
    public void stopTracking(View view){
        try {
            outputStream.close();
            Toast.makeText(this, "Gespeichert...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}
