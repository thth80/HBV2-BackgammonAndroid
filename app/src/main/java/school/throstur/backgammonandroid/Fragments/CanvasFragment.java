package school.throstur.backgammonandroid.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.DrawingCanvas;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.InGameActivity;
import school.throstur.backgammonandroid.LobbyActivity;
import school.throstur.backgammonandroid.R;


public class CanvasFragment extends Fragment {
    private static int NO_PIVOT = -1;
    private static int GREEN_SQUARE = 0;
    private static int WHITE_SQUARE = 1;
    private static int PIVOT_SQUARE = 2;

    private DrawingCanvas mDrawingCanvas;
    private Button mEndTurnButton;
    private Button mThrowDiceButton;
    private Button mFlipCubeButton;

    private AnimationCoordinator mAnimator;
    private int mPivot;
    private Timer mAnimLoop;
    private boolean mCouldDouble;

    private float firstX, firstY;
    private InGameActivity mParentGame;

    public CanvasFragment()
    {
        //TODO ÞÞ: Tengja þessi element við rétt UI element

        mEndTurnButton = (Button)new View(getActivity());
        mThrowDiceButton = (Button)new View(getActivity());
        mFlipCubeButton = (Button)new View(getActivity());
        mDrawingCanvas = (DrawingCanvas)new View(getActivity());

        //TODO AE: Breyta þessu
        mParentGame = (InGameActivity)getActivity();

        mEndTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                hideEndTurn();
                mAnimator.unHighlightAll();
                mPivot = NO_PIVOT;

                InGameActivity game = (InGameActivity)getActivity();
                game.endTurnWasClicked();
                mDrawingCanvas.invalidate();
            }
        });

        mThrowDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                InGameActivity game = (InGameActivity)getActivity();
                game.diceWasThrown();
            }
        });

        mFlipCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                InGameActivity game = (InGameActivity)getActivity();
                game.cubeWasFlipped();
            }
        });

        mDrawingCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                double relativeX = event.getX()/mDrawingCanvas.getCanvasWidth();
                double relativeY = event.getY()/mDrawingCanvas.getCanvasHeight();

                String results = mAnimator.wasSquareClicked(relativeX, relativeY, mPivot);
                String[] eventAndPos = results.split("_");

                if(eventAndPos[0].equals("green"))
                {
                    mAnimator.unHighlightAll();
                    mDrawingCanvas.invalidate();
                    mPivot = NO_PIVOT;
                    mParentGame.greenWasClicked(eventAndPos[1]);
                    //Held að lastWhiteLighted update-i automatic og rétt, þegar nýr skammtur af whitelighted kemur frá server
                }
                else if(eventAndPos[0].equals("white"))
                {
                    mAnimator.unHighlightAll();
                    mPivot = Integer.parseInt(eventAndPos[1]);
                    mDrawingCanvas.invalidate();
                    mParentGame.whiteWasClicked(eventAndPos[1]);
                }
                else if(eventAndPos[0].equals("pivot"))
                {
                    mAnimator.unHighlightAll();
                    mAnimator.whiteLightSquares(mAnimator.getLastWhiteLighted());
                    mParentGame.pivotWasClicked();
                    mPivot = NO_PIVOT;
                }
                return false;
            }
        });

    }

    public void setCouldDouble(boolean couldDouble)
    {
        mCouldDouble = couldDouble;
    }

    public boolean arePawnsMoving()
    {
        return mAnimator.arePawnsMoving();
    }
    public boolean isAnimating()
    {
        return mAnimator.isAnimating();
    }
    public AnimationCoordinator getAnimator()
    {
        return mAnimator;
    }

    public void startAnimLoop()
    {
        mAnimLoop = new Timer();
        mAnimLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                boolean animFinished = updateAnimator(16);
                mDrawingCanvas.invalidate();

                InGameActivity game = (InGameActivity)getActivity();
                if(!mAnimator.isAnimating() && game.shouldResetClock())
                    game.resetGameClock();

                if (animFinished)
                    this.cancel();
            }
        }, 20, 20);
    }

    public boolean updateAnimator(int deltaMs)
    {
        boolean wasMovingPawns = mAnimator.arePawnsMoving();
        if(deltaMs > 30) deltaMs = 16;

        mAnimator.updatePawns(deltaMs);
        mAnimator.updateCube(deltaMs);
        mAnimator.updateDice(deltaMs);

        if(wasMovingPawns != mAnimator.arePawnsMoving() && mAnimator.areMovesStored())
        {
            mAnimator.initPawnAnimation(mAnimator.getStoredMoves());
            mAnimator.emptyStorage();
        }

        if(wasMovingPawns != mAnimator.arePawnsMoving())
        {
            //TODO AE: Ekki rétt lógík, ekki víst að þessi megi sjá takkana
            showThrowDice();
            if(mCouldDouble)
                showFlipCube();
        }

        return mAnimator.isAnimating();
    }

    public void performInTurnMoves(ArrayList<HashMap<String, Integer>> moves)
    {
        mAnimator.performInTurnMoves(moves);
        mDrawingCanvas.invalidate();
    }

    public void startDiceRoll(int first, int second, int team)
    {
        mAnimator.startDiceRoll(first, second, team);
    }

    public void timeRanOut()
    {
        hideEndTurn();
        mAnimator.unHighlightAll();
        mDrawingCanvas.invalidate();
    }

    public void drawCanvas()
    {
        mDrawingCanvas.invalidate();
    }

    public void setAnimator(AnimationCoordinator animator)
    {
        mAnimator = animator;
        mDrawingCanvas.setAnimator(animator);
    }

    public void whiteLightSquares(int[] positions)
    {
        mAnimator.whiteLightSquares(positions);
        mDrawingCanvas.invalidate();
    }

    public void greenLightSquares(int[] positions)
    {
        mAnimator.greenLightSquares(positions);
        mDrawingCanvas.invalidate();
    }

    public void hideButtons()
    {
        mFlipCubeButton.setVisibility(View.INVISIBLE);
        mThrowDiceButton.setVisibility(View.INVISIBLE);
    }

    public void showEndTurn()
    {
        mEndTurnButton.setVisibility(View.VISIBLE);
    }
    public void hideEndTurn()
    {
        mEndTurnButton.setVisibility(View.INVISIBLE);
    }

    public void showFlipCube()
    {
        mFlipCubeButton.setVisibility(View.VISIBLE);
    }
    public void showThrowDice()
    {
        mThrowDiceButton.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_canvas, container, false);
    }

}
