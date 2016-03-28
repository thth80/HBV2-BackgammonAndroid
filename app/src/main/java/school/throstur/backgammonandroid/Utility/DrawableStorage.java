package school.throstur.backgammonandroid.Utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.GameBoard.Cube;
import school.throstur.backgammonandroid.GameBoard.DicePair;
import school.throstur.backgammonandroid.GameBoard.Pawn;
import school.throstur.backgammonandroid.InGameActivity;
import school.throstur.backgammonandroid.R;

/**
 * Created by Aðalsteinn on 28.3.2016.
 */
public class DrawableStorage {
    private static Drawable gameBoard = null;
    private static Drawable whitePawn = null;
    private static Drawable blackPawn = null;
    private static Drawable pawnShine = null;

    private static Bitmap redDiceSheet = null;
    private static Bitmap blueDiceSheet = null;
    private static Bitmap cubeSheet = null;
    private static Rect diceBounds = null;
    private static Rect cubeBounds = null;

    public static void setContext(Context ctx)
    {
        blackPawn = ContextCompat.getDrawable(ctx, R.drawable.black);
        whitePawn = ContextCompat.getDrawable(ctx, R.drawable.white);
        pawnShine =  ContextCompat.getDrawable(ctx, R.drawable.shine);
        gameBoard =  ContextCompat.getDrawable(ctx, R.drawable.game_board);

        redDiceSheet = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.red_strip) ;
        blueDiceSheet = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.blue_strip) ;
        cubeSheet = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.doublecube) ;
    }

    public static void setDimensions(int width, int height)
    {
        int left = (int)(-Pawn.RADIUS * width);
        int top = left;
        blackPawn.setBounds(left, top, -left, -top);
        whitePawn.setBounds(left, top, -left, -top);
        pawnShine.setBounds(left, top, -left, -top);
        gameBoard.setBounds(0, 0, width, height);

        left = top = (int)(-Cube.WIDTH/2 * width);
        cubeBounds = new Rect(left, top, -left, -top);

        left = top = (int)(-DicePair.WIDTH/2 * width);
        diceBounds = new Rect(left, top, -left, -top);
    }

    public static Bitmap getRedDice()
    {
        return redDiceSheet;
    }
    public static Bitmap getBlueDice()
    {
        return blueDiceSheet;
    }
    public static Bitmap getCube()
    {
        return cubeSheet;
    }
    public static Rect getDiceBounds()
    {
        return diceBounds;
    }
    public static Rect getCubeBounds()
    {
        return cubeBounds;
    }
    public static Drawable getShine()
    {
        return pawnShine;
    }
    public static Drawable getBlack()
    {
        return blackPawn;
    }
    public static Drawable getWhite()
    {
        return whitePawn;
    }
    public static Drawable getGameBoard()
    {
        return gameBoard;
    }


}
