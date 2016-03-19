package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InGameActivity extends AppCompatActivity {

    //ATH: MSG um að leikmaður joinaði leik notanda má EKKI vera einungis Lobby MSG
    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";

    private String mUsername;
    private AnimationCoordinator mAnimator;

    private boolean isPlaying, mCouldDouble;
    private int pivotSquare;
    private int mTimeLeftMs;
    private AnimationCoordinator animator;

    private Button leaveMatchButton;
    private Button submitChatButton;


    //Gæti verið kallað á þessa úr Stats, Trophy eða Lobby
    public static Intent playingUserIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, true);
        return i;
    }
    //Einungis kallanleg úr Lobby
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isPlaying = getIntent().getBooleanExtra(IS_PLAYING, false);
        mUsername = getIntent().getStringExtra(USERNAME);
    }

    //TODO ÞÞ: Ekkert sem þarf að gera hér, bara að benda á að onXXX aðferðirnar eru callbacks sem á að tengja við viðeigandi takka

    private void onCanvasClicked(double cx, double cy)
    {

    }

    private void onWhiteSquareClicked(int pos)
    {

    }

    private void onGreenSquareClicked(int pos)
    {

    }

    private void onPivotClicked()
    {
        //TODO AE: Lýsa aftur upp reitina með síðustu white lighting gildum
    }

    private void onEndTurnClicked()
    {

    }

    private void onDoublingClicked()
    {

    }

    private void onThrowDiceClicked()
    {

    }

    private void onTimeRunningOut()
    {

    }

    private void onLeaveMatch()
    {

    }

    //Hér að neðan eru aðferðirnar sem bregðast við mismunandi HTTP response skilaboðum

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
            animMoves.add(singleAnim);
        }

        //TODO AE: Hvernig virkar leikjalykkjan?
        mAnimator.receiveAnimMoves(animMoves);
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
        mAnimator.render("ctx");
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
        int[] squarePos = new int[positions.size()];
        for(int i = 0; i < squarePos.length; i++)
            squarePos[i] = Integer.parseInt(positions.get(""+i));

        if(mAnimator.isRollingDice())
            //Þegar teningarnir rúlla þarf að bíða með whitelight, annars ekki
            //TODO AE: Sjá til þess að animator whitelighti þegar teningar hætta að rúlla
            mAnimator.delayWhiteLighting(squarePos);
        else
        {
            mAnimator.whiteLightSquares(squarePos);
            mAnimator.render("ctx");
        }
    }

    private void greenLightSquares(HashMap<String, String> positions)
    {
        positions.remove("action");
        int[] squarePos = new int[positions.size()];
        for(int i = 0; i < squarePos.length; i++)
            squarePos[i] = Integer.parseInt(positions.get(""+i));

        mAnimator.greenLightSquares(squarePos);
        mAnimator.render("ctx");
    }

    //Hér mun alltaf þurfa að bíða eftir að animation peða(og allt animation) klárist
    //Líklega best að láta acitivity kalla á finish delayed og síðan á mAnimator.render()
    //Reyndar alveg spurning hvort animator sé sá sem á að sjá um þetta ??? Líkelga frekar activity sem reddar þessu.
    private void showButtonsIfPossible(boolean canDouble)
    {
        if(!mAnimator.isAnimating)
            showButtons(canDouble);
        else
            mCouldDouble = canDouble;
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

    //TODO AE: CRUCIAL að borðið sé sett upp áður en nokkrar breytingar eru gerðar á því, gerist líklega á server.
    //Map með lyklunum c0-c27(counts), t0-t27(teams), d0-d3(diceVals), cube
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
        mAnimator.render("ctx");
    }

    private void presentTrophy(int id)
    {
        //TODO AE: Er ekki bara best að harðkóða þessar tiltölulega litlu upplýsingar í Utils? SLeppa því local sql veseni
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

        //HTTP REQUESTS
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
                    case "leave":
                        return Utils.JSONToMapList(InGameNetworking.leaveMatch(mUsername));
                    case "endTurn":
                        return Utils.JSONToMapList(InGameNetworking.endTurn(mUsername));
                }
                return null;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        //HTTP RESPONSES, Chat svör koma kannski seinna inn
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
                    case "announcement":
                        presentStartingMatch(msg.get("playerOne"), msg.get("playerTwo"));
                        break;
                    case "matchOver":
                        presentFinishedMatch(msg.get("winner"), msg.get("loser"), msg.get("winPoints"), msg.get("lossPoints"));
                        break;
                    case "gameOver":
                        presentFinishedGame(msg.get("winner"), msg.get("mult"), msg.get("cube"), msg.get("type"));
                        break;
                    case "wholeBoard":
                        setUpWholeBoard(msg);
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
