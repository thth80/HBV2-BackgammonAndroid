package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class Pawn {
    public static final double PAWN_RADIUS = 0.2;
    public static final int LEFT = (int)(-PAWN_RADIUS * AnimationCoordinator.WIDTH);
    public static final int RIGHT = (int)(PAWN_RADIUS * AnimationCoordinator.WIDTH);
    public static final int TOP = (int)(-PAWN_RADIUS * AnimationCoordinator.HEIGHT);
    public static final int BOTTOM = (int)(PAWN_RADIUS * AnimationCoordinator.HEIGHT);

    private int  team;
    private double cx, cy, z;
    private double xVel, yVel, zVel;
    private Drawable pawnImage, hue;

    public Pawn(int team)
    {
        this.team = team;
        if(this.team == Utils.TEAM_BL)
            pawnImage = AnimationCoordinator.BLACK_PAWN;

        hue = AnimationCoordinator.SHINY_HUE;

        this.cx = cx;
        this.cy = cy;
        this.z = 1.0;
        xVel = yVel = zVel = 0;
    }

    public void updatePos(int msDelta)
    {
        cx += xVel*msDelta;
        cy += yVel*msDelta;
        z += zVel*msDelta;
    }
    public void halt()
    {
        xVel = yVel = zVel = 0;
    }

    public void verticalReverse()
    {
        zVel *= -1;
    }

    public void setVelocity(double xVel, double yVel)
    {
        this.xVel = xVel;
        this.yVel = yVel;
    }

    public void placeAt(double cx, double cy)
    {
        this.cx = cx;
        this.cy = cy;
    }

    public void applyVerticalForce(double zForce)
    {
        zVel += zForce;
    }

    public void changeXVelocity(double change)
    {
        xVel += change;
    }
    public void changeYVelocity(double change)
    {
        yVel += change;
    }

    public void putOnGround()
    {
        z = 1.0;
    }

    public void applyForces(double xForce, double yForce)
    {
        xVel += xForce;
        yVel += yForce;
    }

    public int getTeam()
    {
        return team;
    }

    public double getX()
    {
        return cx;
    }
    public double getY(){
        return cy;
    }
    public double getZ()
    {
        return z;
    }

    public void render(Canvas canvas)
    {
        //Það þarf líka alls ekki að endurtaka þetta
        pawnImage.setBounds(LEFT, TOP, RIGHT, BOTTOM );
        hue.setBounds(LEFT, TOP, RIGHT, BOTTOM);

        canvas.save();
        canvas.translate((float) cx, (float) cy);
        pawnImage.draw(canvas);
        hue.draw(canvas);
        canvas.restore();
    }

}
