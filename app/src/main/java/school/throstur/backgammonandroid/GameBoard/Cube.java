package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;

/**
 * Created by Aðalsteinn on 20.3.2016.
 */
public class Cube {
    private int cubeVal;
    private boolean isAnimating;
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
