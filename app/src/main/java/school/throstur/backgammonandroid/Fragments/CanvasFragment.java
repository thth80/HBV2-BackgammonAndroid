package school.throstur.backgammonandroid.Fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import school.throstur.backgammonandroid.DrawingCanvas;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.InGameActivity;
import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.DrawableStorage;
import school.throstur.backgammonandroid.Utility.Utils;


public class CanvasFragment extends Fragment {
    public static final int NO_SQUARE = -1;
    private static final int NO_POINTER_ID = -1;
    private static final String BOARD_TYPE = "lykillinn fyrir borðið";
    private static final String BOARD_DESC = "lysing a borðinu";
    private static final String MATCH_PRES = "presenting the match if its a player";
    public static final String NEW_BOARD = "new board";
    public static final String EXISTING_BOARD = "existing board";

    private DrawingCanvas mDrawingCanvas;
    private Button mEndTurnButton;
    private Button mThrowDiceButton;
    private Button mFlipCubeButton;
    private RelativeLayout mPresentMatch;

    private TextView postGameHeader, postGameInfo;
    private RelativeLayout postGameWindow;

    private AnimationCoordinator mAnimator;
    private int mPivot, movementId, debugCounter;
    private static Handler mAnimLoop;
    private Runnable mAnimCycle;
    private boolean mCouldDouble, mButtonPermission;
    private HashMap<Integer, int[]> mAllHighlighting;

    private HashMap<String, String> mMatchPresentation;
    private InGameActivity mParentGame;

    public CanvasFragment()
    {

    }

    public static final CanvasFragment newInstance(String boardType, HashMap<String, String> boardDescript
            , HashMap<String, String> matchPres)
    {
        CanvasFragment frag = new CanvasFragment();
        Bundle bundle = new Bundle(3);
        bundle.putString(BOARD_TYPE, boardType);
        bundle.putSerializable(BOARD_DESC, boardDescript);
        bundle.putSerializable(MATCH_PRES, matchPres);

        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mAllHighlighting = null;
        movementId = NO_POINTER_ID;
        mButtonPermission = false;
        mCouldDouble = false;
        mAnimLoop = new Handler();

        mPivot = NO_SQUARE;
        mParentGame = (InGameActivity)getActivity();

        String action = getArguments().getString(BOARD_TYPE);
        HashMap<String, String> board = (HashMap<String, String>)getArguments().getSerializable(BOARD_DESC);
        HashMap<String, String> matchPres = (HashMap<String, String>)getArguments().getSerializable(MATCH_PRES);

        if(action.equals(NEW_BOARD))
        {
            mAnimator = AnimationCoordinator.buildNewBoard(mParentGame);
            mMatchPresentation = matchPres;
        }
        else
        {
            mAnimator = Utils.buildBoardFromDescription(board, mParentGame);
            mMatchPresentation = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);

        mEndTurnButton = (Button) view.findViewById(R.id.end_turn_btn);
        mThrowDiceButton = (Button) view.findViewById(R.id.throw_dice_btn);
        mFlipCubeButton = (Button) view.findViewById(R.id.flip_cube_btn);
        mDrawingCanvas = (DrawingCanvas) view.findViewById(R.id.drawing_canvas);

        postGameHeader = (TextView) view.findViewById(R.id.post_match_heading);
        postGameInfo = (TextView) view.findViewById(R.id.post_match_info);
        postGameWindow = (RelativeLayout) view.findViewById(R.id.post_match_presentation);

        mDrawingCanvas.setAnimator(mAnimator);

        if(mMatchPresentation == null)
            redraw();
        else
        {
            mPresentMatch = (RelativeLayout) view.findViewById(R.id.player_presentation);
            TextView playerOneName = (TextView) mPresentMatch.findViewById(R.id.match_player_one_text);
            playerOneName.setText(mMatchPresentation.get("playerOne"));

            TextView playerTwoName = (TextView) mPresentMatch.findViewById(R.id.match_player_two_text);
            playerTwoName.setText(mMatchPresentation.get("playerTwo"));

            TextView matchPoints = (TextView) mPresentMatch.findViewById(R.id.match_info_score);
            matchPoints.setText("This is a " + mMatchPresentation.get("points") + " point match");

            TextView matchTime = (TextView) mPresentMatch.findViewById(R.id.match_info_time);
            int addedTime = Integer.parseInt(mMatchPresentation.get("addedTime"));
            String timeString = (addedTime == 0)? "Unlimited time to act" : "Time added each round: " + addedTime + " seconds" ;
            matchTime.setText(timeString);

            mPresentMatch.setVisibility(View.VISIBLE);
            redraw();
        }

        mEndTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                hideEndTurn();
                mAnimator.unHighlightAll();
                mPivot = NO_SQUARE;
                redraw();

                mParentGame.endTurnWasClicked();
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

                if (mAnimator.isMovingSinglePawn() && event.getAction() == MotionEvent.ACTION_MOVE) {
                    int index = event.findPointerIndex(movementId);
                    double relativeX = event.getX(index) / mDrawingCanvas.getCanvasWidth();
                    double relativeY = event.getY(index) / mDrawingCanvas.getCanvasHeight();

                    mAnimator.movePawnTo(relativeX, relativeY);
                    redraw();

                }
                //Hér gæti verið einhver funky hegðun, sbr muninn á pointer up og up
                else if (mAnimator.isMovingSinglePawn() &&
                        (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_POINTER_UP)) {
                    int index = event.getActionIndex();
                    if (index == event.findPointerIndex(movementId)) {
                        double relativeX = event.getX(index) / mDrawingCanvas.getCanvasWidth();
                        double relativeY = event.getY(index) / mDrawingCanvas.getCanvasHeight();
                        int greenPos = mAnimator.wasGreenSquareBelow(relativeX, relativeY);

                        mAnimator.unHighlightAll();

                        if (greenPos == NO_SQUARE) {
                            mAnimator.addPawnTo(mPivot);
                            mAnimator.whiteLightSquares(mAnimator.getLastWhiteLighted());
                        } else {
                            hideEndTurn();
                            mAnimator.removeLastWhitelighted();
                            mAnimator.addPawnTo(greenPos);

                            mParentGame.greenWasClicked(mPivot, greenPos);
                        }

                        movementId = NO_POINTER_ID;
                        mPivot = NO_SQUARE;
                        redraw();
                    }
                } else if (!mAnimator.isMovingSinglePawn() && event.getAction() == MotionEvent.ACTION_DOWN) {
                    double relativeX = event.getX(0) / mDrawingCanvas.getCanvasWidth();
                    double relativeY = event.getY(0) / mDrawingCanvas.getCanvasHeight();

                    int whitePos = mAnimator.wasWhiteSquareClicked(relativeX, relativeY);
                    if (whitePos != NO_SQUARE) {
                        mAnimator.unHighlightAll();
                        mAnimator.highlightGreens(mAllHighlighting.get(whitePos));

                        mPivot = whitePos;
                        mAnimator.pickUpPawnAt(whitePos);
                        mAnimator.movePawnTo(relativeX, relativeY);
                        movementId = event.getPointerId(0);

                        redraw();
                    }
                }

                return true;
            }
        });

        return view;
    }

    public void onViewCreated(View view, Bundle saved) {
        super.onViewCreated(view, saved);

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
                                       int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    Log.d("MATCH ERROR", "onLayoutChange did not deliver width and height");
                    return;
                }

                DrawableStorage.setDimensions(mDrawingCanvas.getCanvasWidth(), mDrawingCanvas.getCanvasHeight());
            }
        });
    }

    public void hideMatchPresentation()
    {
        mPresentMatch.setVisibility(View.GONE);
    }

    public void matchEnded()
    {
        hideButtons();
        hideEndTurn();
        mAnimator.unHighlightAll();
    }

    public void giveButtonPermission()
    {
        mButtonPermission = true;
    }

    public void setLightingData(HashMap<Integer, int[]> lightingData)
    {
        mAllHighlighting = lightingData;
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

    public void presentEndOfGame(String header, String content)
    {
        postGameHeader.setText(header);
        postGameInfo.setText(content);
        postGameWindow.setVisibility(View.VISIBLE);
    }

    public void hideEndOfGame()
    {
        postGameWindow.setVisibility(View.GONE);
    }

    public void startAnimLoop()
    {
        Log.d("MATCH", "Starting the animation loop");
        debugCounter  = 0;

        mAnimCycle = new Runnable() {
            @Override
            public void run() {
                boolean stillAnimating = runOneAnimCycle();
                if(!stillAnimating)
                {
                    Log.d("MATCH", "Finishing Animation Loop for now. Loops taken: " + debugCounter);
                    mAnimLoop.removeCallbacks(mAnimCycle);
                }
                else
                    mAnimLoop.postDelayed(mAnimCycle, 16);
            }
        };

        mAnimLoop.postDelayed(mAnimCycle, 16);
    }

    private boolean runOneAnimCycle()
    {
        boolean stillAnimating = updateAnimator(16);
        redraw();
        debugCounter++;

        if (!stillAnimating && mParentGame.shouldResetClock())
            mParentGame.resetGameClock();

        return stillAnimating;
    }

    private void redraw()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDrawingCanvas.invalidate();
            }
        });
    }

    public boolean updateAnimator(int deltaMs)
    {
        //TODO AE: Inf lykkja fannst hér, fyrir vikið voru peðin á undarlegum stöðum.
        boolean pawnsWereMoving = mAnimator.arePawnsMoving();

        if(pawnsWereMoving)
            mAnimator.updatePawns(deltaMs);
        if(mAnimator.isFlippingCube())
            mAnimator.updateCube(deltaMs);
        if(mAnimator.isRollingDice() && !mAnimator.updateDice(deltaMs))
            redraw();

        if(pawnsWereMoving != mAnimator.arePawnsMoving() && mAnimator.areMovesStored())
        {
            mAnimator.initPawnAnimation(mAnimator.getStoredMoves());
            mAnimator.emptyStorage();
        }

        if(pawnsWereMoving != mAnimator.arePawnsMoving() && mButtonPermission)
        {
            Log.d("MATCH", "Delayed showing of the buttons");
            mButtonPermission = false;
            showThrowDice();
            if(mCouldDouble)
                showFlipCube();
        }

        return mAnimator.isAnimating();
    }

    public void startDiceRoll(int first, int second, int team)
    {
        startAnimLoop();
        mAnimator.startDiceRoll(first, second, team);
    }

    public void startCubeFlip()
    {
        startAnimLoop();
        mAnimator.startCubeFlipping();
    }

    public void timeRanOut()
    {
        hideEndTurn();
        mAnimator.unHighlightAll();
        redraw();
    }

    public void setAnimator(AnimationCoordinator animator)
    {
        mAnimator = animator;
        mDrawingCanvas.setAnimator(animator);
    }

    public void whiteLightSquares(int[] positions)
    {
        if(mAnimator.isRollingDice())
            mAnimator.delayWhiteLighting(positions);
        else
            mAnimator.whiteLightSquares(positions);

        redraw();
    }

    public void hideButtons()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFlipCubeButton.setVisibility(View.INVISIBLE);
                mThrowDiceButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showEndTurn()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEndTurnButton.setVisibility(View.VISIBLE);
            }
        });
    }
    public void hideEndTurn()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEndTurnButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showFlipCube()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFlipCubeButton.setVisibility(View.VISIBLE);
            }
        });
    }
    public void showThrowDice()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mThrowDiceButton.setVisibility(View.VISIBLE);
            }
        });
    }

}
