package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class Square {
    public static final double WIDTH = 0.06;
    public static final double HEIGHT = 0.25;
    private static final Rect BOUNDS = new Rect((int)(-WIDTH/2),(int)(-HEIGHT/2),
                        (int)(WIDTH/2), (int)(HEIGHT/2));

    private ArrayList<Pawn> pawns;
    private int position, highlighting;
    private double cx, bottomY, cy;
    private boolean pointsDown;

    public Square(int pos, double cx)
    {
        position = pos;
        pawns = new ArrayList<>();
        this.cx = cx;
        pointsDown = (pos < 13);
        bottomY = (pointsDown)? 0.039 : 0.961;

        this.cy = (pointsDown)? bottomY + Utils.SQUARE_HEIGHT/2 : bottomY - Utils.SQUARE_HEIGHT/2 ;

        highlighting = Utils.NO_LIGHT;
    }

    public int getLighting()
    {
        return highlighting;
    }
    public int getPosition()
    {
        return position;
    }

    public Pawn removePawn()
    {
        Pawn removed = pawns.get(getCount() - 1);
        pawns.remove(getCount() - 1);
        return removed;
    }

    public Pawn addPawn(Pawn pawn)
    {
        Pawn removedPawn = null;
        if(getCount() == 1 && pawn.getTeam() != getFirstPawn().getTeam())
            removedPawn = pawns.remove(0);

        double availSpotCY = getAvailableSpot();
        pawn.placeAt(cx, availSpotCY);
        pawns.add(pawn);
        return removedPawn;
    }

    public double getAvailableSpot()
    {
        //Rétt hlutföll eru 1/3/2.5/2
        double startingSpot = (pointsDown)? bottomY + 0.04: bottomY - 0.04 ;
        int offset = getCount();

        //Ætti þetta ekki að vera í hlutfalli við square stærðir, til dæmis bottomY?
        if(getCount() >= 8)
        {
            startingSpot = (pointsDown)? 0.12 : - 0.12;
            offset -= 8;
        }
        else if(getCount() >= 5)
        {
            startingSpot = (pointsDown)? 0.1 : - 0.1;
            offset -= 5;
        }
        double offsetMovement = (pointsDown)? offset * 0.08: offset * -0.08;
        return startingSpot + offsetMovement;
    }

    public boolean isInside(double cx, double cy)
    {
        return Math.abs(cx - this.cx) <= WIDTH/2 && Math.abs(cy - this.cy) <= HEIGHT/2;
    }

    public double getX()
    {
        return cx;
    }

    public void makePointDown()
    {
        pointsDown = true;
    }

    public void whiteLight()
    {
        highlighting = Utils.WHITE_LIGHT;
    }
    public void greenLight()
    {
        highlighting = Utils.GREEN_LIGHT;
    }

    public void unHighlight()
    {
        highlighting = Utils.NO_LIGHT;
    }

    public void setBottomY(double y)
    {
        bottomY = y;
    }

    public int getCount()
    {
        return pawns.size();
    }

    public Pawn getFirstPawn()
    {
        return pawns.get(0);
    }

    public void render(Canvas canvas)
    {
        for(Pawn pawn: pawns)
            pawn.render(canvas);

        if(highlighting == Utils.GREEN_LIGHT)
        {
            Paint paint = new Paint();
            paint.setARGB(110, 0, 255, 0);

            canvas.save();
            canvas.translate((float)cx, (float)cy);
            canvas.drawRect(BOUNDS, paint);
            canvas.restore();
        }
        else if(highlighting == Utils.WHITE_LIGHT)
        {
            Paint paint = new Paint();
            paint.setARGB(110, 255, 255, 255);

            canvas.save();
            canvas.translate((float)cx, (float)cy);
            canvas.drawRect(BOUNDS, paint);
            canvas.restore();
        }
    }

}
