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

public class TrophyActivity extends AppCompatActivity {
    private static final String SENT_USERNAME = "usernameSent";
    private String username;

    public class Trophy
    {
        private boolean isAccumulated;
        private String desctipt, name;
        private int id;
    }

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = getIntent().getStringExtra(SENT_USERNAME);
    }

    private void appendToTrophyList(int id, int current )
    {
        //TODO ÞÞ: Trophy entry er búið til. Framendi á að eiga allar upplýsingar um trophy með þessum
        //TODO ÞÞ: 2 breytum. ID á að gefa okkur mynd, textalýsingu, nafn bikars og hvort hann sé af
        //TODO ÞÞ: uppsafnaða toganum. Ætli sqlite virki ekki best í það. Ég sé um SQL-ið ef sú aðferð er notuð.
    }

    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = username;
            mPath = path;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            try {
                List<HashMap<String, String>> messages = TrophyStatsNetworking.initStats(mUsername);
                return messages;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> messages)
        {
            //TODO AE: Hér ætti að hlaða gögnum úr local sqlite yfir í Trophy[]. Id myndi svo virka sem index í það fylki.

            for(HashMap<String, String> msg: messages)
            {
                if(msg.get("action").equals("trophyEntry"))
                    appendToTrophyList(Integer.parseInt(msg.get("id")), Integer.parseInt(msg.get("current")));
                else
                    System.out.print("VILLA");
            }
        }
    }

}
