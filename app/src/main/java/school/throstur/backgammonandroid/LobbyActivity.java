package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Utility.LobbyNetworking;

public class LobbyActivity extends AppCompatActivity {
    private static final String SENT_FROM_LOGIN = "usernameExtra";

    private String mUsername;
    private Button mSetupMatchButton;
    private Button mSubmitChatButton;
    private Button mToTrophyButton;
    private Button mToStatsButton;

    private Timer mRefresher;

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
        NetworkingTask initLobby = new NetworkingTask("initLobby");
        initLobby.execute();

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
                (new NetworkingTask("goToTrophy")).execute();
            }
        });
        mToStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new NetworkingTask("goToStats")).execute();
            }
        });

        mRefresher = new Timer();
        mRefresher.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask("refresh")).execute();
            }
        }, 1500, 1500);
    }

     /*
        LOCAL EVENT HANDLING
     */

    private void submitChatEntry()
    {
        //TODO ÞÞ: Extract-a chat textann úr tilsvarandi View-i og setja í chatEntry.
        String chatEntry = "Trump Trump Trump! Make America great again";

        if(chatEntry.length() == 0)
            Toast.makeText(LobbyActivity.this, "If you want to chat you must write something down!", Toast.LENGTH_SHORT);
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

    /*
        HTTP RESPONSES
     */

    private void appendChatEntry(String chatEntry, String chatType)
    {
        //TODO ÞÞ: Bæta textanum í chatEntry aftast í chat UI. chatType skiptir ekki máli sem stendur
    }

    private void addChatBatch(HashMap<String, String> chats)
    {
        chats.remove("action");
        for(int i = 0; i < chats.size(); i++)
            appendChatEntry(chats.get("" + i), "regular");
    }

    private void addWaitEntry(String waiter, String points, String addedTime, String id)
    {
        //TODO ÞÞ: Búa til grafískt wait entry úr upplýsingum og bæta aftan á waiting entry listann. Tengja ID við takkann eða foreldrið
        //clock = True/false breytan var redundant. Það er tími á leiknum ef (int)addedTime > 0

        Button cancelOrJoin = (Button) new View(LobbyActivity.this);
        if(waiter.equals(mUsername))
        {
            //Case CANCEL
            cancelOrJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = "ná í gegnum view?";
                    (new NetworkingTask("removeWaitEntry")).execute(id);
                }
            });
        }
        else
        {
            //Case JOIN
            cancelOrJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = "ná í gegnum view?";
                    (new NetworkingTask("joinHumanMatch")).execute(id);
                }
            });
        }
    }

    private void addOngoingEntry(String playerOne, String playerTwo, String points, String addedTime, String id)
    {
        //TODO ÞÞ: Mjög svipað og í addWaitEntry. Búinn til grafískur entry og honum er appendað við entry listann. Tengja ID við observe takka.

        boolean timedMatch = !addedTime.equals("0");
        Button observe = (Button)new View(LobbyActivity.this);

        observe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "ná í gegnum view?";
                (new NetworkingTask("observeMatch")).execute(id);
            }
        });
    }

    private void removeListEntries(HashMap<String, String> deleteIds)
    {
        deleteIds.remove("action");
        for(int i = 0; i < deleteIds.size(); i++)
        {
            String idToDelete = deleteIds.get("" + i);

            //TODO ÞÞ: Finna lista entry(waiting eða ongoing) sem er bendlað við þetta id og fjarlægja
            //TODO ÞÞ: Entry í heild sinni. Líklega hægt að finna takkann sem er bendlaður við ID og eyða foreldri
        }
    }

    private void removeListEntry(String id)
    {
        //TODO ÞÞ: Sama og í entries hér að ofan
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = LobbyActivity.this.mUsername;
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
                    case "addWaitEntry":
                        return LobbyNetworking.addWaitEntry(mUsername, params[0], params[1]);
                    case "removeWaitEntry":
                        return LobbyNetworking.removeWaitEntry(params[0]);
                    case "joinHumanMatch":
                        return LobbyNetworking.joinHumanMatch(mUsername, params[0]);
                    case "startBotMatch":
                        return LobbyNetworking.startBotMatch(mUsername, params[0], params[1], params[2]);
                    case "observeMatch":
                        return LobbyNetworking.observeMatch(mUsername, params[0]);
                    case "submitLobbyChat":
                        return LobbyNetworking.submitLobbyChat(mUsername, params[0]);
                    case "refresh":
                        return LobbyNetworking.refresh(mUsername);
                    case "initLobby":
                        return LobbyNetworking.initLobby(mUsername);
                    case "goToTrophy":
                        return LobbyNetworking.goToTrophy(mUsername);
                    case "goToStats":
                        return LobbyNetworking.goToStats(mUsername);
                    case "leaveApp":
                        return LobbyNetworking.leaveApp(mUsername);
                }
                return null;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            //Þau tilfelli þegar vitað er að yfirgefa þarf Lobby. Tryggja þarf að /goToTrophy skili EKKI new match

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
                        appendChatEntry(msg.get("entry"), msg.get("type"));
                        break;
                    case "chatBatch":
                        addChatBatch(msg);
                        break;
                    case "waitEntry":
                        addWaitEntry(msg.get("playerOne"), msg.get("points"), msg.get("addedTime"), msg.get("id"));
                        break;
                    case "ongoingEntry":
                        addOngoingEntry(msg.get("playerOne"), msg.get("playerTwo"), msg.get("points"), msg.get("addedTime"), msg.get("id"));
                        break;
                    case "matchAvailable":
                        if(mPath.equals("joinHumanMatch"))
                            startActivity(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername));
                        else if(mPath.equals("observeMatch"))
                            startActivity(InGameActivity.obersvingUserIntent(LobbyActivity.this, mUsername));
                    case "deletedEntries":
                        removeListEntries(msg);
                        break;
                    case "deletedEntry":
                        removeListEntry(msg.get("id"));
                        break;
                    case "explain":
                        Toast.makeText(LobbyActivity.this, msg.get("explain"), Toast.LENGTH_SHORT);
                }
            }
        }
    }
}
