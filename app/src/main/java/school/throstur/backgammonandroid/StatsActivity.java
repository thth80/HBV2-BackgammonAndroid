package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatsActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private String mUsername;

    public static Intent usernameIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(SENT_USERNAME, username);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_stats);

        mUsername = getIntent().getStringExtra(SENT_USERNAME);
        NetworkingTask initStats = new NetworkingTask(mUsername, "initStats");
        initStats.execute();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask(mUsername, "checkForMatch")).execute();
            }
        }, 0, 5000);
    }

    private void appendToStatsList(String currUser, String opponent, String currPoints, String oppoPoints, String currPawns, String oppoPawns)
    {
        //TODO ÞÞ: Gegn tilteknum spilara. Entry fer aftast í listann.
    }

    private void prependToStatsList(String userPoints, String othersPoints)
    {
        //TODO ÞÞ: Overall stats- entry, fer fremst/efst í listann.
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String username, String path)
        {
            mUsername = username;
            mPath = path;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                if(mPath.equals("initStats"))
                    return TrophyStatsNetworking.initStats(mUsername);
                else
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
            {
                if(msg.get("action").equals("versusStats"))
                    appendToStatsList(msg.get("playerOne"), msg.get("playerTwo"), msg.get("pointsOne"), msg.get("pointsTwo"),
                            msg.get("pawnsOne"), msg.get("pawnsTwo"));
                else if(msg.get("action").equals("overallStats"))
                    prependToStatsList(msg.get("pointsFor"), msg.get("pointsAgainst"));
                else if(msg.get("action").equals("matchJoined"))
                    ;
            }
        }
    }
}
