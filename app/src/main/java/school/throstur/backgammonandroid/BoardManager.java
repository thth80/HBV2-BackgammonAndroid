package school.throstur.backgammonandroid;

import java.util.ArrayList;

public class BoardManager {
    public class Cube{
        private int value;
        public Cube(int val)
        {
            value = val;
        }
        public void flip()
        {
            value *= 2;
        }
    }

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
        doublingCube = new Cube(1);
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

    //TODO AE: Vera handviss um að Integer sé ekki að valda neinum vandræðum
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

    public void setUpNextMove(int fromPos, int toPos, int killMove, int moveId)
    {
        Pawn removedPawn = pickUpDepartingPawn(fromPos);
        if(killMove == 1)
            killedPawns[toPos] = squares[toPos].getFirstPawn();

        double landingSpotY = squares[toPos].getAvailableSpot();
        double landingSpotX = squares[toPos].getX();
        findAvailableMover().receiveDirections(removedPawn, landingSpotX, landingSpotY, moveId);
    }

    private PawnMover findAvailableMover()
    {
        if(!batchMode) return pawnMovers[0];
        for(PawnMover mover: pawnMovers)
            if(mover.isActive())
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

    public void startBlackDiceRoll(int first, int second)
    {
        blackDice.prepareForAnimation(first, second);
    }
    public void startWhiteDiceRoll(int first, int second)
    {
        whiteDice.prepareForAnimation(first, second);
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

    //TODO AE: Þetta nafn er lélegt.
    public void resetMovementSettings(boolean isSequential)
    {
        batchMode = !isSequential;
        int[] randSettings = PawnMover.randomMovementSettings();
        if(isSequential)
            pawnMovers[0].setProtocols(randSettings);
        else
            for(PawnMover mover: pawnMovers)
                mover.setProtocols(randSettings);

    }

    public void greenLightSquare(int pos)
    {
        squares[pos].greenLight();
    }
    public void whiteLightSquare(int pos)
    {
        squares[pos].whiteLight();
    }
    public void unHighlightSquare(int pos)
    {
        squares[pos].unHighlight();
    }
    public void unHighlightAll()
    {
        for(Square sq: squares)
            sq.unHighlight();
    }

    public void prepareTeleport()
    {
        for(PawnMover mover: pawnMovers)
            mover.setToTeleport();
    }

    private void flipCube()
    {
        doublingCube.flip();
    }

    public void render(String ctx)
    {
        //teikna borð
        //teikna squares(peðin)
        //teikna teningapör
        //teikna cube
        for(PawnMover mover: pawnMovers)
            if(mover.isActive())
                mover.render(ctx);
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
                cx = Utils.WIDTH - offset*Utils.SQUARE_WIDTH - 0.133*Utils.WIDTH;
            else
                cx = Utils.WIDTH*0.102 + offset*Utils.SQUARE_WIDTH + Utils.SQUARE_WIDTH*0.5;

            squares[pos] = new Square(pos, cx);
        }

        Square whiteDeadZone = new Square(25, Utils.WIDTH*0.5);
        whiteDeadZone.setBottomY(Utils.HEIGHT*0.8);
        squares[25] = whiteDeadZone;

        Square blackDeadZone = new Square(0, Utils.WIDTH*0.5);
        blackDeadZone.setBottomY(Utils.HEIGHT*0.2);
        squares[0] = blackDeadZone;

        Square whiteEndZone = new Square(26, Utils.WIDTH*0.94);
        whiteEndZone.makePointDown();
        whiteEndZone.setBottomY(Utils.HEIGHT*0.039);
        squares[26] = whiteEndZone;

        Square blackEndZone = new Square(27, Utils.WIDTH*0.94);
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
    }



}
