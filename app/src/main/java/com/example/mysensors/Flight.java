package com.example.mysensors;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.mysensors.GameView.screenRatioX;
import static com.example.mysensors.GameView.screenRatioY;

public class Flight {

    boolean isGoingUp = false, isGoingDown = false, isGoingLeft = false, isGoingRight = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead,flight3,flight4;
    private GameView gameView;

    float velocityX, velocityY;
    float lastVelocityX, lastVelocityY;
    float maxSpeed = 20;
    float acceleration = .1f;


    Flight(GameView gameView, int screenY, Resources res) {
        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, R.drawable.knight1right);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2;
        x = (int) (64 * screenRatioX);
    }

    float lerp(float a, float b, float f)
    {
        float value = (float)(a * (1.0 - f) + (b * f));
        if (Math.abs(value) <= .05f) value = 0;
        return value;
    }

    void update() {
        if (isGoingUp) velocityY = lerp(velocityY, -maxSpeed * screenRatioY, acceleration); // Example movement speed, adjust as needed
        if (isGoingDown) velocityY = lerp(velocityY, maxSpeed * screenRatioY, acceleration);
        if (isGoingLeft) velocityX = lerp(velocityX, -maxSpeed * screenRatioX, acceleration);
        if (isGoingRight) velocityX = lerp(velocityX, maxSpeed * screenRatioX, acceleration);
        if (!isGoingUp || !isGoingDown || !isGoingLeft || !isGoingRight) {
            velocityX = lerp(velocityX, 0, acceleration);
            velocityY = lerp(velocityY, 0, acceleration);
        }

        x += velocityX;
        y += velocityY;

        // Ensure the flight does not move out of the screen
        x = Math.max(x, 0); // Prevent moving beyond the left edge
        x = Math.min(x, gameView.getWidth() - width); // Prevent moving beyond the right edge
        y = Math.max(y, 0); // Prevent moving beyond the top edge
        y = Math.min(y, gameView.getHeight() - height); // Prevent moving beyond the bottom edge

        if (velocityX != 0) lastVelocityX = velocityX;
        if (velocityY != 0) lastVelocityY = velocityY;
    }

    Bitmap getFlight () {
        /*
        if (isGoingUp) {
            return flight1;
        } else if (isGoingDown) {
            return flight2;
        } else if (isGoingLeft){
            return flight3;
        } else if (isGoingRight){
            return flight4;
        } else {
            return flight1;
        }
        */
        return flight1;
    }
    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }

}
