package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Adapters.ChatAdapter;
import school.throstur.backgammonandroid.Fragments.ListsFragment;
import school.throstur.backgammonandroid.Fragments.SetupMatchFragment;
import school.throstur.backgammonandroid.Utility.LobbyNetworking;
import school.throstur.backgammonandroid.Utility.Utils;

public class LobbyActivity extends AppCompatActivity {
    private static final String USERNAME_FROM_LOGIN = "usernameExtra";
    private static final String INIT_DATA_FROM_LOGIN = "hhhherrrrrrrrppppderrrrppp";
    private static final String MATCH_PRESENTATION = "ForPassingPresFromStatsToHere";
    private static final int REQUEST_CODE = 666;

    public static final String TAG = "LOBBYACTIVITY";

    private String mUsername;
    private ImageButton mSubmitChatButton;
    private EditText mChatText;
    private Button mToTrophyButton;
    private Button mToStatsButton;
    private ImageButton mSwitchFragButton;

    private SetupMatchFragment mMatchSetupFragment;
    private ListsFragment mListsFragment;
    private boolean mDisplayingLists;
    private ArrayList<HashMap<String, String>> mInitMessages;

    private RecyclerView mChatRecycler;
    private ChatAdapter mChatAdapter;

    private Timer mRefresher;

    public static Intent fromStatsTrophiesIntent(Context packageContext, HashMap<String, String> matchPres)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(MATCH_PRESENTATION, matchPres);
        return i;
    }

    public static Intent initLobbyIntent(Context packageContext, String username, ArrayList<HashMap<String, String>> initData)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(USERNAME_FROM_LOGIN, username);
        i.putExtra(INIT_DATA_FROM_LOGIN, initData);
        return i;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            mRefresher = new Timer();
            mRefresher.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    (new NetworkingTask("refresh")).execute();
                }
            }, 100, 5000);
        }
        else
        {
            HashMap<String, String> matchPresentation = (HashMap<String, String>)data.getSerializableExtra(MATCH_PRESENTATION);
            startActivityForResult(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername, matchPresentation), REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        FragmentManager fm = getSupportFragmentManager();
        mMatchSetupFragment = new SetupMatchFragment();
        mListsFragment = (ListsFragment)fm.findFragmentById(R.id.lobby_fragment_container);
        mDisplayingLists = true;

        if (mListsFragment == null) {
            mListsFragment = new ListsFragment();
            fm.beginTransaction()
                    .add(R.id.lobby_fragment_container, mListsFragment)
                    .commit();
        }

        mSwitchFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment toUse = (mDisplayingLists)? mListsFragment : mMatchSetupFragment;
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.lobby_fragment_container, toUse);
                ft.commit();
            }
        });

        mChatText = (EditText) findViewById(R.id.text_to_submit);
        mSubmitChatButton = (ImageButton) findViewById(R.id.submit_chat);
        mToTrophyButton = (Button) findViewById(R.id.to_trophy);
        mToStatsButton = (Button) findViewById(R.id.to_stats);
        mSwitchFragButton = (ImageButton) findViewById(R.id.btn_swap);

        mChatRecycler = (RecyclerView) findViewById(R.id.chat_list);

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
        }, 7000, 7500);


        mUsername = getIntent().getStringExtra(USERNAME_FROM_LOGIN);
        ArrayList<HashMap<String, String>> initialData =
                (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra(INIT_DATA_FROM_LOGIN);

        mChatAdapter = new ChatAdapter(LobbyActivity.this, this);
        mChatRecycler.setAdapter(mChatAdapter);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(LobbyActivity.this));

        mInitMessages = initialData;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        processInitialData();
    }
    private void processInitialData()
    {
        for(HashMap<String, String> msg: mInitMessages)
        {
            Log.d(LobbyActivity.TAG + " Init", msg.get("action"));
            switch (msg.get("action"))
            {
                case "chatBatch":
                    addChatBatch(msg);
                    break;
                case "waitEntry":
                    addWaitEntry(msg);
                    break;
                case "ongoingEntry":
                    addOngoingEntry(msg);
                    break;
                case "deletedEntries":
                    removeListEntries(msg);
                    break;
                case "deletedEntry":
                    removeListEntry(msg.get("id"));
                    break;
            }
        }
       // mInitMessages = null;
    }

     /*
        LOCAL EVENT HANDLING
     */

    private void submitChatEntry()
    {
        String chatEntry = mChatText.getText().toString();
        mChatText.setText("");

        if(chatEntry.length() == 0)
            Toast.makeText(LobbyActivity.this, "If you want to chat you must write something down!", Toast.LENGTH_SHORT).show();
        else
        {
            mChatAdapter.appendEntry("[" + mUsername + "]: " + chatEntry);
            mChatAdapter.notifyItemInserted(mChatAdapter.latestIndex());
            (new NetworkingTask("submitLobbyChat")).execute(chatEntry);
        }
    }

    public void setupNewMatch(String points, String addedTime,String botDiff ,boolean isHumanMatch)
    {
        if(isHumanMatch)
            (new NetworkingTask("addWaitEntry")).execute(points, addedTime);
        else
            (new NetworkingTask("startBotMatch")).execute(points, addedTime, botDiff);
    }

    /*
        HTTP RESPONSES
     */

    private void appendChatEntry(String chatEntry, String chatType)
    {
        mChatAdapter.appendEntry(chatEntry);
        mChatAdapter.notifyItemInserted(mChatAdapter.latestIndex());
    }

    private void addChatBatch(HashMap<String, String> chats)
    {
        chats.remove("action");
        for(int i = 0; i < chats.size(); i++)
            mChatAdapter.appendEntry(chats.get("" + i));
        mChatAdapter.notifyDataSetChanged();
    }

    public SetupMatchFragment getSetupMatch()
    {
        return mMatchSetupFragment;
    }
    public ListsFragment getListsFragment()
    {
        return mListsFragment;
    }

    public void attemptObservingMatch(String id)
    {
        (new NetworkingTask("observeMatch")).execute(id);
    }

    public void attemptJoiningMatch(String id)
    {
        (new NetworkingTask("joinHumanMatch")).execute(id);
    }

    public void removeWaitEntry(String id)
    {
        (new NetworkingTask("removeWaitEntry")).execute(id);
    }

    private void addOngoingEntry(HashMap<String, String> entry)
    {
        mListsFragment.addOngoingEntry(entry);
    }

    private void addWaitEntry(HashMap<String, String> entry)
    {
        boolean canCancel = entry.get("playerOne").equals(mUsername);
        mListsFragment.addWaitingEntry(entry, canCancel);
    }

    private void removeListEntries(HashMap<String, String> deleteIds)
    {
        deleteIds.remove("action");
        for(int i = 0; i < deleteIds.size(); i++)
        {
            String idToDelete = deleteIds.get("" + i);
            mListsFragment.removeListEntry(idToDelete);
        }
        mListsFragment.refreshList();
    }

    private void removeListEntry(String id)
    {
        mListsFragment.removeListEntry(id);
        mListsFragment.refreshList();
    }

    public class NetworkingTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

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
        protected ArrayList<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                switch (mPath)
                {
                    case "addWaitEntry":
                        return LobbyNetworking.addWaitEntry(mUsername, params[0], params[1]);
                    case "removeWaitEntry":
                        return LobbyNetworking.removeWaitEntry(mUsername, params[0]);
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
        protected void onPostExecute(final ArrayList<HashMap<String, String>> messages)
        {
            boolean goToTrophyRoomAfterProcessing = false;
            boolean goToStatsAfterProcessing = false;
            boolean startMatchAfterProcessing = false;
            boolean observeMatchAfterProcessing = false;
            switch (mPath)
            {
                case "startBotMatch":
                    startMatchAfterProcessing = true;
                    break;
                case "goToTrophy":
                    goToTrophyRoomAfterProcessing = true;
                    break;
                case "goToStats":
                    goToStatsAfterProcessing = true;
            }

            for(HashMap<String, String> msg: messages)
            {
                Log.d(LobbyActivity.TAG, "Message action is: " + msg.get("action"));
                switch (msg.get("action"))
                {
                    case "chatEntry":
                        appendChatEntry(msg.get("entry"), msg.get("type"));
                        break;
                    case "chatBatch":
                        addChatBatch(msg);
                        break;
                    case "waitEntry":
                        addWaitEntry(msg);
                        break;
                    case "ongoingEntry":
                        addOngoingEntry(msg);
                        break;
                    case "deletedEntries":
                        removeListEntries(msg);
                        break;
                    case "deletedEntry":
                        removeListEntry(msg.get("id"));
                        break;
                    case "explain":
                        Toast.makeText(LobbyActivity.this, msg.get("explain"), Toast.LENGTH_LONG).show();
                        break;
                    case "matchAvailable":
                        if(mPath.equals("joinHumanMatch"))
                            startMatchAfterProcessing = true;
                        else if(mPath.equals("observeMatch"))
                            observeMatchAfterProcessing = true;
                }
            }

            if(startMatchAfterProcessing)
            {
                HashMap<String, String> matchPresentation = Utils.extractSpecificAction(messages, "presentMatch");
                startActivityForResult(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername, matchPresentation), REQUEST_CODE);
            }
            else if(observeMatchAfterProcessing)
            {
                HashMap<String, String> currentBoardState = Utils.extractSpecificAction(messages, "wholeBoard");
                startActivityForResult(InGameActivity.obersvingUserIntent(LobbyActivity.this, mUsername, currentBoardState), REQUEST_CODE);
            }
            else if(goToTrophyRoomAfterProcessing)
            {
                ArrayList<HashMap<String, String>> trophyMessages = Utils.extractSpecificActions(messages, "trophyEntry");
                startActivityForResult(TrophyActivity.trophyDataIntent(LobbyActivity.this, mUsername, trophyMessages), REQUEST_CODE);
            }
            else if(goToStatsAfterProcessing)
            {
                ArrayList<HashMap<String, String>> statsMessages = Utils.extractSpecificActions(messages, "versusStats");
                startActivityForResult(StatsActivity.statsDataIntent(LobbyActivity.this, mUsername, statsMessages), REQUEST_CODE);
            }

        }
    }
}
