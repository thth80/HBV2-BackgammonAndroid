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

public class InGameActivity extends AppCompatActivity {

    //ATH: MSG um að leikmaður joinaði leik notanda má EKKI vera einungis Lobby MSG
    private static final String USERNAME = "nameOfUser";
    private static final String IS_PLAYING = "canPlay";

    private String username = "addi";
    private boolean isPlaying = false;
    private int[] greenSquares;
    private int[] whiteSquares;
    private int pivotSquare;
    private int timeLeft;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your hello Addi action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    //<String, MSG, MSG>
    public class NetworkingTask extends AsyncTask<String, Void, MSG[]> {

        private final String mUsername;
        private final String mPath;

        NetworkingTask(String path) {
            mUsername = username;
            mPath = path;
        }

        @Override
        protected MSG[] doInBackground(String... params)
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
        protected void onPostExecute(final MSG[] message)
        {


        }

        @Override
        protected void onCancelled() {

        }
    }

}
