package school.throstur.backgammonandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.Fragments.TrophyFragment;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.Utility.InGameNetworking;
import school.throstur.backgammonandroid.Utility.Utils;

public class InGameActivity extends AppCompatActivity
{
    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";
    private static final String PRESENT_DATA = "PresentingTHeMAtch";
    private static final String CURRENT_BOARD = "currentStateOfTheBoardForNewcomers";

    private String mUsername;
    private boolean mIsPlaying, mShouldResetClock, mMatchOver, blockProcessing;
    private long mLastGameClockTime;
    private int mTimeLeftMs, mAddedTime;

    private Handler mClockHandler, mRefreshHandler;
    private Runnable mClockTickRunnable, mRefreshRunnable;

    private CanvasFragment mCanvas;
    private ArrayList<Integer> mTrophyIds;

    private Button mLeaveMatchButton;
    private TextView mTextClock;

    public static Intent playingUserIntent(Context packageContext, String username, HashMap<String, String> matchPresent)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, true);
        i.putExtra(PRESENT_DATA, matchPresent);
        return i;
    }
    public static Intent obersvingUserIntent(Context packageContext, String username, HashMap<String, String> wholeBoard)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, false);
        i.putExtra(CURRENT_BOARD, wholeBoard);
        return i;
    }

    @Override
    public void onBackPressed()
    {
        leaveMatchClicked();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);

        blockProcessing = false;
        mClockHandler = new Handler();
        mRefreshHandler = new Handler();

        mMatchOver = false;
        mUsername = getIntent().getStringExtra(USERNAME);
        mIsPlaying = getIntent().getBooleanExtra(IS_PLAYING, false);

        mTextClock = (TextView) findViewById(R.id.time_left);
        mLeaveMatchButton = (Button) findViewById(R.id.btn_leave_match);
        mLeaveMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveMatchClicked();
            }
        });

        if(mIsPlaying)
        {
            HashMap<String, String> matchPres = (HashMap<String, String>)getIntent().getSerializableExtra(PRESENT_DATA);
            mCanvas = CanvasFragment.newInstance(CanvasFragment.NEW_BOARD, null, matchPres);

            mAddedTime = Integer.parseInt(matchPres.get("addedTime"));
            if(mAddedTime == 0)
                mTextClock.setText("\u221E");
            else
            {
                mTimeLeftMs = 30 * 1000;
                mTextClock.setText(mTimeLeftMs/1000 + "");
            }
        }
        else
        {
            // TODO AE: Ég held við getum falið klukkuna svona, spurning um að skilgreina sidebar annars staðar
            FrameLayout sidebar = (FrameLayout) findViewById(R.id.ingame_sidebar_container);
            mTextClock.setVisibility(sidebar.GONE);

            HashMap<String, String> boardDescript = (HashMap<String, String>)getIntent().getSerializableExtra(CURRENT_BOARD);
            mCanvas = CanvasFragment.newInstance(CanvasFragment.EXISTING_BOARD, boardDescript, null);
        }

        addFragment(mCanvas);

        int delayUntilFirstRefresh = (mIsPlaying)? 7000 : 1500;
        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCanvas.hideMatchPresentation();
                    }
                });

                (new NetworkingTask("refresh")).execute();
                mRefreshHandler.postDelayed(mRefreshRunnable, 1500);
            }
        };

        mRefreshHandler.postDelayed(mRefreshRunnable, delayUntilFirstRefresh);
    }

    public void greenWasClicked(int from, int to)
    {
        Log.d("MATCH", "FromSquare: " + from + " ToSquare: " + to);
        (new NetworkingTask("green")).execute(from + "", to + "");
    }

    public void endTurnWasClicked()
    {
        mClockHandler.removeCallbacks(mClockTickRunnable);
        if(mAddedTime > 0)
        {
            mTimeLeftMs += (mAddedTime * 1000);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextClock.setText(mTimeLeftMs / 1000 + "");
                }
            });
        }
        (new NetworkingTask("endTurn")).execute();
    }

    public void cubeWasFlipped()
    {
        (new NetworkingTask("cube")).execute();
    }
    public void diceWasThrown()
    {
        (new NetworkingTask("dice")).execute();
    }

    private void onTimeRunningOut()
    {
        mCanvas.timeRanOut();
        mTimeLeftMs = mAddedTime * 1000;
        mClockHandler.removeCallbacks(mClockTickRunnable);
        (new NetworkingTask("timeOut")).execute();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextClock.setText(mTimeLeftMs / 1000 + "");
                Toast.makeText(InGameActivity.this, "No more time for you!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void leaveMatchClicked()
    {
        if(!mIsPlaying)
        {
            (new NetworkingTask("observerLeaving")).execute();
            mRefreshHandler.removeCallbacks(mRefreshRunnable);
            blockProcessing = true;
            finish();
        }

        new AlertDialog.Builder(InGameActivity.this)
                .setTitle("Leaving The Match")
                .setMessage("Are you sure you want to leave and forfeit the match?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        (new NetworkingTask("playerLeaving")).execute();
                        mRefreshHandler.removeCallbacks(mRefreshRunnable);
                        blockProcessing = true;
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    { /*Ekkert gerist*/ }

                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void startAnimation(HashMap<String, String> animInfo)
    {
        ArrayList<HashMap<String, Integer>> animMoves = Utils.convertToAnimationMoves(animInfo);

        if(!mCanvas.isAnimating() && animMoves.size() > 0)
            mCanvas.startAnimLoop();

        if(animMoves.size() > 0)
        {
            if(!mCanvas.arePawnsMoving())
                mCanvas.getAnimator().initPawnAnimation(animMoves);
            else
                mCanvas.getAnimator().storeDelayedMoves(animMoves);
        }
        else
            Toast.makeText(InGameActivity.this, "Your opponent had no moves available!", Toast.LENGTH_SHORT).show();

    }

    private void startDiceRoll(int first, int second, int team, String thrower)
    {
        mCanvas.startDiceRoll(first, second, team);
        if(thrower.equals(mUsername) && mAddedTime > 0)
            mShouldResetClock = true;
    }

    private void allHighlights(HashMap<String, String> highlightInfo)
    {
        HashMap<Integer, int[]> lightingData = Utils.convertToHighlights(highlightInfo);
        int[] whitePositions = Utils.getWhitesFromLightingMap(lightingData);

        mCanvas.setLightingData(lightingData);
        mCanvas.whiteLightSquares(whitePositions);
    }

    private void showButtonsIfPossible(boolean canDouble)
    {
        if(!mCanvas.arePawnsMoving())
        {
            mCanvas.showThrowDice();
            if(canDouble) mCanvas.showFlipCube();
        }
        else
        {
            mCanvas.setCouldDouble(canDouble);
            mCanvas.giveButtonPermission();
        }
    }

    public boolean shouldResetClock()
    {
        return mShouldResetClock;
    }

    public void resetGameClock()
    {
        mShouldResetClock = false;
        mLastGameClockTime = System.currentTimeMillis();

        mClockTickRunnable = new Runnable() {
            @Override
            public void run()
            {
                long timeNow = System.currentTimeMillis();
                long delta = timeNow - mLastGameClockTime;
                mLastGameClockTime = timeNow;
                mTimeLeftMs -= delta;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextClock.setText(mTimeLeftMs / 1000 + "");
                    }
                });

                if (mTimeLeftMs <= 0)
                    onTimeRunningOut();
                else
                    mClockHandler.postDelayed(mClockTickRunnable, 200);
            }
        };

        mClockHandler.postDelayed(mClockTickRunnable, 50);
    }

    private void presentFinishedMatch(String winner, String loser, String winPoints, String lossPoints)
    {
        mMatchOver = true;
        mCanvas.matchEnded();

        final String lineOne = "The ultimate winner was " + winner + " \n";
        final String lineTwo = "The biggest loser was " + loser + " \n";
        final String lineThree = winner + " earned " + winPoints + " points \n";
        final String lineFour = loser + " earned " + lossPoints + " points";

        runOnUiThread(new Runnable() {
            public void run() {
                mCanvas.presentEndOfGame("The Match Is Over!", lineOne + lineTwo + lineThree + lineFour);
            }
        });

        mRefreshHandler.removeCallbacks(mRefreshRunnable);
    }

    private void presentFinishedGame(String winner, String multiplier, String cube, String winType)
    {
        mClockHandler.removeCallbacks(mClockTickRunnable);

        String wonBy = " Won by a Regular win";
        if(multiplier.equals("2")) wonBy = " Won By Gammon!";
        else if(multiplier.equals("3")) wonBy = " Won By Backgammon!!!";

        String cubeStr = "The final value of the doubling cube: " + cube;

        int totalPoints = Integer.parseInt(multiplier) * Integer.parseInt(cube);
        final String lineOne = winner+wonBy+" \n ";
        final String lineTwo = cubeStr + " \n";
        final String lineThree = winner+" receives a total of " + totalPoints + " points!";

        runOnUiThread(new Runnable() {
            public void run() {
                mCanvas.presentEndOfGame("The Game Has Ended!" , lineOne + lineTwo + lineThree );
            }
        });

        mRefreshHandler.removeCallbacks(mRefreshRunnable);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                AnimationCoordinator animator = AnimationCoordinator.buildNewBoard(InGameActivity.this);
                mCanvas.setAnimator(animator);
                if (mIsPlaying && mAddedTime > 0 && !mMatchOver)
                    mTimeLeftMs = 30 * 1000;

                if (mIsPlaying && !mMatchOver)
                    (new NetworkingTask("startNewGame")).execute();

                if (mMatchOver) return;

                runOnUiThread(new Runnable() {
                    public void run() {
                        mCanvas.hideEndOfGame();
                    }
                });

                mRefreshRunnable = new Runnable() {
                    @Override
                    public void run() {
                        (new NetworkingTask("refresh")).execute();
                        mRefreshHandler.postDelayed(mRefreshRunnable, 2000);
                    }
                };

                mRefreshHandler.postDelayed(mRefreshRunnable, 2000);

            }
        }, 5000);
    }

    private void presentTrophy(int id)
    {
        mTrophyIds = new ArrayList<>();
        mTrophyIds.add(id);
        if(mTrophyIds.size() == 1)
            replaceFragment(convertIdToTrophyFragment(id));
    }

    private TrophyFragment convertIdToTrophyFragment(int id)
    {
        HashMap<String, String> trophyPres = new HashMap<>();
        trophyPres.put("desc", Utils.trophyDesc[id]);
        trophyPres.put("name", Utils.trophyNames[id]);

        return TrophyFragment.newInstance(trophyPres);
    }

    public void onRemoveTrophy()
    {
        if(mTrophyIds.size() == 1)
        {
            mTrophyIds = new ArrayList<>();
            replaceFragment(mCanvas);
        }
        else
        {
            replaceFragment(convertIdToTrophyFragment(mTrophyIds.get(1)));

            ArrayList<Integer> restOfStack = new ArrayList<>();
            for(int i = 1; i < mTrophyIds.size(); i++)
                restOfStack.add(mTrophyIds.get(i));

            mTrophyIds = restOfStack;
        }
    }

    private void replaceFragment(Fragment newFragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.ingame_fragment_container, newFragment);
        ft.commit();
    }

    private void addFragment(Fragment firstFragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.ingame_fragment_container, firstFragment);
        ft.commit();
    }

    private void playerDoubled(String doubler, String decider, String stakes)
    {
        mCanvas.startCubeFlip();

        if(doubler.equals(mUsername))
            Toast.makeText(InGameActivity.this, "You doubled the stakes, "+decider+" is making his decision", Toast.LENGTH_LONG).show();
        else if(decider.equals(mUsername))
        {
            new AlertDialog.Builder(InGameActivity.this)
                    .setTitle("The Stakes Were Doubled")
                    .setMessage("Do you accept your opponent's offer?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new NetworkingTask("accept")).execute();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new NetworkingTask("reject")).execute();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
            makeToast(doubler + " doubled the stakes. "+decider+" is making his decision");
    }

    private void makeToast(final String toastMessage)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(InGameActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>>
    {
        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = InGameActivity.this.mUsername;
            mPath = path;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                switch (mPath)
                {
                    case "green":
                        return InGameNetworking.greenSquare(mUsername, params[0], params[1]);
                    case "dice":
                        return InGameNetworking.diceThrown(mUsername);
                    case "cube":
                        return InGameNetworking.cubeThrown(mUsername);
                    case "refresh":
                        return InGameNetworking.refresh(mUsername);
                    case "endTurn":
                        return InGameNetworking.endTurn(mUsername);
                    case "timeOut":
                        return InGameNetworking.timeOut(mUsername);
                    case "startNewGame":
                        return InGameNetworking.startNewGame(mUsername);
                    case "accept":
                        return InGameNetworking.acceptOffer(mUsername);
                    case "reject":
                        return InGameNetworking.rejectOffer(mUsername);
                    case "observerLeaving":
                        return InGameNetworking.observerLeaving(mUsername);
                    case "playerLeaving":
                        return InGameNetworking.playerLeaving(mUsername);
                }

                Log.d("MATCH", "NO PATH TAKEN");
                return new ArrayList<>();
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            if(blockProcessing) return;
            for(HashMap<String, String> msg: messages)
            {
                if(msg.get("action") == null)
                {
                    Log.d("INCOMING ERROR MATCH", msg.toString());
                    continue;
                }
                switch (msg.get("action"))
                {
                    case "animate":
                        startAnimation(msg);
                        break;
                    case "diceThrow":
                        startDiceRoll(Integer.parseInt(msg.get("firstDice")), Integer.parseInt(msg.get("secondDice")),
                                Integer.parseInt(msg.get("team")), msg.get("thrower"));
                        break;
                    case "allHighlights":
                        allHighlights(msg);
                        break;
                    case "showButtons":
                        showButtonsIfPossible(Boolean.parseBoolean(msg.get("canDouble")));
                        break;
                    case "mayEndTurn":
                        mCanvas.showEndTurn();
                        break;
                    case "matchOver":
                        presentFinishedMatch(msg.get("winner"), msg.get("loser"), msg.get("winPoints"), msg.get("lossPoints"));
                        break;
                    case "gameOver":
                        presentFinishedGame(msg.get("winner"), msg.get("mult"), msg.get("cube"), msg.get("type"));
                        break;
                    case "explain":
                        Toast.makeText(InGameActivity.this, msg.get("explain"), Toast.LENGTH_LONG).show();
                        break;
                    case "presentTrophy":
                        presentTrophy(Integer.parseInt(msg.get("id")));
                        break;
                    case "playerDoubled":
                        playerDoubled(msg.get("doubler"), msg.get("decider"), msg.get("stakes"));
                        break;
                }
            }
        }
    }
}
