package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;

import java.util.ArrayList;

import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.GameBoard.Pawn;

public class PawnMover {

    private static final int JUMPER = 0;
    private static final int TELEPORT = 1;
    private static final int FLOATER = 7;

    private Pawn movingPawn;
    private int id, currTargetIndex;
    private int updateProtocol;
    private ArrayList<Double> XtargetsOnTheWay, YtargetsOnTheWay;
    private boolean isActive;

    public PawnMover()
    {
        id = -1;
        movingPawn = null;
        currTargetIndex = AnimationCoordinator.NOT_AN_ID;
        updateProtocol = JUMPER;
        isActive = false;
        XtargetsOnTheWay = YtargetsOnTheWay = null;
    }

    public int getId()
    {
        return id;
    }

    public Pawn shutDownAndReleasePawn()
    {
        movingPawn.halt();
        isActive = false;
        currTargetIndex = 0;
        XtargetsOnTheWay = YtargetsOnTheWay = null;
        id = AnimationCoordinator.NOT_AN_ID;

        Pawn copy = movingPawn;
        movingPawn = null;
        return copy;
    }

    public void setProtocol(int newProto)
    {
        updateProtocol = newProto;
    }

    public int update(int deltaMs)
    {
        if(this.updateProtocol == JUMPER)
            return updateStandardJumper(deltaMs);
        else if(updateProtocol == FLOATER)
            return updateFloater(deltaMs);
        else
            return 0;
    }

    private boolean noMoreTargets()
    {
        return currTargetIndex >= XtargetsOnTheWay.size();
    }

    private boolean reachedTarget(double pawnCx, double pawnCy )
    {
        double targetX = XtargetsOnTheWay.get(currTargetIndex);
        double targetY = YtargetsOnTheWay.get(currTargetIndex);

        double xDistSquared = Math.pow( pawnCx - targetX ,2);
        double yDistSquared = Math.pow( pawnCy - targetY ,2);
        double realDist = Math.sqrt(xDistSquared + yDistSquared);
        return realDist < 0.15;
    }

    private void initTargetList(double cx, double cy)
    {
        if(updateProtocol == FLOATER)
            initFloater(cx, cy);
        else if(updateProtocol == JUMPER)
            initJumper(cx, cy);
    }

    private int updateStandardJumper(int deltaMs)
    {
        movingPawn.updatePos(deltaMs);
        if(reachedTarget(movingPawn.getX(), movingPawn.getY()))
        {
            currTargetIndex++;
            if(noMoreTargets())
            {
                movingPawn.putOnGround();
                return id;
            }
            else
            {
                movingPawn.verticalReverse();
                return AnimationCoordinator.NOT_AN_ID;
            }
        }

        return AnimationCoordinator.NOT_AN_ID;
    }

    private int updateFloater(int deltaMs)
    {
        movingPawn.updatePos(1);
        if(reachedTarget(movingPawn.getX(), movingPawn.getY()))
            return id;
        else
            return AnimationCoordinator.NOT_AN_ID;

    }

    private void initFloater(double cx, double cy)
    {
        double xVector = cx - movingPawn.getX();
        double yVector = cy - movingPawn.getY();
        double xVel = xVector/100;
        double yVel = yVector/100;

        movingPawn.setVelocity(xVel, yVel);
        XtargetsOnTheWay = new ArrayList<>();
        YtargetsOnTheWay = new ArrayList<>();

        XtargetsOnTheWay.add(cx);
        YtargetsOnTheWay.add(cy);
    }

    private void initJumper(double cx, double cy)
    {
        double apexZ = 4.0;
        double zVel = 0.003;
        double timeToApex = (apexZ - movingPawn.getZ())/zVel;
        double midXVect = (cx - movingPawn.getX())/2;
        double midYVect = (cy - movingPawn.getY())/2;

        double xForce = midXVect/timeToApex;
        double yForce = midYVect/timeToApex;
        /*targetsOnTheWay = new Point[2];
        targetsOnTheWay[0] = new Point((int)(movingPawn.getX()+ midXVect),(int)( movingPawn.getY() + midYVect));
        targetsOnTheWay[1] = new Point((int)cx, (int)cy); */

        movingPawn.applyForces(xForce, yForce);
        movingPawn.applyVerticalForce(zVel);
    }

    public void receiveDirections(Pawn pawn, double targetX, double targetY, int id)
    {
        this.id = id;
        isActive = true;
        currTargetIndex = 0;
        movingPawn = pawn;

        initTargetList(targetX, targetY);
    }

    public static int randomMovementSettings()
    {
        int randomProtocol = FLOATER;
        return randomProtocol;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public void render(Canvas canvas)
    {
        canvas.save();
        canvas.scale((float)movingPawn.getZ(), (float)movingPawn.getZ());

        movingPawn.render(canvas);

        canvas.restore();
    }

    /*
    private void initLauncher(double cx, double cy)
    {
        gravity = -0.00003;
        double zLaunchVel = 0.011 + Math.random()*0.003;
        double timeToLandAgain = -2 * (zLaunchVel/gravity);

        double xVector = cx - movingPawn.getX();
        double yVector = cy - movingPawn.getY();
        double neededXVel = xVector/timeToLandAgain;
        double neededYVel = yVector/timeToLandAgain;

        movingPawn.applyForces(neededXVel, neededYVel);
        movingPawn.applyVerticalForce(zLaunchVel);

    }*/
}
