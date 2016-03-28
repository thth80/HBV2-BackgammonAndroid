package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.DrawableStorage;
import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class DicePair {
    public static final double WIDTH = 0.05;

    private boolean isWhite, isAnimating;
    private int first, second, firstCopy, secondCopy;
    private int animTimeLeft, timeBtwFlips;

    public DicePair(int team)
    {
        isWhite = (team == Utils.TEAM_WH);

        first = second = firstCopy = secondCopy = 1;
        isAnimating = false;
        animTimeLeft = 0;
        timeBtwFlips = 80;
    }

    public boolean update(int deltaMs)
    {
        int oldFlipsLeft = animTimeLeft/timeBtwFlips;
        animTimeLeft -= deltaMs;
        int currFlipsLeft = animTimeLeft/timeBtwFlips;

        if(oldFlipsLeft > currFlipsLeft)
        {
            int oldFirst = first;
            int oldSecond = second;
            while(oldFirst == first)
                first = 1 + (int)(Math.random()*6);
            while(oldSecond == second)
                second = 1 + (int)(Math.random()*6);
        }

        if(animTimeLeft <= 0)
        {
            isAnimating = false;
            first = firstCopy;
            second = secondCopy;
        }

        return isAnimating;
    }

    public boolean isRolling()
    {
        return isAnimating;
    }

    public void prepareForAnimation(int updatedFirst, int updatedSecond)
    {
        isAnimating = true;
        animTimeLeft = 1350;
        firstCopy = updatedFirst;
        secondCopy = updatedSecond;
    }

    public void setBoth(int first, int second)
    {
        this.first = first;
        this.second = second;
    }
    public void setFirst(int val)
    {
        first = val;
    }
    public void setSecond(int val)
    {
        second = val;
    }

    public void render(Canvas canvas)
    {
        int width = 60;
        int gap = 3;
        int leftOffset = (this.first - 1) * (width + gap);
        Rect source = new Rect(leftOffset, 0, leftOffset + width, width);

        canvas.save();
        if(isWhite)
            canvas.translate((float)(canvas.getWidth()*0.25), (float)(canvas.getHeight()*0.5));
        else
            canvas.translate((float)(canvas.getWidth() - canvas.getWidth()*0.25), (float)(canvas.getHeight()*0.5) );

        Bitmap diceSheet = (isWhite)? DrawableStorage.getRedDice(): DrawableStorage.getBlueDice() ;
        canvas.drawBitmap(diceSheet, source, DrawableStorage.getDiceBounds(), null);
        canvas.restore();
    }
}
