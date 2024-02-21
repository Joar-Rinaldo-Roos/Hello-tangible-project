package com.example.mysensors;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.mysensors.GameView.screenRatioX;
import static com.example.mysensors.GameView.screenRatioY;

public class Flight {
    boolean isGoingUp = false, isGoingDown = false, isGoingLeft = false, isGoingRight = false;
    int x, y, width, height;
    Bitmap flight1, flight2, dead;
    private GameView gameView;

    float velocityX, velocityY;
    float lastVelocityX, lastVelocityY;
    float maxSpeed = 20;
    float acceleration = .1f;



    Flight(GameView gameView,int x, int y, Resources res,int player) {
        this.gameView = gameView;
        this.x = x; // Set the initial X position
        this.y = y; // Set the initial Y position
        if (player ==1){
            flight1 = BitmapFactory.decodeResource(res, R.drawable.knight1right);
        } else {
            flight1 = BitmapFactory.decodeResource(res, R.drawable.knight2);
        }


        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);

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
        return flight1;
    }

    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    // Add method in Flight class to get the sword's collision shape
    Rect getSwordCollisionShape() {
        int swordOffsetX = width - (width / 4); // Example offset, adjust according to your sprite
        int swordOffsetY = height / 4; // Adjust as needed
        int swordWidth = width / 4; // Assuming sword width is a quarter of the knight's width
        int swordHeight = (3 * height) / 4; // Assuming sword height is three-quarters of the knight's height

        return new Rect(x + swordOffsetX, y + swordOffsetY, x + swordOffsetX + swordWidth, y + swordOffsetY + swordHeight);
    }




}
