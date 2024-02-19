package com.example.mysensors;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Context;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class ShakeActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView ballAwns;
    private TextView speechText;
    private TextView endSpeech;
    private Button micButton;
    private Boolean shakeAble = false;
    private ShakeDetector mShakeDetector;
    private ShapeView shapeView;
    private MediaRecorder recorder;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private AnimatorSet pulsateAnimator;
    private static final int REQUEST_CODE_SPEECH_INPUT= 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake);


        ballAwns = findViewById(R.id.ballAwns);
        speechText = findViewById(R.id.speechText);
        endSpeech = findViewById(R.id.endSpeech);


        shapeView = (ShapeView) findViewById(R.id.shapeView);

        pulsateAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.pulsate);
        pulsateAnimator.setTarget(shapeView); // assuming shapeView is your shape's view


        shapeView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        checkPermission();
                        speak();
                        startSpeechRecognition();
                        pulsateAnimator.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        // User released the view - stop speech recognition
                        stopSpeechRecognition();
                        pulsateAnimator.end();
                        break;
                }
                return true;
            }
        });



        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            public void onShake(int count) {
                if (shakeAble) {
                    shapeView.changeShape();
                    ballAwns.setText(eightBallAwnser());
                    endSpeech.setText("");
                    vibrator.vibrate(500);
                    shakeAble = false; // Reset after showing the answer
                }
            }
        });


    }


    //https://stackoverflow.com/questions/16228817/android-speech-recognition-app-without-pop-up
    public void speak(){
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak Now");
        startActivityForResult(speechRecognizerIntent,88);

        try{
            startActivityForResult(speechRecognizerIntent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch(Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
}

//receive voice input and handle it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode ==RESULT_OK && null!=data){
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0));
                    shakeAble = true;
                }
            }
            break;

        }
        if (requestCode == 88 && resultCode == RESULT_OK){
            speechText.setText("You said: "+ data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0)+"?");
            endSpeech.setText("Shake me!");
            shakeAble = true;
        }
    }

    public String eightBallAwnser(){
        String[] responses = {
                "It is certain.",
                "It is decidedly so.",
                "Without a doubt.",
                "Yes - definitely.",
                "You may rely on it.",
                "As I see it, yes.",
                "Most likely.",
                "Outlook good.",
                "Yes.",
                "Signs point to yes.",
                "Reply hazy, try again.",
                "Ask again later.",
                "Better not tell you now.",
                "Cannot predict now.",
                "Concentrate and ask again.",
                "Don't count on it.",
                "My reply is no.",
                "My sources say no.",
                "Outlook not so good.",
                "Very doubtful."
        };

        Random random = new Random();
        int index = random.nextInt(responses.length);
        return responses[index];
    }


    public void startSpeechRecognition() {
        // Start listening to speech input
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }
    public void stopSpeechRecognition() {
        // Stop listening to speech input
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }


    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
    // In your Activity
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_AUDIO_PERMISSION_CODE);
        } else {
            // Permission has already been granted, you can start speech recognition
            startSpeechRecognition();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                startSpeechRecognition();
            } else {
                // Permission was denied, handle the failure
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}