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

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    private static final String SENT_FROM_LOGIN = "usernameExtra";

    private String username;

    public static Intent usernameIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, LobbyActivity.class);
        i.putExtra(SENT_FROM_LOGIN, username);
        return i;
    }

    //MUNA að það þarf að vera til 6. PostboxmManagerinn, forAll sem getur geymt skilaboð sem
    //fá má í lobby, stats og trophy room

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_lobby);

        username = getIntent().getStringExtra(SENT_FROM_LOGIN);
        NetworkingTask initData = new NetworkingTask("initLobby");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                List<HashMap<String, String>> messages = null;
                switch (mPath)
                {
                    case "addHuman":
                        messages = LobbyNetworking.addHumanMatch(mUsername, params[0], params[1]);
                        break;
                    case "joinHuman":
                        messages = LobbyNetworking.joinHumanMatch(mUsername, params[0]);
                        break;
                    case "removeHuman":
                        break;
                    case "startBot":
                        break;
                    case "observeMatch":
                        break;
                    case "initLobby":
                        break;
                    case "newChat":
                        break;
                    case "refresh":
                        break;
                }
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
