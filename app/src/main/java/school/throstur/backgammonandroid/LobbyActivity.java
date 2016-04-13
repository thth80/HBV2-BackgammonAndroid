package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import school.throstur.backgammonandroid.Utility.LobbyData;
import school.throstur.backgammonandroid.Utility.LobbyNetworking;
import school.throstur.backgammonandroid.Utility.Utils;

public class LobbyActivity extends AppCompatActivity {
    private static final String USERNAME_FROM_LOGIN = "usernameExtra";
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

    private RecyclerView mChatRecycler;
    private ChatAdapter mChatAdapter;

    private Handler mRefreshHandler;
    private Runnable mRefreshRunnable;
    private boolean stopProcessing;

    public static Intent fromStatsTrophiesIntent(Context packageContext, HashMap<String, String> matchPres)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(MATCH_PRESENTATION, matchPres);
        return i;
    }

    public static Intent initLobbyIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(USERNAME_FROM_LOGIN, username);
        return i;
    }

    @Override
    public void onBackPressed()
    {
        (new NetworkingTask("logout")).execute();
        LobbyData.clearData();
        stopProcessing = true;
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        stopProcessing = false;
        if(resultCode == RESULT_CANCELED)
        {
            mRefreshRunnable = new Runnable() {
                @Override
                public void run() {
                    (new NetworkingTask("refresh")).execute();
                    mRefreshHandler.postDelayed(mRefreshRunnable, 1200);
                }
            };

            mRefreshHandler.postDelayed(mRefreshRunnable, 1000);
        }
        else
        {
            HashMap<String, String> matchPresentation = (HashMap<String, String>)data.getSerializableExtra(MATCH_PRESENTATION);
            startActivity(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername, matchPresentation));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        stopProcessing = false;
        mRefreshHandler = new Handler();

        FragmentManager fm = getSupportFragmentManager();
        mMatchSetupFragment = new SetupMatchFragment();

        mListsFragment = new ListsFragment();
        fm.beginTransaction()
                .add(R.id.lobby_fragment_container, mListsFragment)
                .commit();
        mDisplayingLists = true;

        mChatText = (EditText) findViewById(R.id.text_to_submit);
        mSubmitChatButton = (ImageButton) findViewById(R.id.submit_chat);
        mToTrophyButton = (Button) findViewById(R.id.to_trophy);
        mToStatsButton = (Button) findViewById(R.id.to_stats);
        mSwitchFragButton = (ImageButton) findViewById(R.id.btn_swap);

        mSwitchFragButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Fragment toUse = (mDisplayingLists)? mMatchSetupFragment : new ListsFragment();
                mDisplayingLists = !mDisplayingLists;

                // The button should have a different icon + color depending on the fragment shown
                int addResID = getResources().getIdentifier("ic_playlist_add" , "drawable", getPackageName());
                int listResID = getResources().getIdentifier("ic_action_list_2", "drawable", getPackageName());
                if(mDisplayingLists) {
                    mSwitchFragButton.setImageResource(addResID);
                    mSwitchFragButton.setBackgroundResource(R.drawable.color_green_btn_border);
                } else {
                    mSwitchFragButton.setImageResource(listResID);
                    mSwitchFragButton.setBackgroundResource(R.drawable.primary_color_btn_border);
                }

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.lobby_fragment_container, toUse);
                ft.commit();
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

        mRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                (new NetworkingTask("refresh")).execute();
                mRefreshHandler.postDelayed(mRefreshRunnable, 1500);
            }
        };

        mRefreshHandler.postDelayed(mRefreshRunnable, 1500);

        mUsername = getIntent().getStringExtra(USERNAME_FROM_LOGIN);

        mChatRecycler = (RecyclerView) findViewById(R.id.chat_list);
        mChatAdapter = new ChatAdapter(LobbyActivity.this, LobbyData.getChatEntries() ,this);
        mChatRecycler.setAdapter(mChatAdapter);
        mChatRecycler.setLayoutManager(new LinearLayoutManager(LobbyActivity.this));

    }

    @Override
    public void onResume()
    {
        super.onResume();
        stopProcessing = false;
    }

    private void submitChatEntry()
    {
        String chatEntry = mChatText.getText().toString();
        mChatText.setText("");

        if(chatEntry.length() == 0)
            Toast.makeText(LobbyActivity.this, "No text to send, no chat for you!", Toast.LENGTH_SHORT).show();
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
                    case "logout":
                        return LobbyNetworking.logOut(mUsername);
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
            if(stopProcessing) return;

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
                case "logout":
                    finish();
                case "goToStats":
                    goToStatsAfterProcessing = true;
            }

            for(HashMap<String, String> msg: messages)
            {
                if(msg.get("action") == null)
                {
                    Log.d("INCOMING ERROR POST", msg.toString());
                    continue;
                }
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

            //TODO AE: Muna að sumar aðferðir fjarlægja "ACTION"
            if(startMatchAfterProcessing || observeMatchAfterProcessing || goToTrophyRoomAfterProcessing || goToStatsAfterProcessing)
            {
                mRefreshHandler.removeCallbacks(mRefreshRunnable);
                stopProcessing = true;
            }

            if(startMatchAfterProcessing)
            {
                HashMap<String, String> matchPresentation = Utils.extractSpecificAction(messages, "presentMatch");
                startActivity(InGameActivity.playingUserIntent(LobbyActivity.this, mUsername, matchPresentation));
            }
            else if(observeMatchAfterProcessing)
            {
                HashMap<String, String> currentBoardState = Utils.extractSpecificAction(messages, "wholeBoard");
                startActivity(InGameActivity.obersvingUserIntent(LobbyActivity.this, mUsername, currentBoardState));
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
