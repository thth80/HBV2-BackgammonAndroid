package school.throstur.backgammonandroid;

import android.graphics.Point;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class PawnMover {

    private static final int JUMPER = 0;
    private static final int TELEPORT = 1;

    private Pawn movingPawn;
    private int id, currTargetIndex;
    private int[] updateProtocols;
    private Point[] targetsOnTheWay;
    private boolean isActive;

    public PawnMover()
    {
        id = -1;
        movingPawn = null;
        currTargetIndex = AnimationCoordinator.NOT_AN_ID;
        updateProtocols = new int[]{JUMPER, JUMPER, JUMPER, JUMPER};
        isActive = false;
        targetsOnTheWay = null;
    }

    public int update(int deltaMs)
    {
        return AnimationCoordinator.NOT_AN_ID;
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

    public void receiveDirections(Pawn pawn, double targetX, double targetY, int id)
    {
        this.id = id;
        movingPawn = pawn;

    }

    //TODO AE: Hafa þetta frekar static, þar sem stillingar allra breytast við þetta?
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

    public void render(String ctx)
    {
        movingPawn.render(ctx);
    }
}
