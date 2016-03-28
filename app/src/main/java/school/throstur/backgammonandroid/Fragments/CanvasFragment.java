package school.throstur.backgammonandroid.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Adapters.LobbyListAdapter;
import school.throstur.backgammonandroid.DrawingCanvas;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.InGameActivity;
import school.throstur.backgammonandroid.LobbyActivity;
import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.DrawableStorage;
import school.throstur.backgammonandroid.Utility.Utils;


public class CanvasFragment extends Fragment {
    private static int NO_PIVOT = -1;
    private static final String BOARD_TYPE = "lykillinn fyrir borðið";
    private static final String BOARD_DESC = "lysing a borðinu";
    public static final String NEW_BOARD = "new board";
    public static final String EXISTING_BOARD = "existing board";

    private DrawingCanvas mDrawingCanvas;
    private Button mEndTurnButton;
    private Button mThrowDiceButton;
    private Button mFlipCubeButton;

    private AnimationCoordinator mAnimator;
    private int mPivot;
    private Timer mAnimLoop;
    private boolean mCouldDouble, mButtonPermission;

    private float firstX, firstY;
    private InGameActivity mParentGame;

    public CanvasFragment()
    {

    }

    public static final CanvasFragment newInstance(String boardType, HashMap<String, String> boardDescript)
    {
        CanvasFragment frag = new CanvasFragment();
        Bundle bdl = new Bundle(2);
        bdl.putString(BOARD_TYPE, boardType);
        bdl.putSerializable(BOARD_DESC, boardDescript);
        frag.setArguments(bdl);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mButtonPermission = false;
        mCouldDouble = false;
        mPivot = NO_PIVOT;
        mParentGame = (InGameActivity)getActivity();

        String action = getArguments().getString(BOARD_TYPE);
        HashMap<String, String> board = (HashMap<String, String>)getArguments().getSerializable(BOARD_DESC);

        if(action.equals(NEW_BOARD))
            mAnimator = AnimationCoordinator.buildNewBoard(mParentGame);
        else
            mAnimator = Utils.buildBoardFromDescription(board, mParentGame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);

        //TODO ÞÞ: Búa til þessi element með þessum ids

        //mEndTurnButton = (Button) view.findViewById(R.id.end_turn_btn);
       // mThrowDiceButton = (Button) view.findViewById(R.id.throw_dice_btn);
        //mFlipCubeButton = (Button) view.findViewById(R.id.flip_cube_btn);
        //mDrawingCanvas = (DrawingCanvas)view.findViewById(R.id.drawing_canvas);


        mEndTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                hideEndTurn();
                mAnimator.unHighlightAll();
                mPivot = NO_PIVOT;

                mParentGame.endTurnWasClicked();
                mDrawingCanvas.invalidate();
            }
        });

        mThrowDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                mParentGame.diceWasThrown();
            }
        });

        mFlipCubeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                mParentGame.cubeWasFlipped();
            }
        });

        mDrawingCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                double relativeX = event.getX() / mDrawingCanvas.getCanvasWidth();
                double relativeY = event.getY() / mDrawingCanvas.getCanvasHeight();

                String results = mAnimator.wasSquareClicked(relativeX, relativeY, mPivot);
                String[] eventAndPos = results.split("_");

                if (eventAndPos[0].equals("green"))
                {
                    mAnimator.unHighlightAll();
                    mAnimator.removeLastWhitelighted();
                    mDrawingCanvas.invalidate();
                    mPivot = NO_PIVOT;
                    mParentGame.greenWasClicked(eventAndPos[1]);
                }
                else if (eventAndPos[0].equals("white"))
                {
                    mAnimator.unHighlightAll();
                    mDrawingCanvas.invalidate();
                    mPivot = Integer.parseInt(eventAndPos[1]);
                    mParentGame.whiteWasClicked(eventAndPos[1]);
                }
                else if (eventAndPos[0].equals("pivot"))
                {
                    mAnimator.unHighlightAll();
                    mAnimator.whiteLightSquares(mAnimator.getLastWhiteLighted());
                    mPivot = NO_PIVOT;
                    mParentGame.pivotWasClicked();
                }
                return false;
            }
        });

        return view;
    }

    public void onViewCreated(View view, Bundle saved)
    {
        super.onViewCreated(view, saved);
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom)
            {
                if (left == 0 && top == 0 && right == 0 && bottom == 0)
                    return;

                DrawableStorage.setDimensions(mDrawingCanvas.getCanvasWidth(), mDrawingCanvas.getCanvasHeight());
            }
        });
    }

    public void giveButtonPermission()
    {
        mButtonPermission = true;
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

                if(!mAnimator.isAnimating() && mParentGame.shouldResetClock())
                    mParentGame.resetGameClock();

                if (animFinished)
                    this.cancel();
            }
        }, 16, 16);
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

        if(wasMovingPawns != mAnimator.arePawnsMoving() && mButtonPermission)
        {
            mButtonPermission = false;
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


}
