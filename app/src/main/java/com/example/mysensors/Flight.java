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
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private GameView gameView;

    Flight(GameView gameView, int screenY, Resources res) {
        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1);
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);

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


        if (wingCounter == 0) {
            wingCounter++;
            return flight1;
        }
        wingCounter--;

        return flight2;
    }
    Rect getCollisionShape() {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead() {
        return dead;
    }

}
