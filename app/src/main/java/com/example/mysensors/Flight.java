package com.example.mysensors;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.example.mysensors.GameView.screenRatioX;
import static com.example.mysensors.GameView.screenRatioY;

public class Flight {

    int toShoot = 0;
    boolean isGoingUp = false, isGoingDown = false, isGoingLeft = false, isGoingRight = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead,flight3,flight4;
    private GameView gameView;


    Flight(GameView gameView, int screenY, Resources res) {
        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, R.drawable.knight1up);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.knight1down);
        flight3 = BitmapFactory.decodeResource(res, R.drawable.knight1left);
        flight4 = BitmapFactory.decodeResource(res, R.drawable.knight1right);


        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);
        flight3 = Bitmap.createScaledBitmap(flight3, width, height, false);
        flight4 = Bitmap.createScaledBitmap(flight4, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2;
        x = (int) (64 * screenRatioX);
}

    void update() {
        if (isGoingUp) y -= 3 * screenRatioY; // Example movement speed, adjust as needed
        if (isGoingDown) y += 3 * screenRatioY;
        if (isGoingLeft) x -= 3 * screenRatioX;
        if (isGoingRight) x += 3 * screenRatioX;

        // Ensure the flight does not move out of the screen
        x = Math.max(x, 0); // Prevent moving beyond the left edge
        x = Math.min(x, gameView.getWidth() - width); // Prevent moving beyond the right edge
        y = Math.max(y, 0); // Prevent moving beyond the top edge
        y = Math.min(y, gameView.getHeight() - height); // Prevent moving beyond the bottom edge
    }

    Bitmap getFlight () {
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


    }
    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }

}
