package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Adapters.StatsAdapter;
import school.throstur.backgammonandroid.Utility.TrophyStatsNetworking;

public class StatsActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private static final String SENT_VERSUS = "xxxxxxdfdsf";

    private String mUsername;
    private StatsAdapter mAdapter;
    private RecyclerView mStatRecycler;
    private Button mBackToLobby;
    private Intent mLobbyMessage;

    public static Intent statsDataIntent(Context packageContext, String username, ArrayList<HashMap<String, String>> versusEntries)
    {
        Intent i = new Intent(packageContext, StatsActivity.class);
        i.putExtra(SENT_USERNAME, username);
        i.putExtra(SENT_VERSUS, versusEntries);

        Log.d("STATSACTIVITY", versusEntries.toString());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mLobbyMessage = null;
        mUsername = getIntent().getStringExtra(SENT_USERNAME);
        ArrayList<HashMap<String, String>> versusStats = (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra(SENT_VERSUS);

        mStatRecycler = (RecyclerView) findViewById(R.id.stats_list);
        mAdapter = new StatsAdapter(StatsActivity.this, versusStats, this);
        mStatRecycler.setLayoutManager(new GridLayoutManager(StatsActivity.this, 2, GridLayoutManager.VERTICAL, false));
        mStatRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        Toast.makeText(StatsActivity.this, "Click the progress bars for more detailed stats", Toast.LENGTH_LONG).show();

        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask(mUsername)).execute();
            }
        }, 5000, 5000); */
    }

    @Override
    public void onBackPressed()
    {
        if(mLobbyMessage == null)
            setResult(RESULT_CANCELED);
        else
            setResult(RESULT_OK, mLobbyMessage);

        finish();
    }

    public void makeToast(String toast)
    {
        Toast.makeText(StatsActivity.this, toast, Toast.LENGTH_LONG).show();
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>>
    {
        private final String mUsername;

        NetworkingTask(String username)
        {
            mUsername = username;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                return TrophyStatsNetworking.checkForJoinedMatch(mUsername);
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            for(HashMap<String, String> msg: messages)
                if(msg.get("action").equals("presentMatch"))
                {
                    mLobbyMessage = LobbyActivity.fromStatsTrophiesIntent(StatsActivity.this, msg );
                    onBackPressed();
                }
        }
    }
}
