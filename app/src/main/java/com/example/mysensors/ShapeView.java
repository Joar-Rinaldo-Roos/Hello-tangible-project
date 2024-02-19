package com.example.mysensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;
public class ShapeView extends View {
        private Paint paint;
        private int currentShape;
        private Random random;

    public ShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            random = new Random();
            currentShape = 0; // Start with first shape
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Choose a random color
            paint.setColor(Color.rgb(random.nextInt(180), random.nextInt(180), random.nextInt(180)));
            paint.setAlpha(0); // 128 is semi-transparent (50%)

            // Get center and size of the view
            int x = getWidth() / 2;
            int y = getHeight() / 2;
            int size = Math.min(x, y) / 2;

            // Draw shape based on currentShape value
            switch (currentShape) {
                case 0: // Circle
                    canvas.drawCircle(x, y, size, paint);
                    break;
                case 1: // Square
                    canvas.drawRect(x - size, y - size, x + size, y + size, paint);
                    break;
                case 2: // Triangle
                    drawTriangle(canvas, x, y, size);
                    break;
            }
        }

    private void drawTriangle(Canvas canvas, int x, int y, int size) {
        Path path = new Path();
        path.moveTo(x, y - size); // Top
        path.lineTo(x - size, y + size); // Bottom left
        path.lineTo(x + size, y + size); // Bottom right
        path.close(); // Closing the path to form a triangle

        canvas.drawPath(path, paint);
    }


    public void changeShape() {
            currentShape = (currentShape + 1) % 3; // Cycle through shapes
            invalidate(); // Redraw the view
        }
    }
