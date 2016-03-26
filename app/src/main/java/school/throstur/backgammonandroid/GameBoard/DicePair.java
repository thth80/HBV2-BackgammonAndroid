package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class DicePair {
    private static final Rect BOUNDS = new Rect(-50, -50, 50, 50);

    private int imageId;
    private boolean isWhite, isAnimating;
    private int first, second, firstCopy, secondCopy;
    private int animTimeLeft, timeBtwFlips;

    private Bitmap spriteSheet;

    public DicePair(int team)
    {
        imageId = 666;
        isWhite = (team == Utils.TEAM_WH);
        spriteSheet = (isWhite)? BitmapFactory.decodeResource(AnimationCoordinator.ctx.getResources(), R.drawable.red_strip) :
                                  BitmapFactory.decodeResource(AnimationCoordinator.ctx.getResources(), R.drawable.blue_strip);

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

        //TODO AE: Setja rétt relative gildi hérna sem færa teningana
        canvas.save();
        if(isWhite)
            canvas.translate((float)200, (float) 300);
        else
            canvas.translate((float)500, (float)300 );

        canvas.drawBitmap(spriteSheet, source, BOUNDS, null);
        canvas.restore();
    }
}
