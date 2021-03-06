package school.throstur.backgammonandroid.GameBoard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import school.throstur.backgammonandroid.Utility.DrawableStorage;
import school.throstur.backgammonandroid.Utility.Utils;

public class BoardManager {

    private Square[] squares;
    private Pawn[] killedPawns;
    private PawnMover[] pawnMovers;
    private Cube doublingCube;
    private DicePair whiteDice, blackDice;
    private boolean batchMode;

    public BoardManager()
    {
        squares = new Square[28];
        killedPawns = new Pawn[28];
        pawnMovers = new PawnMover[]{new PawnMover(), new PawnMover(), new PawnMover(), new PawnMover()};
        doublingCube = new Cube(0);
        whiteDice = new DicePair(Utils.TEAM_WH);
        blackDice = new DicePair(Utils.TEAM_BL);
        batchMode = false;

        addSquares();
    }

    public static BoardManager newStartingBoard()
    {
        BoardManager newBoard = new BoardManager();
        newBoard.installPawns();
        return newBoard;
    }

    public static BoardManager buildExistingBoard(int[] teams, int[] counts, int[] diceVals, int cube)
    {
        BoardManager custom = new BoardManager();
        custom.installCustomPawns(teams, counts);
        custom.setDicePairs(diceVals);
        custom.setCube(cube);

        return custom;
    }

    public Pawn getPawnFrom(int pos)
    {
        return squares[pos].removePawn();
    }
    public void addPawnTo(Pawn pawn, int pos)
    {
        Pawn killed = squares[pos].addPawn(pawn);
        if(killed != null)
        {
            if(killed.getTeam() == Utils.TEAM_WH)
                squares[25].addPawn(killed);
            else
                squares[0].addPawn(killed);
        }
    }

    public boolean updateCube(int deltaMs)
    {
        return doublingCube.update(deltaMs);
    }

    //Skilar true ef teningar eru animating, annars false
    public boolean updateDice(int deltaMs)
    {
        if(blackDice.isRolling())
            return blackDice.update(deltaMs);
        else
            return whiteDice.update(deltaMs);
    }

    public ArrayList<Integer> updateMovers(int deltaMs)
    {
        ArrayList<Integer> finishedIds = new ArrayList<>();
        for(PawnMover mover: pawnMovers)
            if(mover.isActive())
            {
                int returnedId = mover.update(deltaMs);
                if(returnedId != AnimationCoordinator.NOT_AN_ID)
                    finishedIds.add(returnedId);
            }

        return finishedIds;
    }

    public void setUpNextMove(int fromPos, int toPos, int killMove, int moveId)
    {
        Pawn removedPawn = pickUpDepartingPawn(fromPos);
        if(killMove == 1)
            killedPawns[toPos] = squares[toPos].getFirstPawn();

        double landingSpotY = squares[toPos].getAvailableSpot();
        double landingSpotX = squares[toPos].getX();
        findAvailableMover().receiveDirections(removedPawn, landingSpotX, landingSpotY, moveId);
    }

    public void finishMove(int moveId, int moveTo)
    {
        Pawn landingPawn = getMoverById(moveId).shutDownAndReleasePawn();
        squares[moveTo].addPawn(landingPawn);
    }

    private PawnMover getMoverById(int id)
    {
        for(PawnMover mover: pawnMovers)
            if(mover.getId() == id)
                return mover;
        return null;
    }

    private PawnMover findAvailableMover()
    {
        if(!batchMode) return pawnMovers[0];

        for(PawnMover mover: pawnMovers)
            if(!mover.isActive())
                return mover;
        return null;
    }

    private Pawn pickUpDepartingPawn(int fromPos)
    {
        if(killedPawns[fromPos] == null)
            return squares[fromPos].removePawn();
        else
        {
            Pawn alreadyKilled = killedPawns[fromPos];
            killedPawns[fromPos] = null;
            return alreadyKilled;
        }
    }

    public Square getClickedSquare(double cx, double cy)
    {
        for(Square square: squares)
            if(square.isInside(cx, cy))
                return square;

        return null;
    }

    public void startBlackDiceRoll(int first, int second)
    {
        blackDice.prepareForAnimation(first, second);
    }
    public void startWhiteDiceRoll(int first, int second)
    {
        whiteDice.prepareForAnimation(first, second);
    }
    public void startCubeFlipping()
    {
        doublingCube.startFlipping();
    }

    private void setDicePairs(int[] vals)
    {
        whiteDice = new DicePair(Utils.TEAM_WH);
        blackDice = new DicePair(Utils.TEAM_BL);
        whiteDice.setBoth(vals[0], vals[1]);
        blackDice.setBoth(vals[2], vals[3]);
    }
    private void setCube(int val)
    {
        doublingCube = new Cube(val);
    }

    public void resetMovementSettings(boolean isSequential)
    {
        batchMode = !isSequential;
        int randSetting = PawnMover.randomMovementSettings();
        if(isSequential)
            pawnMovers[0].setProtocol(randSetting);
        else
            for(PawnMover mover: pawnMovers)
                mover.setProtocol(randSetting);
    }

    public void greenLightSquare(int pos)
    {
        squares[pos].greenLight();
    }
    public void whiteLightSquare(int pos) { squares[pos].whiteLight(); }

    public void unHighlightAll()
    {
        for(Square sq: squares)
            sq.unHighlight();
    }


    public void render(Canvas canvas)
    {
        Drawable gameBoard = DrawableStorage.getGameBoard();
        gameBoard.draw(canvas);

        for(Square square: squares)
            square.render(canvas);

        whiteDice.render(canvas);
        blackDice.render(canvas);
        doublingCube.render(canvas);

        for(PawnMover mover: pawnMovers)
            if(mover.isActive())
                mover.render(canvas);
    }


    //Aðferðir hér fyrir neðan skipta litlu mali

    private void addSquares()
    {
        int offset;
        double cx;
        for(int pos = 1; pos < 25; pos++)
        {
            if(pos > 18)
                offset = 24 - pos;
            else if(pos > 12)
                offset = pos - 13;
            else if(pos > 6)
                offset = 12 - pos;
            else
                offset = pos - 1;

            if(pos > 18 || pos < 7)
                cx = 1.0 - offset * Square.WIDTH - 0.133;
            else
                cx = 0.135 + offset * Square.WIDTH;

            squares[pos] = new Square(pos, cx);
        }

        Square whiteDeadZone = new Square(25, 0.5);
        whiteDeadZone.setBottomY(0.8);
        whiteDeadZone.setCY();
        squares[25] = whiteDeadZone;

        Square blackDeadZone = new Square(0, 0.5);
        blackDeadZone.setBottomY(0.2);
        blackDeadZone.setCY();
        squares[0] = blackDeadZone;

        Square whiteEndZone = new Square(26, 0.94);
        whiteEndZone.makePointDown();
        whiteEndZone.setBottomY(0.039);
        whiteEndZone.setCY();
        squares[26] = whiteEndZone;

        Square blackEndZone = new Square(27, 0.94);
        squares[27] = blackEndZone;
        //Gera eitthvað með bounding boxes hér eins og í JS?
    }

    private void installCustomPawns(int[] teams, int[] counts)
    {
        for(int pos = 0; pos < teams.length; pos++)
        {
            int count = counts[pos];
            if(count == 0) continue;

            Square current = squares[pos];
            int team = teams[pos];
            for(int k = 0; k < count; k++)
                current.addPawn(new Pawn(team));
        }
    }

    private void installPawns()
    {
        Square blackTower1 = squares[19];
        Square blackTower2 = squares[12];
        Square whiteTower1 = squares[6];
        Square whiteTower2 = squares[13];
        for(int i = 0; i < 5; i++)
        {
            blackTower1.addPawn(new Pawn(Utils.TEAM_BL));
            blackTower2.addPawn(new Pawn(Utils.TEAM_BL));
            whiteTower1.addPawn(new Pawn(Utils.TEAM_WH));
            whiteTower2.addPawn(new Pawn(Utils.TEAM_WH));
        }

        Square blackTriple = squares[17];
        Square whiteTriple = squares[8];
        for(int i = 0; i < 3; i++)
        {
            blackTriple.addPawn(new Pawn(Utils.TEAM_BL));
            whiteTriple.addPawn(new Pawn(Utils.TEAM_WH));
        }

        Square blackRunners = squares[1];
        Square whiteRunners = squares[24];
        for(int i = 0; i < 2; i++)
        {
            blackRunners.addPawn(new Pawn(Utils.TEAM_BL));
            whiteRunners.addPawn(new Pawn(Utils.TEAM_WH));
        }
    }



}
