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
import android.widget.Adapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import school.throstur.backgammonandroid.Adapters.TrophyAdapter;
import school.throstur.backgammonandroid.Utility.TrophyStatsNetworking;
import school.throstur.backgammonandroid.Utility.Utils;

public class TrophyActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private static final String SENT_TROPHIES = "trrrrrrrrrophies";

    private String mUsername;
    private RecyclerView mTrophyRecycler;
    private TrophyAdapter mAdapter;
    private Button mBackToLobby;
    private Intent mLobbyMessage;

    public static Intent trophyDataIntent(Context packageContext, String username, ArrayList<HashMap<String, String>> trophies)
    {
        Intent i = new Intent(packageContext, TrophyActivity.class);
        i.putExtra(SENT_USERNAME, username);
        i.putExtra(SENT_TROPHIES, trophies);

        Log.d("TROPHYROOM", trophies.size()+" number of trophies");
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy);

        mUsername = getIntent().getStringExtra(SENT_USERNAME);
        ArrayList<HashMap<String, String>> trophies = (ArrayList<HashMap<String, String>>)getIntent().getSerializableExtra(SENT_TROPHIES);

        mTrophyRecycler = (RecyclerView) findViewById(R.id.trophy_list);
        mTrophyRecycler.setLayoutManager(new GridLayoutManager(TrophyActivity.this, 2, GridLayoutManager.VERTICAL, false));
        mAdapter = new TrophyAdapter(TrophyActivity.this, trophies);
        mTrophyRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mLobbyMessage = null;

        /*mBackToLobby = (Button)new View(TrophyActivity.this);
        mBackToLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    */
        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask(mUsername)).execute();
            }
        }, 0, 10000); */

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

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;

        NetworkingTask(String username)
        {
            mUsername = username;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
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
                    mLobbyMessage = LobbyActivity.fromStatsTrophiesIntent(TrophyActivity.this, msg );
                    onBackPressed();
                }
        }
    }

}
