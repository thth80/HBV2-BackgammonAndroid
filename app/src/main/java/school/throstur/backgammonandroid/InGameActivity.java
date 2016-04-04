package school.throstur.backgammonandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
    private boolean mIsPlaying, mShouldResetClock, mMatchOver;
    private long mLastGameClockTime;
    private int mTimeLeftMs, mAddedTime;

    private Timer  mRefresher, mClockTimer;
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
                mTextClock.setText("inf");

        }
        else
        {
            // TODO AE: Ég held við getum falið klukkuna svona, spurning um að skilgreina sidebar annars staðar
            FrameLayout sidebar = (FrameLayout) findViewById(R.id.ingame_sidebar_container);
            mTextClock.setVisibility(sidebar.GONE);

            HashMap<String, String> boardDescript = (HashMap<String, String>)getIntent().getSerializableExtra(CURRENT_BOARD);
            mCanvas = CanvasFragment.newInstance(CanvasFragment.EXISTING_BOARD, boardDescript, null);
        }

        setFragment(mCanvas);

        int delayUntilFirstRefresh = (mIsPlaying)? 5000 : 1500;
        mRefresher = new Timer();
        mRefresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mCanvas.hideMatchPresentation();
                (new NetworkingTask("refresh")).execute();
            }
        }, delayUntilFirstRefresh, 2500);
    }

    public void greenWasClicked(int from, int to)
    {
        (new NetworkingTask("green")).execute(from + "", to + "");
    }

    public void endTurnWasClicked()
    {
        mClockTimer.cancel();
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
        mClockTimer.cancel();
        (new NetworkingTask("timeOut")).execute();

        mTextClock.setText(mAddedTime/1000);
        Toast.makeText(InGameActivity.this, "No more time for you!", Toast.LENGTH_LONG).show();
    }

    private void leaveMatchClicked()
    {
        if(!mIsPlaying)
        {
            (new NetworkingTask("observerLeaving")).execute();
            finish();
        }

        new AlertDialog.Builder(InGameActivity.this)
                .setTitle("Leaving The Match")
                .setMessage("Are you sure you want to leave and forfeit the match?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        (new NetworkingTask("leaveMatch")).execute();
                        InGameActivity.super.onBackPressed();
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
        mClockTimer = new Timer();

        mClockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                long delta = timeNow - mLastGameClockTime;
                mLastGameClockTime = timeNow;
                mTimeLeftMs -= delta;

                if (mTimeLeftMs <= 0)
                    onTimeRunningOut();
            }
        }, 25, 250);
    }

    private void presentFinishedMatch(String winner, String loser, String winPoints, String lossPoints)
    {
        mMatchOver = true;
        mCanvas.matchEnded();
        //TODO AE: Setja gögn í viðeigandi glugga og show()-a svo gluggann. Slökkva á refresh?

        mRefresher.cancel();
    }

    private void presentFinishedGame(String winner, String multiplier, String cube, String winType)
    {
        //TODO AE: Breyta þessu í setText á rétt item
        String wonBy = " won a normal victory";
        if(multiplier.equals("2")) wonBy = " Won By Gammon!";
        else if(multiplier.equals("3")) wonBy = " Won By Backgammon!!!";

        int totalPoints = Integer.parseInt(multiplier) * Integer.parseInt(cube);
        Toast.makeText(InGameActivity.this, winner+wonBy+" "+winner+" receives a total of " + totalPoints + " points!", Toast.LENGTH_LONG).show();

        mRefresher.cancel();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                AnimationCoordinator animator = AnimationCoordinator.buildNewBoard(InGameActivity.this);
                mCanvas.setAnimator(animator);
                if (mIsPlaying && !mMatchOver)
                    (new NetworkingTask("startNewGame")).execute();

                if (mMatchOver) return;

                mRefresher = new Timer();
                mRefresher.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        (new NetworkingTask("refresh")).execute();
                    }
                }, 2500, 3500);
            }
        }, 5000);
    }

    private void presentTrophy(int id)
    {
        mTrophyIds.add(id);
        if(mTrophyIds.size() == 1)
            replaceFragment(convertIdToTrophyFragment(id));
    }

    private TrophyFragment convertIdToTrophyFragment(int id)
    {
        //TODO: Tengja gefinn trophy við random bikaramynd til að byrja með
        HashMap<String, String> trophyPres = new HashMap<>();
        String trophyDescript = Utils.trophyDesc[id];
        String trophyName = Utils.trophyNames[id];

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
        ft.replace(R.id.lobby_fragment_container, newFragment);
        ft.commit();
    }

    private void setFragment(Fragment firstFragment)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.lobby_fragment_container, firstFragment);
        ft.commit();
    }

    private void playerDoubled(String doubler, String decider, String stakes)
    {
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
            Toast.makeText(InGameActivity.this, doubler + " doubled the stakes. "+decider+" is making his decision", Toast.LENGTH_LONG).show();
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
                        return Utils.JSONToMapList(InGameNetworking.greenSquare(mUsername, params[0], params[1]));
                    case "dice":
                        return Utils.JSONToMapList(InGameNetworking.diceThrown(mUsername));
                    case "cube":
                        return Utils.JSONToMapList(InGameNetworking.cubeThrown(mUsername));
                    case "refresh":
                        return Utils.JSONToMapList(InGameNetworking.refresh(mUsername));
                    case "endTurn":
                        return Utils.JSONToMapList(InGameNetworking.endTurn(mUsername));
                    case "timeOut":
                        return Utils.JSONToMapList(InGameNetworking.timeOut(mUsername));
                    case "startNewGame":
                        return Utils.JSONToMapList(InGameNetworking.startNewGame(mUsername));
                    case "accept":
                        return Utils.JSONToMapList(InGameNetworking.acceptOffer(mUsername));
                    case "reject":
                        return Utils.JSONToMapList(InGameNetworking.rejectOffer(mUsername));
                    case "observerLeaving":
                        return Utils.JSONToMapList(InGameNetworking.observerLeaving(mUsername));
                    case "playerLeaving":
                        return Utils.JSONToMapList(InGameNetworking.playerLeaving(mUsername));
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
