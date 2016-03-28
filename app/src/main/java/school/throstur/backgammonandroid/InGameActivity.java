package school.throstur.backgammonandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.Fragments.PresentMatchFragment;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;
import school.throstur.backgammonandroid.Utility.InGameNetworking;
import school.throstur.backgammonandroid.Utility.Utils;

public class InGameActivity extends AppCompatActivity {

    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";
    private static final String PRESENT_DATA = "PresentingTHeMAtch";
    private static final String CURRENT_BOARD = "currentStateOfTheBoardForNewcomers";

    private String mUsername;
    private boolean mCouldDouble, mIsPlaying, mShouldResetClock, mTimedMatch;
    private Timer  mRefresher, mClockTimer;
    private long mLastGameClockTime;

    private CanvasFragment mCanvas;
    private int mTimeLeftMs;

    private Button mLeaveMatchButton;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_in_game);

        setResult(RESULT_OK);
        mUsername = getIntent().getStringExtra(USERNAME);
        mIsPlaying = getIntent().getBooleanExtra(IS_PLAYING, false);
        mLeaveMatchButton = (Button)new View(InGameActivity.this);
        //TODO ÞÞ: Tengja takkann rétt
        mLeaveMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveMatchClicked();
            }
        });

        if(mIsPlaying)
        {
            mCanvas = CanvasFragment.newInstance(CanvasFragment.NEW_BOARD, null);

            HashMap<String, String> pres = (HashMap<String, String>)getIntent().getSerializableExtra(PRESENT_DATA);
            mTimedMatch = !pres.get("addedTime").equals("0") ;

            PresentMatchFragment presentFrag = new PresentMatchFragment();
            presentFrag.setMatchData(pres.get("playerOne"), pres.get("playerTwo"), pres.get("points"), pres.get("addedTime"));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //Skipta um Fragment hér

                    mRefresher = new Timer();
                    mRefresher.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            (new NetworkingTask("refresh")).execute();
                        }
                    }, 500, 1500);
                }
            }, 5000);
        }
        else
        {
            mTimedMatch = false;
            HashMap<String, String> boardDescript = (HashMap<String, String>)getIntent().getSerializableExtra(CURRENT_BOARD);
            mCanvas = CanvasFragment.newInstance(CanvasFragment.EXISTING_BOARD, boardDescript);

            //TODO AE: Hvenær er öruggt að biðja um að teikna canvas?
            mRefresher = new Timer();
            mRefresher.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    (new NetworkingTask("refresh")).execute();
                }
            }, 1500, 1500);
        }
    }

    public void greenWasClicked(String pos)
    {
        (new NetworkingTask("green")).execute(pos);
    }
    public void whiteWasClicked(String pos)
    {
        (new NetworkingTask("white")).execute(pos);
    }
    public void pivotWasClicked()
    {
        (new NetworkingTask("pivot")).execute();
    }
    public void endTurnWasClicked()
    {
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
        mTimeLeftMs = 0;
        mClockTimer.cancel();
        (new NetworkingTask("timeOut")).execute();

        //TODO ÞÞ: Láta klukku UI element fá gildið 0
        Toast.makeText(InGameActivity.this, "No more time for you!", Toast.LENGTH_LONG);
    }

    private void leaveMatchClicked()
    {
        new AlertDialog.Builder(InGameActivity.this)
                .setTitle("Leaving The Match")
                .setMessage("Are you sure you want to leave and forfeit the match?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        (new NetworkingTask("leaveMatch")).execute();
                        setResult(RESULT_OK);
                        InGameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /*
        HTTP RESPONSE aðferðir
    */
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

    private void performInTurnMoves(HashMap<String, String> moveInfo)
    {
        ArrayList<HashMap<String, Integer>> inTurnMoves = Utils.convertToAnimationMoves(moveInfo);
        mCanvas.performInTurnMoves(inTurnMoves);
    }

    private void startDiceRoll(int first, int second, int team, String thrower)
    {
        mCanvas.startDiceRoll(first, second, team);
        if(thrower.equals(mUsername) && mTimedMatch)
            mShouldResetClock = true;
    }

    private void whiteLightSquares(HashMap<String, String> positions)
    {
        int[] squarePositions = Utils.extractIntsFromPositionMessage(positions);
        mCanvas.whiteLightSquares(squarePositions);
    }

    private void greenLightSquares(HashMap<String, String> positions)
    {
        int[] squarePositions = Utils.extractIntsFromPositionMessage(positions);
        mCanvas.greenLightSquares(squarePositions);
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
        mClockTimer = new Timer();
        mClockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                long delta = timeNow - mLastGameClockTime;
                mLastGameClockTime = timeNow;
                mTimeLeftMs -= delta;

                if(mTimeLeftMs <= 0)
                    onTimeRunningOut();
            }
        }, 25 , 50);
    }

    private void addGameTime(int seconds)
    {
        mTimeLeftMs += seconds*1000;
        int secondsOnClock = mTimeLeftMs/1000;
        //TODO ÞÞ: uppfæra UI klukkuna með gildinu í secondsOnClock
    }

    private void presentFinishedMatch(String winner, String loser, String winPoints, String lossPoints)
    {
        //TODO AE: Henda í Toast hérna. Slökkva á refresh?

        mRefresher.cancel();
    }

    private void presentFinishedGame(String winner, String multiplier, String cube, String winType)
    {
        //TODO AE: Notumst við Toast hérna
        String wonBy = " Won by a Regular win";
        if(multiplier.equals("2")) wonBy = " Won By Gammon!";
        else if(multiplier.equals("3")) wonBy = " Won By Backgammon!!!";

        int totalPoints = Integer.parseInt(multiplier) * Integer.parseInt(cube);
        Toast.makeText(InGameActivity.this, winner+wonBy+" "+winner+" receives a total of " + totalPoints + " points!", Toast.LENGTH_LONG);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AnimationCoordinator animator = AnimationCoordinator.buildNewBoard(InGameActivity.this);
                mCanvas.setAnimator(animator);
                (new NetworkingTask("startNewGame")).execute();
            }
        }, 3000);
    }

    private void presentTrophy(int id)
    {
        //TODO: Tengja gefinn trophy við random bikaramynd til að byrja með
        String trophyDescript = Utils.trophyDesc[id];
        String trophyName = Utils.trophyNames[id];

        //TODO AE: Búa til nytt TrophyFragment. Setja Fragment á stafla. Birta fragment.
    }

    private void playerDoubled(String doubler, String decider, String stakes)
    {
        if(doubler.equals(mUsername))
            Toast.makeText(InGameActivity.this, "You doubled the stakes, "+decider+" is making his decision", Toast.LENGTH_LONG);
        else if(decider.equals(mUsername))
        {
            new AlertDialog.Builder(InGameActivity.this)
                    .setTitle("The Stakes Were Doubled")
                    .setMessage("Do you accept your opponent's offer?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new NetworkingTask("doublingDecision")).execute("true");
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            (new NetworkingTask("doublingDecision")).execute("false");
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
            Toast.makeText(InGameActivity.this, doubler + " doubled the stakes. "+decider+" is making his decision", Toast.LENGTH_LONG);
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>>
    {
        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = InGameActivity.this.mUsername;
            mPath = path;
        }

        /*
            HTTP REQUESTS
         */
        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                switch (mPath)
                {
                    case "green":
                        return Utils.JSONToMapList(InGameNetworking.greenSquare(mUsername, params[0]));
                    case "white":
                        return Utils.JSONToMapList(InGameNetworking.whiteSquare(mUsername, params[0]));
                    case "dice":
                        return Utils.JSONToMapList(InGameNetworking.diceThrown(mUsername));
                    case "cube":
                        return Utils.JSONToMapList(InGameNetworking.cubeThrown(mUsername));
                    case "pivot":
                        return Utils.JSONToMapList(InGameNetworking.pivotClicked(mUsername));
                    case "refresh":
                        return Utils.JSONToMapList(InGameNetworking.refresh(mUsername));
                    case "leaveMatch":
                        return Utils.JSONToMapList(InGameNetworking.leaveMatch(mUsername));
                    case "endTurn":
                        return Utils.JSONToMapList(InGameNetworking.endTurn(mUsername));
                    case "timeOut":
                        return Utils.JSONToMapList(InGameNetworking.timeOut(mUsername));
                    case "startNewGame":
                        return Utils.JSONToMapList(InGameNetworking.startNewGame(mUsername));
                    case "doublingDecision":
                        return Utils.JSONToMapList(InGameNetworking.doublingDecision(mUsername, params[0]));
                }
                return null;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        /*
            HTTP RESPONSES
        */
        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            for(HashMap<String, String> msg: messages)
            {
                switch (msg.get("action"))
                {
                    case "animate":
                        startAnimation(msg);
                        break;
                    case "inTurnMove":
                        performInTurnMoves(msg);
                        break;
                    case "diceThrow":
                        startDiceRoll(Integer.parseInt(msg.get("firstDice")), Integer.parseInt(msg.get("secondDice")),
                                Integer.parseInt(msg.get("team")), msg.get("thrower"));
                        break;
                    case "whiteLighted":
                        whiteLightSquares(msg);
                        break;
                    case "greenLighted":
                        greenLightSquares(msg);
                        break;
                    case "showButtons":
                        showButtonsIfPossible(Boolean.parseBoolean(msg.get("canDouble")));
                        break;
                    case "mayEndTurn":
                        mCanvas.showEndTurn();
                        break;
                    case "addedTime":
                        addGameTime(Integer.parseInt(msg.get("seconds")));
                        break;
                    case "matchOver":
                        presentFinishedMatch(msg.get("winner"), msg.get("loser"), msg.get("winPoints"), msg.get("lossPoints"));
                        break;
                    case "gameOver":
                        presentFinishedGame(msg.get("winner"), msg.get("mult"), msg.get("cube"), msg.get("type"));
                        break;
                    case "explain":
                        Toast.makeText(InGameActivity.this, msg.get("explain"), Toast.LENGTH_LONG);
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
