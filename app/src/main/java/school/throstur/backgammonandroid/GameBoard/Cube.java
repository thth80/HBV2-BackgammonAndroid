package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by Aðalsteinn on 20.3.2016.
 */
public class Cube {
    private static final Rect BOUNDS = new Rect(-30, -30, 30, 30);

    private int cubeVal;
    private boolean isAnimating;
    private Bitmap spriteSheet;

    public Cube(int val)
    {
        cubeVal = val;
        isAnimating = false;
    }

    public void startFlipping(int newVal)
    {
        cubeVal = newVal;
    }

    public void render(Canvas canvas)
    {

    }
}
