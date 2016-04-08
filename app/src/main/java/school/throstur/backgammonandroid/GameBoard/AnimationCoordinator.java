package school.throstur.backgammonandroid.GameBoard;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.Utility.DrawableStorage;
import school.throstur.backgammonandroid.Utility.Utils;

public class AnimationCoordinator {
    public static final int NOT_AN_ID = -1;

    private BoardManager board;
    private Pawn movingPawn;
    private ArrayList<HashMap<String, Integer>> moves, delayedMoves;
    private int currAnimIndex;
    private int[] lastWhiteLighted;
    boolean isSequential, pawnsAreMoving, diceAreStillRolling, isDelaying, cubeIsFlipping;

    private AnimationCoordinator(Context context)
    {
        moves = null;
        movingPawn = null;

        currAnimIndex = 0;
        isSequential = false;
        pawnsAreMoving = false;
        diceAreStillRolling = false;
        cubeIsFlipping = false;

        DrawableStorage.setContext(context);
    }

    public static AnimationCoordinator buildNewBoard(Context context)
    {
        AnimationCoordinator newBoard = new AnimationCoordinator(context);
        newBoard.board = BoardManager.newStartingBoard();
        return newBoard;
    }

    public static AnimationCoordinator buildExistingBoard(int[] teams, int[] counts, int[] diceVals, int cube, Context context)
    {
        AnimationCoordinator existingBoard = new AnimationCoordinator(context);
        existingBoard.board = BoardManager.buildExistingBoard(teams, counts, diceVals, cube);
        return existingBoard;
    }

    public boolean isMovingSinglePawn()
    {
        return movingPawn != null;
    }

    public void highlightGreens(int[] greens)
    {
        for(int i = 0; i < greens.length; i++)
            board.greenLightSquare(greens[i]);
    }

    public int wasWhiteSquareClicked(double cx, double cy)
    {
        Square square = board.getClickedSquare(cx , cy);
        if(square == null || square.getLighting() != Utils.WHITE_LIGHT)
            return CanvasFragment.NO_SQUARE;
        else
            return square.getPosition();
    }

    public void movePawnTo(double cx, double cy)
    {
        movingPawn.placeAt(cx, cy);
    }

    public void pickUpPawnAt(int pos)
    {
        movingPawn = board.getPawnFrom(pos);
    }

    public void addPawnTo(int pos)
    {
        board.addPawnTo(movingPawn, pos);
        movingPawn = null;
    }

    public int wasGreenSquareBelow(double cx, double cy)
    {
        Square square = board.getClickedSquare(cx, cy);
        if(square == null || square.getLighting() != Utils.GREEN_LIGHT)
            return CanvasFragment.NO_SQUARE;
        else
            return square.getPosition();
    }

    public boolean arePawnsMoving()
    {
        return pawnsAreMoving;
    }

    public boolean isAnimating()
    {
        return pawnsAreMoving || cubeIsFlipping || diceAreStillRolling;
    }

    public void startDiceRoll(int first, int second, int team)
    {
        diceAreStillRolling = true;
        if(team == Utils.TEAM_WH)
            board.startWhiteDiceRoll(first, second);
        else
            board.startBlackDiceRoll(first, second);
    }

    public void startCubeFlipping()
    {
        board.startCubeFlipping();
        cubeIsFlipping = true;
    }

    public void delayWhiteLighting(int[] positions)
    {
        lastWhiteLighted = positions;
        isDelaying = true;
    }

    public boolean isRollingDice()
    {
        return diceAreStillRolling;
    }
    public boolean isFlippingCube()
    {
        return cubeIsFlipping;
    }

    public void storeDelayedMoves(ArrayList<HashMap<String, Integer>> delayed)
    {
        delayedMoves = delayed;
    }

    public void initPawnAnimation(ArrayList<HashMap<String, Integer>> moves)
    {
        pawnsAreMoving = true;
        currAnimIndex = 0;
        this.moves = moves;
        isSequential = isSequenceNeeded();

        if(isSequential)
        {
            board.resetMovementSettings(true);
            HashMap<String, Integer> nextMove = getCurrentMove();
            board.setUpNextMove(nextMove.get("from"), nextMove.get("to"), nextMove.get("killMove"), currAnimIndex);
        }
        else
        {
            board.resetMovementSettings(false);
            for(HashMap<String, Integer> move: this.moves)
                if(move.get("killMove").equals(0))
                    board.setUpNextMove(move.get("from"), move.get("to"), move.get("killMove"), currAnimIndex);
        }

    }

    public void updatePawns(int deltaMs)
    {
        if(isSequential)
            sequentialUpdate(deltaMs);
        else
            batchUpdate(deltaMs);
    }

    public boolean updateDice(int deltaMs)
    {
        diceAreStillRolling = board.updateDice(deltaMs);
        if(!diceAreStillRolling && isDelaying)
        {
            Log.d("MATCH", "Dice were rolling, now we whitelight squares");
            isDelaying = false;
            whiteLightSquares(lastWhiteLighted);
        }
        return diceAreStillRolling;
    }

    public void updateCube(int deltaMs)
    {
        cubeIsFlipping = board.updateCube(deltaMs);
    }

    public ArrayList<HashMap<String, Integer>> getStoredMoves()
    {
        return delayedMoves;
    }

    public boolean areMovesStored()
    {
        return delayedMoves != null;
    }

    public void emptyStorage()
    {
        delayedMoves = null;
    }

    public void removeLastWhitelighted()
    {
        lastWhiteLighted = new int[0];
    }

    public void render(Canvas canvas)
    {
        board.render(canvas);
    }

    private void sequentialUpdate(int deltaMs)
    {
        ArrayList<Integer> finishedIds = board.updateMovers(deltaMs);
        if(finishedIds.size() != 0)
        {
            board.finishMove(currAnimIndex, getCurrentMove().get("to"));
            currAnimIndex++;
            if(currAnimIndex < moves.size())
            {
                HashMap<String, Integer> nextMove = getCurrentMove();
                board.setUpNextMove(nextMove.get("from"), nextMove.get("to"),nextMove.get("killMove"), currAnimIndex);
            }
            else
                pawnsAreMoving = false;
        }
    }

    private void batchUpdate(int deltaMs)
    {
        ArrayList<Integer> finishedIndexes = board.updateMovers(deltaMs);
        if(finishedIndexes.size() == 0) return;

        for(Integer index: finishedIndexes)
            board.finishMove(index, moves.get(index).get("to"));

        for(int i = finishedIndexes.size() - 1; i >= 0; i--)
        {
            startKilledMoveIfPossible(finishedIndexes.get(i));
            moves.remove(finishedIndexes.get(i));
            moves.get(finishedIndexes.get(i)).put("finished", 1);
        }

        pawnsAreMoving = false;
        for(HashMap<String, Integer> move: moves)
        {
            int finished = move.get("finished");
            if(finished == 0) pawnsAreMoving = true;
        }

    }

    //TODO AE: Sannreyna að þessi aðferð virki sem skildi
    private void startKilledMoveIfPossible(int index)
    {
        HashMap<String, Integer> finishedMove = moves.get(index);
        for(int i = 0; i < moves.size(); i++)
        {
            HashMap<String, Integer> move = moves.get(index);
            if(move.get("finished").equals(0) && move.get("killMove").equals(1) && move.get("from").equals(finishedMove.get("to")))
            {
                board.setUpNextMove(move.get("from"), move.get("to"), 0, i);
                return;
            }
        }
    }

    private HashMap<String, Integer> getCurrentMove()
    {
        return moves.get(currAnimIndex);
    }

    private boolean isSequenceNeeded()
    {
        for(int i = 0; i < moves.size(); i++)
            for(int k = 0; k < moves.size(); k++)
                if(moves.get(i).get("to").equals(moves.get(k).get("from")))
                    return true;

        return false;
    }

    public int[] getLastWhiteLighted()
    {
        return lastWhiteLighted;
    }

    public void greenLightSquares(int[] positions)
    {
        for(int pos: positions)
            board.greenLightSquare(pos);
    }
    public void whiteLightSquares(int[] positions)
    {
        lastWhiteLighted = positions;
        for(int pos: positions)
            board.whiteLightSquare(pos);
    }
    public void unHighlightAll(){
        board.unHighlightAll();
    }
}
