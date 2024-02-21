package com.example.mysensors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private Flight flight,flight2;

    private GameActivity activity;
    private Background background1;

    private MqttController mqttController;

    public GameView(GameActivity activity, int screenX, int screenY, Context context) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        mqttController = new MqttController(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        //sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenX / 4, screenY / 4, getResources(),1); // Spawn at 1/4th Y position
        flight2 = new Flight(this, screenX - screenX / 4, screenY / 4 - screenY / 4 , getResources(),2); // Spawn at 3/4th Y position

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);


        random = new Random();

    }

    @Override
    public void run() {
        mediaPlayer = MediaPlayer.create(this.getContext(), R.raw.music);
        mediaPlayer.start();

        while (isPlaying) {
            update ();
            draw ();
            sleep ();
        }

    }

    private void update () {
        String values = mqttController.values;
        Log.d("Joystick info:", values);

        if (!values.equals("")) {
            String[] splitValues = values.split(" ");

            flight.joyValX = Float.parseFloat(splitValues[1]);
            flight.joyValY = Float.parseFloat(splitValues[2]);
            flight2.joyValX = Float.parseFloat(splitValues[0]);
            flight2.joyValY = Float.parseFloat(splitValues[3]);
        }

        flight2.update(); //King elsa denna måste vara först
        flight.update();

        if(Rect.intersects(flight.getCollisionShape(),flight2.getCollisionShape()))
        {
            isGameOver = true;
        }
        if(Rect.intersects(flight.getSwordCollisionShape(), flight2.getSwordCollisionShape())) {
            bounceBack(flight, flight2);

            // Log.d("SwordCollision", "The swords have collided!");
        }
    }

    private void bounceBack(Flight flight1, Flight flight2) {
        // Simple bounce back logic: reverse direction and move back a bit
        final float bounceDistance = 50; // Adjust this value as needed

        // Reverse velocities
        flight1.velocityX *= -1;
        flight1.velocityY *= -1;
        flight2.velocityX *= -1;
        flight2.velocityY *= -1;

        // Move knights back to avoid stickiness
        flight1.x += Math.signum(flight1.velocityX) * bounceDistance;
        flight1.y += Math.signum(flight1.velocityY) * bounceDistance;
        flight2.x += Math.signum(flight2.velocityX) * bounceDistance;
        flight2.y += Math.signum(flight2.velocityY) * bounceDistance;

        // Ensure they are not moved outside of the screen bounds
        flight1.x = Math.max(flight1.x, 0);
        flight1.x = Math.min(flight1.x, screenX - flight1.width);
        flight1.y = Math.max(flight1.y, 0);
        flight1.y = Math.min(flight1.y, screenY - flight1.height);

        flight2.x = Math.max(flight2.x, 0);
        flight2.x = Math.min(flight2.x, screenX - flight2.width);
        flight2.y = Math.max(flight2.y, 0);
        flight2.y = Math.min(flight2.y, screenY - flight2.height);
    }


    private void draw () {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);

            if (isGameOver) {
                isPlaying = false;
                Bitmap gameOverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gameover_transparent);
                int centerX = (screenX - gameOverBitmap.getWidth()) / 2;
                int centerY = (screenY - gameOverBitmap.getHeight()) / 2;
                canvas.drawBitmap(gameOverBitmap, centerX, centerY, null);
                getHolder().unlockCanvasAndPost(canvas);
                waitBeforeExiting ();
                return;
            }

            float flightRotation = (float)Math.toDegrees(Math.atan2(flight.lastVelocityY, flight.lastVelocityX));
            float flightRotation2 = (float)Math.toDegrees(Math.atan2(flight2.lastVelocityY, flight2.lastVelocityX));

            Matrix matrix = new Matrix();
            Matrix matrix2 = new Matrix();

            matrix.postRotate(flightRotation);
            matrix2.postRotate(flightRotation2);

            Bitmap rotatedBitmap = Bitmap.createBitmap(flight.getFlight(), 0, 0, flight.getFlight().getWidth(), flight.getFlight().getHeight(), matrix, true);
            Bitmap rotatedBitmap2 = Bitmap.createBitmap(flight2.getFlight(), 0, 0, flight2.getFlight().getWidth(), flight2.getFlight().getHeight(), matrix2, true);

            canvas.drawBitmap(rotatedBitmap, flight.x, flight.y, paint);
            canvas.drawBitmap(rotatedBitmap2, flight2.x, flight2.y, paint);

            getHolder().unlockCanvasAndPost(canvas);

        }

    }

    private void waitBeforeExiting() {

        try {
            Thread.sleep(1000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void sleep () {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () {

        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: // Consider continuous movement while touching
                // Divide the screen into quadrants
                if (x < screenX / 2 && y < screenY / 2) {
                    // Top-left quadrant for moving up
                    flight.isGoingUp = true;
                    flight.isGoingLeft = true;
                    flight2.isGoingUp = true;
                    flight2.isGoingRight = true;
                } else if (x > screenX / 2 && y < screenY / 2) {
                    // Top-right quadrant for moving up
                    flight.isGoingUp = true;
                    flight.isGoingRight = true;
                    flight2.isGoingUp = true;
                    flight2.isGoingRight = true;
                } else if (x < screenX / 2 && y > screenY / 2) {
                    // Bottom-left quadrant for moving down
                    flight.isGoingDown = true;
                    flight.isGoingLeft = true;
                    flight2.isGoingDown = true;
                    flight2.isGoingLeft = true;
                } else if (x > screenX / 2 && y > screenY / 2) {
                    // Bottom-right quadrant for moving down
                    flight.isGoingDown = true;
                    flight.isGoingRight = true;
                    flight2.isGoingDown = true;
                    flight2.isGoingRight = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // Reset movement flags
                flight.isGoingUp = false;
                flight.isGoingDown = false;
                flight.isGoingLeft = false;
                flight.isGoingRight = false;
                flight2.isGoingUp = false;
                flight2.isGoingDown = false;
                flight2.isGoingLeft = false;
                flight2.isGoingRight = false;

                // Implement shooting or other actions if necessary

                break;
        }

        return true;
    }
     */

}
