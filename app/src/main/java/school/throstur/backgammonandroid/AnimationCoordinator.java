package school.throstur.backgammonandroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class AnimationCoordinator {
    public static final int NOT_AN_ID = -1;

    private BoardManager board;
    private ArrayList<HashMap<String, Integer>> moves;
    private int currAnimIndex;
    boolean isSequential, pawnsAreMoving, isAnimating;

    public AnimationCoordinator()
    {
        moves = null;
        currAnimIndex = 0;
        isSequential = false;
        pawnsAreMoving = false;
        isAnimating = false;
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

    public void receiveMoves(ArrayList<HashMap<String, Integer>> moves)
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
                if(move.get("killMove") == 0)
                    board.setUpNextMove(move.get("from"), move.get("to"), move.get("killMove"), currAnimIndex);
        }

        isAnimating = true;
    }

    public void update(int deltaMs)
    {
        if(isSequential)
            sequentialUpdate(deltaMs);
        else
            batchUpdate(deltaMs);
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

        Collections.sort(finishedIndexes);
        for(Integer index: finishedIndexes)
            board.finishMove(index, moves.get(index).get("to"));

        for(int i = finishedIndexes.size() - 1; i >= 0; i--)
        {
            startKilledMoveIfPossible(finishedIndexes.get(i));
            moves.remove(finishedIndexes.get(i));
        }

        if(moves.size() == 0)
            pawnsAreMoving = false;
    }


    //TODO AE: Integer.equals er MUST allstaðar
    private void startKilledMoveIfPossible(int index)
    {
        HashMap<String, Integer> finishedMove = moves.get(index);
        for(int i = 0; i < moves.size(); i++)
        {
            HashMap<String, Integer> move = moves.get(index);
            if(move.get("killMove").equals(1) && move.get("from").equals(finishedMove.get("to")))
            {
                board.setUpNextMove(move.get("from"), move.get("to"), 1, i);
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
