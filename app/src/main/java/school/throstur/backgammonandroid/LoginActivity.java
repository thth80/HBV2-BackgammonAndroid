package school.throstur.backgammonandroid;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import school.throstur.backgammonandroid.Fragments.LoginFragment;
import school.throstur.backgammonandroid.Utility.LobbyData;
import school.throstur.backgammonandroid.Utility.Utils;

public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.auth_container);

        // Setja Login fragment í auth_containerinn, ef það er ekki þegar
        // fragment í containernum.
        if (fragment == null) {
            fragment = new LoginFragment();
            fm.beginTransaction()
                    .add(R.id.auth_container, fragment)
                    .commit();
        }
    }


    public void PerformSignUp(String username, String password) {
        (new UserLoginTask(username, password, "signup")).execute((Void) null);
    }

    public void PerformLogin(String username, String password) {
        (new UserLoginTask(username, password, "login")).execute((Void) null);
    }





    public class UserLoginTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

        private final String mUsername;
        private final String mPassword;
        private final String mPath;

        UserLoginTask(String username, String password, String path) {
            mUsername = username;
            mPassword = password;
            mPath = path;
        }

        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params)
        {
            try
            {
                LoginNetworking networker = new LoginNetworking();
                if(mPath.equals("login"))
                    return Utils.JSONToMapList(networker.tryLogin(mUsername, mPassword));
                else
                    return Utils.JSONToMapList(networker.trySignup(mUsername, mPassword));
            }
            catch (Exception e)
            {
                Log.w(e.getMessage(), e);
               return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<HashMap<String, String>> msgs) {

            for(HashMap<String, String> msg: msgs)
            {
                if(msg.get("action") == null)
                    Log.d("INCOMING ERROR", msg.toString());
                else if(msg.get("action").equals("legalSignup"))
                {
                    String username = msg.get("username");
                    LobbyData.setData(msgs, username);

                    startActivity(LobbyActivity.initLobbyIntent(LoginActivity.this, username));
                }
                else if(msg.get("action").equals("explain"))
                {
                    Toast.makeText(LoginActivity.this, msg.get("explain"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public class LoginNetworking {
        public LoginNetworking()
        {  }

        public JSONArray tryLogin(String username, String password) throws Exception
        {
            try
            {
                String url = Uri.parse(Utils.HOST + "/login")
                        .buildUpon()
                        .appendQueryParameter("name", username)
                        .appendQueryParameter("pw", password)
                        .build().toString();

                return new JSONArray(Utils.getUrlString(url));
            }
            catch (Exception e)
            {
                Log.d("LOGINACTIVITY", e.getMessage());
                throw e;
            }
        }

        public JSONArray trySignup(String username, String password) throws Exception
        {
            try
            {
                String url = Uri.parse(Utils.HOST + "/signup")
                        .buildUpon()
                        .appendQueryParameter("name", username)
                        .appendQueryParameter("pw", password)
                        .build().toString();

                return new JSONArray(Utils.getUrlString(url));
            }
            catch (Exception e)
            {
                throw e;
            }
        }
    }
}

