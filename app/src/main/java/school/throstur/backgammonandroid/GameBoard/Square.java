package school.throstur.backgammonandroid.GameBoard;

import java.util.ArrayList;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class Square {
    private static final double HEIGHT = 450;

    private ArrayList<Pawn> pawns;
    private int position, count, highlighting;
    private double cx, bottomY;
    private boolean pointsDown;

    public Square(int pos, double cx)
    {
        position = pos;
        pawns = new ArrayList<>();
        this.cx = cx;
        pointsDown = (pos < 13);
        bottomY = (pointsDown)? HEIGHT*0.039 : HEIGHT*0.961;
        highlighting = Utils.NO_LIGHT;
    }

    //TODO AE: Athuga með að bæta þennan add/Remove protocol. Til dæmis láta add skila Pawn
    public Pawn removePawn()
    {
        Pawn removed = pawns.get(getCount() - 1);
        pawns.remove(getCount() - 1);
        return removed;
    }

    public void addPawn(Pawn pawn)
    {
        if(count == 1 && pawn.getTeam() == getFirstPawn().getTeam())
            pawns.remove(0);

        double availSpotCY = getAvailableSpot();
        pawn.placeAt(cx, availSpotCY);
        pawns.add(pawn);
    }
    //TODO AE: Gera eitthvað í þessum töfratölum
    public double getAvailableSpot()
    {
        double startingSpot = (pointsDown)?bottomY + 30: bottomY - 30 ;
        int offset = getCount();

        //Ætti þetta ekki að vera í hlutfalli við square stærðir, til dæmis bottomY?
        if(getCount() >= 8)
        {
            startingSpot = (pointsDown)? 90 : HEIGHT - 90;
            offset -= 8;
        }
        else if(getCount() >= 5)
        {
            startingSpot = (pointsDown)? 75 : HEIGHT - 75;
            offset -= 5;
        }
        double offsetMovement = (pointsDown)? offset * 60: offset * -60;
        return startingSpot + offsetMovement;
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

    public void render(String ctx)
    {
        for(Pawn pawn: pawns)
            pawn.render(ctx);

        if(highlighting != Utils.NO_LIGHT)
        {
            //Lýsa upp reitinn
        }
    }

}
