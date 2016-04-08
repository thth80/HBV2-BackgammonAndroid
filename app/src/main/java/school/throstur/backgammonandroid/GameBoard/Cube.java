package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import school.throstur.backgammonandroid.Utility.DrawableStorage;

/**
 * Created by Aðalsteinn on 20.3.2016.
 */
public class Cube {
    public static final double WIDTH = 0.06;

    private int cubeVal, cubeCopy;
    private boolean isAnimating;
    private int animTimeLeft, timeBtwFlips;

    public Cube(int val)
    {
        cubeVal = val;
        isAnimating = false;
        timeBtwFlips = 75;
    }

    public void startFlipping()
    {
        cubeVal += 1;
        isAnimating = true;
        animTimeLeft = 1350;
        cubeCopy = cubeVal;
    }

    public boolean update(int deltaMs)
    {
        int oldFlipsLeft = animTimeLeft/timeBtwFlips;
        animTimeLeft -= deltaMs;
        int currFlipsLeft = animTimeLeft/timeBtwFlips;

        if(oldFlipsLeft > currFlipsLeft)
            cubeVal = (cubeVal + 1)%6;

        if(animTimeLeft <= 0)
        {
            isAnimating = false;
            cubeVal = cubeCopy;
        }

        return isAnimating;
    }

    public void render(Canvas canvas)
    {
        int width = 90;
        int gap = 20;
        int leftOffset = (cubeVal - 1)*(width + gap);

        Rect source = new Rect(leftOffset, 0, leftOffset + width , 94);
        Bitmap cubeSheet = DrawableStorage.getCube();

        canvas.save();
        canvas.translate((float)(canvas.getWidth() * 0.065), (float)(canvas.getHeight() * 0.5));
        canvas.drawBitmap(cubeSheet, source ,DrawableStorage.getCubeBounds() , null );
        canvas.restore();
    }
}
