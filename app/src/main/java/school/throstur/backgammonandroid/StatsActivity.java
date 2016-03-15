package school.throstur.backgammonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private String username;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra(SENT_USERNAME);
        NetworkingTask initStats = new NetworkingTask("initStats");
        initStats.execute();
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = username;
            mPath = path;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params)
        {
            try
            {
                List<HashMap<String, String>> messages = TrophyStatsNetworking.initStats(mUsername);
                return messages;
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            //Farið í gegnum skilaboðin, mögulega keyrt út frá núverandi mPath
            //sem segir okkur eitthvað um samhengi skilaboðanna sem tekið er á móti

        }

        @Override
        protected void onCancelled() {

        }
    }

}
