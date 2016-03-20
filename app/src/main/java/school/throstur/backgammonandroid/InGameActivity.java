package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InGameActivity extends AppCompatActivity {

    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";

    private String mUsername;
    private boolean mCouldDouble, mIsPlaying;
    private Timer mAnimLoop, mRefresher, mClockTimer;
    private AnimationCoordinator mAnimator;

    private int mTimeLeftMs;

    private Button leaveMatchButton;
    private Button submitChatButton;


    public static Intent playingUserIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, true);
        return i;
    }

    public static Intent obersvingUserIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, false);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_in_game);

        mIsPlaying = getIntent().getBooleanExtra(IS_PLAYING, false);
        mUsername = getIntent().getStringExtra(USERNAME);

        int refreshPause = (mIsPlaying)? 1500: 2000 ;

        mRefresher = new Timer();
        mRefresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask("refresh")).execute();
            }
        }, 1500 ,refreshPause);

    }


    private void onCanvasClicked(double cx, double cy)
    {
        int pos = 15;
        String event = "derp";
        if(event == "white")
        {
            (new NetworkingTask("white")).execute(pos + "");
            //aflita hvíta
        }
        else if(event == "green")
        {
            (new NetworkingTask("green")).execute(pos+"");
            //aflita alla reiti
        }
        else if(event == "pivot")
        {
            (new NetworkingTask("pivot")).execute(pos+"");
            //lýsa aftur upp hvíta reiti
        }

    }


    private void onEndTurnClicked()
    {
        (new NetworkingTask("endTurn")).execute();
        //aflita alla græna eða hvíta, stilla mPivot rétt
    }

    private void onDoublingClicked()
    {
        (new NetworkingTask("cube")).execute();
        //fela alla takka
    }

    private void onThrowDiceClicked()
    {
        (new NetworkingTask("dice")).execute();
        //fela alla takka
    }

    private void onTimeRunningOut()
    {
        (new NetworkingTask("timeOut")).execute();
        //aflita alla, skipta um pivot, senda toast
    }

    private void onLeaveMatch()
    {

    }

    /*
        HTTP RESPONSE aðferðir
     */

    private void startAnimation(HashMap<String, String> animInfo)
    {
        animInfo.remove("action");
        ArrayList<HashMap<String, Integer>> animMoves = new ArrayList<>();
        for(int i = 0; i < animInfo.size()/3; i++)
        {
            HashMap<String, Integer> singleAnim = new HashMap<>();
            singleAnim.put("from", Integer.parseInt(animInfo.get("from" + i)));
            singleAnim.put("to", Integer.parseInt(animInfo.get("to" + i)));

            int killMove = animInfo.get("kill" + i).equals("true")? 1 : 0;
            singleAnim.put("killMove",killMove ) ;
            singleAnim.put("finished", 0);
            animMoves.add(singleAnim);
        }

        //TODO AE: Hvernig virkar leikjalykkjan? Hvað með þegar moves.length = 0?
        mAnimator.initPawnAnimation(animMoves);
    }

    private void performInTurnMoves(HashMap<String, String> moveInfo)
    {
        moveInfo.remove("action");
        ArrayList<HashMap<String, Integer>> inTurnMoves = new ArrayList<>();
        for(int i = 0; i < moveInfo.size()/3; i++)
        {
            HashMap<String, Integer> singleAnim = new HashMap<>();
            singleAnim.put("from", Integer.parseInt(moveInfo.get("from" + i)));
            singleAnim.put("to", Integer.parseInt(moveInfo.get("to" + i)));

            int killMove = moveInfo.get("kill" + i).equals("true")? 1 : 0;
            singleAnim.put("killMove",killMove ) ;
            inTurnMoves.add(singleAnim);
        }

        mAnimator.performInTurnMoves(inTurnMoves);
        mAnimator.render(new Canvas());
    }

    private void startDiceRoll(int first, int second, int team)
    {
        mAnimator.startDiceRoll(first, second, team);
        //TODO AE: isAnimating = true, meðal annars til að blokka birtingu hvítra reita.
        //Koma leikjalykkju af stað
        //TODO AE: Láta klukkuna fara af stað ef leikurinn er timed, eftir að teningar hafa rúllað HJÁ spilara
    }

    private void whiteLightSquares(HashMap<String, String> positions)
    {
        positions.remove("action");
        int[] squarePositions = new int[positions.size()];
        for(int i = 0; i < squarePositions.length; i++)
            squarePositions[i] = Integer.parseInt(positions.get(""+i));

        if(mAnimator.isRollingDice())
            mAnimator.delayWhiteLighting(squarePositions);
        else
        {
            mAnimator.whiteLightSquares(squarePositions);
            mAnimator.render(new Canvas());
        }
    }

    private void greenLightSquares(HashMap<String, String> positions)
    {
        positions.remove("action");
        int[] squarePos = new int[positions.size()];
        for(int i = 0; i < squarePos.length; i++)
            squarePos[i] = Integer.parseInt(positions.get(""+i));

        mAnimator.greenLightSquares(squarePos);
        mAnimator.render(new Canvas());
    }

    //Líklega best að láta acitivity kalla á finish delayed og síðan á onDraw()
    private void showButtonsIfPossible(boolean canDouble)
    {
        if(!mAnimator.pawnsAreMoving)
            showButtons(canDouble);
        else
            mCouldDouble = canDouble;
    }

    public void updateAnimator(int deltaMs)
    {

    }

    private void showButtons(boolean canDouble)
    {
        //TODO ÞÞ: Takkarnir birtast, báðir(double stakes og throw dice) ef canDouble == true, annars bara throwDice
    }

    private void showEndTurn()
    {
        //TODO ÞÞ: End Turn takkinn birtist, verður virkur/enabled
    }

    private void addGameTime(int seconds)
    {
        mTimeLeftMs += seconds*1000;
        int secondsOnClock = mTimeLeftMs/1000;
        //TODO ÞÞ: uppfæra klukkuna með gildinu í secondsOnClock
    }

    //Á þessum tímapunkti þarf að koma
    private void presentStartingMatch(String playerOne, String playerTwo)
    {
        //TODO ÞÞ: Seinni tíma vandamál að láta match presentation birtast hér

        //TODO AE: Ætti initMatch að senda einungis þessi skilaboð og síðan er rest sótt eftir
        //nokkrar sekúndur? Hér má græða á því að ástandið er yfirleitt svipað, engin peð hafa
        //verið hreyfð. Birta þarf teningarúll eftir að present skjárinn hverfur, síðan white Lighting
    }

    private void presentFinishedMatch(String winner, String loser, String winPoints, String lossPoints)
    {
        //TODO ÞÞ: Búið er til element með þessum upplýsingum. Það er sett inn í stað canvas elements,
        //þar sem leikurinn(match) er hvort eð er búinn. Ekkert að því heldur að setja yfir canvasinn án þess að eyða canvas.

        mRefresher.cancel();
    }

    private void presentFinishedGame(String winner, String multiplier, String cube, String winType)
    {
        String wonBy = "Regular Win";
        if(multiplier.equals("2")) wonBy = "Won By Gammon!";
        else if(multiplier.equals("3")) wonBy = "Won By Backgammon!!!";

        int totalPoints = Integer.parseInt(multiplier) * Integer.parseInt(cube);
        //TODO ÞÞ: Notast við winner, wonBy, cube, totalPoints og winType til að búa til kynningu á lok leiks
        //Líklega best að láta kynningu vera ofan á canvas, þar sem match er EKKI lokið hér

        //TODO AE: Hvernig má útfæra það að notandinn sæki gögn fyrir nýjan leik eftir að kynningu lýkur?
        //Þessi skilaboð geta borist fyrir nokkur HTTP Requests. Delayed postbox á server? Láta teninga rúlla í bakgrunni?
    }

    private void setUpWholeBoard(HashMap<String, String> boardDescription)
    {
        int[] counts = new int[28];
        int[] teams = new int[28];
        int[] diceVals = new int[4];
        int cubeValue = Integer.parseInt(boardDescription.get("cube"));

        for(int i = 0; i < 28; i++)
        {
            counts[i] = Integer.parseInt(boardDescription.get("c" + i));
            teams[i] = Integer.parseInt(boardDescription.get("t" + i));
        }
        for(int i = 0; i < 4; i++)
            diceVals[i] = Integer.parseInt(boardDescription.get("d" + i));

        mAnimator.buildExistingBoard(teams, counts, diceVals, cubeValue);
        mAnimator.render(new Canvas());
    }

    private void presentTrophy(int id)
    {
        //TODO: tengja löglegt imageId hérna í stað 1
        displayTrophy(Utils.trophyNames[id], Utils.trophyDesc[id], 1);
    }

    private void displayTrophy(String name, String descript, int imageId)
    {
        //TODO ÞÞ: (Low priority eins og er) Birta element sem inniheldur mynd, lýsingu og nafn á bikar auk þess að hafa takka sem
        //er tengdur við onClick. Element liggur ofan á canvas
    }

    private void playerDoubled(String doubler, String decider, String stakes)
    {
        if(doubler.equals(mUsername))
            Toast.makeText(InGameActivity.this, "You doubled the stakes. "+decider+" is making his decision", Toast.LENGTH_LONG);
        else if(decider.equals(mUsername));
            //TODO ÞÞ: Birta ALERT með OK/Cancel eða hvað sem það heitir í Android með texta sem segir að andstæðingurinn dobblaði.
        else
            Toast.makeText(InGameActivity.this, doubler + " doubled the stakes. "+decider+" is making his decision", Toast.LENGTH_LONG);
    }

    //TODO AE: Gerast líklega skrýtnir hlutir þegar observer horfir á bot vs human og 2 sett af hreyfingum koma í röð
    //TODO AE: Það tilfelli þegar annar spilara getur ekki gert neitt gæti þurft að rannsaka
    //<String, MSG, MSG>
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
                    case "leave":
                        return Utils.JSONToMapList(InGameNetworking.leaveMatch(mUsername));
                    case "endTurn":
                        return Utils.JSONToMapList(InGameNetworking.endTurn(mUsername));
                    case "timeOut":
                        return Utils.JSONToMapList(InGameNetworking.timeOut(mUsername));
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
                                Integer.parseInt(msg.get("team")));
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
                        showEndTurn();
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
                    case "presentMatch":
                        presentStartingMatch(msg.get("playerOne"), msg.get("playerTwo"));
                        break;
                    case "wholeBoard":
                        setUpWholeBoard(msg);
                        break;
                }
            }

        }

    }

}
