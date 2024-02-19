package com.example.mysensors;
import static java.lang.Math.round;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class AccelatorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView textViewX, textViewY, textViewZ;
    private MediaPlayer mediaPlayer;
    private final float alpha = 0.8f;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];

    private long lastUpdate = 1;
    private float last_z;
    private final int MOVEMENT_THRESHOLD = 50; // Increase this value to require more significant motion
    private final int UPDATE_INTERVAL = 50000; // Increase this value to slow down the update

    private final float UPWARDS_THRESHOLD = 9.8f;
    private final float FORWARD_THRESHOLD = 0.5f; // Consider some tolerance


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelator);
        textViewX = findViewById(R.id.textViewX);
        textViewY = findViewById(R.id.textViewY);
        textViewZ = findViewById(R.id.textViewZ);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mediaPlayer = MediaPlayer.create(this, R.raw.coin_c);
        last_z = 0;

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, 2000000);
        } else {

        }
    }


    //https://www.youtube.com/watch?v=gVszXHio7hU
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //float x = event.values[0];
            //float y = event.values[1];
            //float z = event.values[2];

            float x = (event.values[0]);
            float y = (event.values[1]);
            float z = (event.values[2]);


            if (Math.abs(x) < FORWARD_THRESHOLD && y > UPWARDS_THRESHOLD) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start(); // Play sound only if it's not already playing
                }
            }

            textViewX.setText(String.format("X : %.2f", x));
            textViewY.setText(String.format("Y : %.2f", y));
            textViewZ.setText(String.format("Z : %.2f", z));

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastUpdate) > UPDATE_INTERVAL) { // Update the interval
                float dz = Math.abs(event.values[2] - last_z); // Change in Z value
                last_z = event.values[2];

                if (dz > MOVEMENT_THRESHOLD) { // Threshold for detecting raise motion
                    mediaPlayer.start(); //
                }

                lastUpdate = currentTime;
            }


            Log.d("ACCELEROMETER", "X: " + x + " Y: " + y + " Z: " + z);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, 2000000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
