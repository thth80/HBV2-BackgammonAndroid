package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
    private static final String SENT_OVERALL = "derrrrrrrrpppppp";

    private String mUsername;
    private StatsAdapter mAdapter;
    private RecyclerView mStatRecycler;

    public static Intent statsDataIntent(Context packageContext, String username, ArrayList<HashMap<String, String>> versusEntries,
                                         HashMap<String, String> overallEntry)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(SENT_USERNAME, username);
        i.putExtra(SENT_VERSUS, versusEntries);
        i.putExtra(SENT_OVERALL, overallEntry);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_stats);

        mUsername = getIntent().getStringExtra(SENT_USERNAME);
        ArrayList<HashMap<String, String>> versusStats = (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra(SENT_VERSUS);
        HashMap<String, String> overallStats = (HashMap<String, String>) getIntent().getSerializableExtra(SENT_OVERALL);

        mStatRecycler = (RecyclerView) new View(StatsActivity.this);
        mAdapter = new StatsAdapter(StatsActivity.this, versusStats, this);
        mStatRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        String pointsFor = overallStats.get("pointsFor");
        String pointsAgainst = overallStats.get("pointsAgainst");

        //TODO ÞÞ AE: Tengja recycler rétt, tengja pointsFor og pointsAgainst við static UI element sem er alltaf efst á skjánum.

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask(mUsername)).execute();
            }
        }, 0, 5000);
    }

    public void makeToast(String toast)
    {
        Toast.makeText(StatsActivity.this, toast, Toast.LENGTH_LONG);
    }


    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

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
                if(msg.get("action").equals("matchJoined"))
                {

                }
        }
    }
}
