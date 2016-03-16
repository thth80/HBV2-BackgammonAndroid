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
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

public class InGameActivity extends AppCompatActivity {

    //ATH: MSG um að leikmaður joinaði leik notanda má EKKI vera einungis Lobby MSG
    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";

    private String username;
    private boolean isPlaying;
    private int[] greenSquares;
    private int[] whiteSquares;
    private int pivotSquare;
    private int timeLeft;
    private AnimationCoordinator animator;

    private Button leaveMatchButton;
    private Button submitChatButton;


    //Gæti verið kallað á þessa úr Stats, Trophy eða Lobby
    public static Intent playingUserIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, true);
        return i;
    }
    //Einungis kallanleg úr Lobby
    public static Intent obersvingUserIntent(Context packageContext, String username)
    {
        Intent i = new Intent(packageContext, InGameActivity.class);
        i.putExtra(USERNAME, username);
        i.putExtra(IS_PLAYING, false);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_in_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isPlaying = getIntent().getBooleanExtra(IS_PLAYING, false);
        username = getIntent().getStringExtra(USERNAME);

    }

    //Map með lyklunum c0-c27(counts), t0-t27(teams), d0-d3(diceVals), cube
    private void setUpWholeBoard(HashMap<String, String> boardDescription)
    {
        int[] counts = new int[28];
        int[] teams = new int[28];
        int[] diceVals = new int[4];
        int cubeValue;
        for (String key : boardDescription.keySet())
        {
            int value = Integer.parseInt(boardDescription.get(key));
            if(key.length() < 4)
            {
                int index = Integer.parseInt(key.substring(1));
                char first = key.charAt(0);
                if(first == 'c')
                    counts[index] = value;
                else if(first == 't')
                    teams[index] = value;
                else if(first == 'd')
                    diceVals[index] = value;
            }
            else
                cubeValue = value;
        }

    }

    //<String, MSG, MSG>
    public class NetworkingTask extends AsyncTask<String, Void, List<HashMap<String, String>>>
    {
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
                switch (mPath)
                {
                    case "dice":
                        break;
                    case "cube":
                        break;
                    case "green":
                        break;
                    case "white":
                        break;

                }
                return null;
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
                switch (msg.get("action"))
                {
                    case "wholeBoard":
                        setUpWholeBoard(msg);
                        break;
                    case "waitEntry":
                        break;
                    case "startObserving":
                        break;
                    case "startPlaying":
                        break;
                }
            }

        }

    }

}
