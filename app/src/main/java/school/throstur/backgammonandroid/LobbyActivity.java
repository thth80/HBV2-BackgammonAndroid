package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

//MUNA að það þarf að vera til 6. PostboxmManagerinn, forAll sem getur geymt skilaboð sem
//fá má í lobby, stats og trophy room

public class LobbyActivity extends AppCompatActivity {
    private static final String SENT_FROM_LOGIN = "usernameExtra";

    /*
        Öll view sem þurfa að vera tengt við events. Join og Cancel takkinn á hverju einasta entry
        eru meira vandamál, líklega ekki viðeigandi að láta activity tengja þá takka.
    */

    private String mUsername;
    private Button mSetupMatchButton;
    private Button mSubmitChatButton;
    private Button mToTrophyButton;
    private Button mToStatsButton;

    public static Intent usernameIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(SENT_FROM_LOGIN, username);
        return i;
    }

    //TODO ÞÞ: Tengja allar member breytur við raunveruleg widget í gegnum findViewById. Æskileg ID nöfn eru í lok hverrar línu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_lobby);

        mSetupMatchButton = (Button)new View(LobbyActivity.this); //setup_match
        mSubmitChatButton = (Button)new View(LobbyActivity.this); //submit_chat
        mToTrophyButton = (Button)new View(LobbyActivity.this);  //to_trophy
        mToStatsButton = (Button)new View(LobbyActivity.this);   //to_stats

        mUsername = getIntent().getStringExtra(SENT_FROM_LOGIN);
        NetworkingTask initData = new NetworkingTask("initLobby");
        initData.execute();

        mSetupMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupNewMatch();
            }
        });
        mSubmitChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitChatEntry();
            }
        });
        mToTrophyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterTrophyRoom();
            }
        });
        mToStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterMyStats();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /*
    TODO ÞÞ: Líklega viðeigandi í aðferðunum 2 hér að neðan að sýna notandanum að verið sé að vinna
    í þessari beiðni(progress spinner, toast eða eitthvað einfalt). Líklega verður kallað á server áður en kallað er
    á startActivity.
    */
    private void enterMyStats()
    {
        //Kannski eitthvað sem þarf að gera áður en við yfirgefum Lobby
        //AÐ ÖLLUM LÍKINDUM verður talað við server hér og skilaboð fyrir stats generated þar
        startActivity(StatsActivity.usernameIntent(LobbyActivity.this, mUsername));
    }

    private void enterTrophyRoom()
    {
        //Kannski eitthvað sem þarf að gera áður en við yfirgefum Lobby
        startActivity(TrophyActivity.usernameIntent(LobbyActivity.this, mUsername));
    }

    private void submitChatEntry()
    {
        //TODO ÞÞ: Extract-a chat textann úr tilsvarandi View-i og setja í chatEntry. Einnig skal henda í Toast þegar length == 0
        String chatEntry = "Trump Trump Trump! Make America great again";

        if(chatEntry.length() == 0);
            //TOAST: Bannað að senda tóma strengi sem chat
        else
            (new NetworkingTask("newLobbyChat")).execute(chatEntry);
    }

    //TODO ÞÞ: Þetta þarf að senda sem HTTP, því Strings en ekki ints. Hér þarf að extract-a gögn úr RadioGroup og setja í breyturnar 4.
    private void setupNewMatch()
    {
        String points = "999";
        String clock = "true";
        String addedTime = "15";
        boolean isHumanMatch = true;

        if(isHumanMatch)
            (new NetworkingTask("waitingEntry")).execute(points, clock, addedTime);
        else
            (new NetworkingTask("startBotMatch")).execute(points, clock, addedTime);
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = LobbyActivity.this.mUsername;
            mPath = path;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                List<HashMap<String, String>> messages = null;
                switch (mPath)
                {
                    case "addHuman":
                        messages = LobbyNetworking.addHumanMatch(mUsername, params[0], params[1]);
                        break;
                    case "joinHumanMatch":
                        messages = LobbyNetworking.joinHumanMatch(mUsername, params[0]);
                        break;
                    case "removeHumanMatch":
                        messages = LobbyNetworking.removeWaitEntry("waitID=5?");
                        break;
                    case "startBotMatch":
                        messages = LobbyNetworking.startBotMatch(mUsername, params[0], params[1], params[2]);
                        break;
                    case "observeMatch":
                        break;
                    case "initLobby":
                        break;
                    case "newLobbyChat":
                        messages = LobbyNetworking.addLobbyChat(mUsername, params[0]);
                        break;
                    case "waitingEntry":
                        break;
                    case "refresh":
                        break;
                }
                return messages;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        //Kannski ætti klikk á Trophy/stats að valda því að server generate-ar rétt gögn ÁÐUR en kallað er á startActivity.
        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            //Þau tilfelli þegar vitað er að yfirgefa þarf Lobby. Ath að það er tiny séns á að samþykktum leik verði "hent" hérna
            //Ef einhver önnur skilaboð fylgdu með þá er þeim hent. Þegar aftur er komið úr InGame er allt ástandið refreshað

            if(mPath.equals("startBotMatch"))
                startActivity(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername));
            else if(mPath.equals("goToTrophy"))
                startActivity(TrophyActivity.usernameIntent(LobbyActivity.this, mUsername));
            else if(mPath.equals("goToStats"))
                startActivity(TrophyActivity.usernameIntent(LobbyActivity.this, mUsername));

            for(HashMap<String, String> msg: messages)
            {
                switch (msg.get("action"))
                {
                    case "chatEntry":
                        break;
                    case "waitEntry":
                        break;
                    case "startObserving":
                        break;
                    case "startPlaying":
                        break;
                }
            }

        }

    }

}
