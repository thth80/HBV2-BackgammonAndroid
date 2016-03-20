package school.throstur.backgammonandroid;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AnimationCoordinator {
    public static final int NOT_AN_ID = -1;

    private BoardManager board;
    private ArrayList<HashMap<String, Integer>> moves;
    private int currAnimIndex;
    private int[] lastWhiteLighted;
    boolean isSequential, pawnsAreMoving, diceAreRolling, isDelaying, cubeIsFlipping;

    public AnimationCoordinator()
    {
        moves = null;
        currAnimIndex = 0;
        isSequential = false;
        pawnsAreMoving = false;
        diceAreRolling = false;
        cubeIsFlipping = false;
    }

    public static AnimationCoordinator buildNewBoard()
    {
        AnimationCoordinator newBoard = new AnimationCoordinator();
        newBoard.board = BoardManager.newStartingBoard();
        return newBoard;
    }

    public static AnimationCoordinator buildExistingBoard(int[] teams, int[] counts, int[] diceVals, int cube)
    {
        AnimationCoordinator existingBoard = new AnimationCoordinator();
        existingBoard.board = BoardManager.buildExistingBoard(teams, counts, diceVals, cube);
        return existingBoard;
    }

    public void startDiceRoll(int first, int second, int team)
    {
        diceAreRolling = true;
        if(team == Utils.TEAM_WH)
            board.startWhiteDiceRoll(first, second);
        else
            board.startBlackDiceRoll(first, second);
    }

    public void startCubeFlipping(int nextValue)
    {
        board.startCubeFlipping(nextValue);
        cubeIsFlipping = true;
    }

    public void delayWhiteLighting(int[] positions)
    {
        lastWhiteLighted = positions;
        isDelaying = true;
    }

    public boolean isRollingDice()
    {
        return diceAreRolling;
    }
    public boolean isFlippingCube()
    {
        return cubeIsFlipping;
    }

    public void initPawnAnimation(ArrayList<HashMap<String, Integer>> moves)
    {
        pawnsAreMoving = true;
        this.moves = moves;
        isSequential = isSequenceNeeded();

        if(isSequential)
        {
            board.resetMovementSettings(true);
            currAnimIndex = 0;
            HashMap<String, Integer> nextMove = getCurrentMove();
            board.setUpNextMove(nextMove.get("from"),nextMove.get("to"), nextMove.get("killMove"), currAnimIndex);
        }
        else
        {
            board.resetMovementSettings(false);
            for(HashMap<String, Integer> move: this.moves)
                if(move.get("killMove").equals(0))
                    board.setUpNextMove(move.get("from"), move.get("to"), move.get("killMove"), currAnimIndex);
        }

        pawnsAreMoving = true;
    }

    public void performInTurnMoves(ArrayList<HashMap<String, Integer>> moves)
    {
        board.prepareTeleport();
        isSequential = true;
        currAnimIndex = 0;
        for(HashMap<String, Integer> move: moves)
        {
            board.setUpNextMove(move.get("from"), move.get("to"), move.get("killMoce"), currAnimIndex++);
            board.updateMovers(1);
            board.finishMove(currAnimIndex - 1, move.get("to"));
        }
    }

    //TODO AE: Láta Activity sjá um að birta takkana eftir að peð hafa verið hreyfð

    public void updatePawns(int deltaMs)
    {
        if(isSequential)
            sequentialUpdate(deltaMs);
        else
            batchUpdate(deltaMs);
    }

    public void updateDice(int deltaMs)
    {
        boolean oldStatus = diceAreRolling;
        diceAreRolling = board.updateDice(deltaMs);
        if(oldStatus != diceAreRolling && isDelaying)
        {
            isDelaying = false;
            whiteLightSquares(lastWhiteLighted);
        }
    }

    public void updateCube(int deltaMs)
    {
        cubeIsFlipping = board.updateCube(deltaMs);
    }

    public void render(Canvas canvas)
    {

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

        //Ef pawnsAreMoving = false og storedMoves != null þá eru þau move keyrð og pawnsAreMoving = true
    }

    //TODO AE: Sannreyna að þessi aðferð virki sem skildi
    private void startKilledMoveIfPossible(int index)
    {
        HashMap<String, Integer> finishedMove = moves.get(index);
        for(int i = 0; i < moves.size(); i++)
        {
            HashMap<String, Integer> move = moves.get(index);
            if(move.get("killMove").equals(1) && move.get("from").equals(finishedMove.get("to")))
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

    public void greenLightSquares(int[] positions)
    {
        for(int pos: positions)
            board.greenLightSquare(pos);
    }
    public void whiteLightSquares(int[] positions)
    {
        for(int pos: positions)
            board.whiteLightSquare(pos);
    }
    public void unHighlightAll(){
        board.unHighlightAll();
    }
}
