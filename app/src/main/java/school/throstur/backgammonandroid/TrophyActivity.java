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
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrophyActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private String mUsername;

    public static Intent usernameIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(SENT_USERNAME, username);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_trophy);

        mUsername = getIntent().getStringExtra(SENT_USERNAME);
        (new NetworkingTask(mUsername, "initTrophies")).execute();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                (new NetworkingTask(mUsername, "checkForMatch")).execute();
            }
        }, 0, 5000);
    }

    private void appendToTrophyList(int id, int percent)
    {
        //TODO ÞÞ: Búa þarf til trophy entry, hann má alveg vera af sömu breidd og skjárinn. Upplýsingar verða harðkóðaðar í Utils klasanum
        String description = Utils.trophyDesc[id];
        String name = Utils.trophyNames[id];
        //TODO ÞÞ: Finna einhverja mynd(ID myndar) til að vísa í, má vera random í upphafi á meðan einhverja mynd er að finna í /res
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
        protected List<HashMap<String, String>> doInBackground(String... params) {
            try
            {
                if(mPath.equals("initTrophies"))
                    return TrophyStatsNetworking.initTrophies(mUsername);
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
                if(msg.get("action").equals("trophyEntry"))
                    appendToTrophyList(Integer.parseInt(msg.get("id")), Integer.parseInt(msg.get("current")));
                else if(msg.get("action").equals("matchJoined"))
                    System.out.print("VILLA");
            }
        }
    }

}
