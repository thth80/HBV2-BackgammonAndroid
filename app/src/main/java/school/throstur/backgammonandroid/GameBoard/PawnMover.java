package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;
import android.graphics.Point;

import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.GameBoard.Pawn;

public class PawnMover {

    private static final int JUMPER = 0;
    private static final int TELEPORT = 1;

    private Pawn movingPawn;
    private int id, currTargetIndex;
    private int[] updateProtocols;
    private Point[] targetsOnTheWay;
    private boolean isActive;
    private double gravity;

    public PawnMover()
    {
        id = -1;
        movingPawn = null;
        currTargetIndex = AnimationCoordinator.NOT_AN_ID;
        updateProtocols = new int[]{JUMPER, JUMPER, JUMPER, JUMPER};
        isActive = false;
        targetsOnTheWay = null;
        gravity = 0;
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
        targetsOnTheWay = null;
        id = AnimationCoordinator.NOT_AN_ID;

        Pawn copy = movingPawn;
        movingPawn = null;
        return copy;
    }

    public void setProtocols(int[] newProto)
    {
        for(int i = 0; i < updateProtocols.length; i++)
            updateProtocols[i] = newProto[i];
    }

    public int update(int deltaMs)
    {
        if(this.updateProtocols[0] == TELEPORT)
            return this.id;
        else if(this.updateProtocols[0] == JUMPER)
            return updateStandardJumper(deltaMs);
        else
            return 0;
    }

    private boolean noMoreTargets()
    {
        return currTargetIndex >= targetsOnTheWay.length;
    }

    private int updateStandardJumper(int deltaMs)
    {
        movingPawn.updatePos(deltaMs);
        if(reachedTarget(movingPawn.getX(), movingPawn.getY(), getCurrentTarget()))
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

    private Point getCurrentTarget()
    {
        return targetsOnTheWay[currTargetIndex];
    }

    private boolean reachedTarget(double pawnCx, double pawnCy, Point target )
    {
        double xDistSquared = Math.pow(Math.abs(pawnCx - target.x ),2);
        double yDistSquared = Math.pow(Math.abs(pawnCy - target.y),2);
        double realDistSquared = xDistSquared + yDistSquared;
        return realDistSquared < 100;
    }

    private void initTargetList(double cx, double cy)
    {
        if(updateProtocols[0] == TELEPORT)
            movingPawn.placeAt(cx, cy);
        else if(updateProtocols[0] == JUMPER)
            initJumper(cx, cy);
    }

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
        targetsOnTheWay = new Point[2];
        targetsOnTheWay[0] = new Point((int)(movingPawn.getX()+ midXVect),(int)( movingPawn.getY() + midYVect));
        targetsOnTheWay[1] = new Point((int)cx, (int)cy);

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

    public void activate()
    {
        isActive = true;
    }

    public static int[] randomMovementSettings()
    {
        int[] randomProtocols = new int[4];
        randomProtocols[0] = JUMPER;
        randomProtocols[1] = JUMPER;
        randomProtocols[2] = JUMPER;
        randomProtocols[3] = JUMPER;

        return randomProtocols;
    }

    public void setToTeleport()
    {
        for(int i = 0; i< updateProtocols.length; i++)
            updateProtocols[i] = TELEPORT;
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
}
