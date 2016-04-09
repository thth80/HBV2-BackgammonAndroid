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
    public static final double WIDTH = 0.1;

    private boolean isWhite, isAnimating;
    private int first, second, firstCopy, secondCopy;
    private int animTimeLeft, timeBtwFlips;
    private double centerX;

    public DicePair(int team)
    {
        isWhite = (team == Utils.TEAM_WH);
        centerX = (isWhite)? 0.29 : 0.71 ;

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

    public void render(Canvas canvas)
    {
        Bitmap diceSheet = (isWhite)? DrawableStorage.getRedDice(): DrawableStorage.getBlueDice() ;
        Rect diceBounds = DrawableStorage.getDiceBounds();
        int diceWidth = diceBounds.width();

        int width = 60;
        int gap = 3;
        int leftOffsetOne = (this.first - 1) * (width + gap);
        Rect source = new Rect(leftOffsetOne, 0, leftOffsetOne + width, 60);

        int leftOffsetTwo = (this.second - 1)* (width + gap);
        Rect sourceTwo = new Rect(leftOffsetTwo, 0, leftOffsetTwo + width, 60);

        canvas.save();
        canvas.translate((float) (canvas.getWidth() * centerX), (float) (canvas.getHeight() * 0.5));
        canvas.translate((float)-diceWidth/2, 0.0f);
        canvas.drawBitmap(diceSheet, source, diceBounds, null);

        canvas.translate((float)diceWidth, 0.0f);
        canvas.drawBitmap(diceSheet, sourceTwo, diceBounds, null );

        canvas.restore();
    }
}
