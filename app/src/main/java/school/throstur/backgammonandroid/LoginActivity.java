package school.throstur.backgammonandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import school.throstur.backgammonandroid.Utility.Utils;

    public class LoginActivity extends AppCompatActivity {

        private AutoCompleteTextView mUsernameView;
        private EditText mPasswordView;
        private View mProgressView;
        private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        //TODO ÞÞ: Default lógíkin hér að neðan er í ruglinu, það sem á að gerast
        //TODO ÞÞ: er að annað hvort attemptSignup eða attemptLogin er kallað þegar ýtt er á takkana 2 á skjánum

        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!isUsernameValid(username))
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else
        {
            showProgress(true);
            (new UserLoginTask(username, password, "login")).execute((Void) null);
        }
    }

    private void attemptSignup() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        //TODO ÞÞ: Hér þarf að tengja rétt trxt field við hverja af breytunum 3
        String username = mUsernameView.getText().toString();
        String passwordOne = mPasswordView.getText().toString();
        String passwordTwo = "Make Backgammon great again!";
        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(passwordOne) || !passwordOne.equals(passwordTwo))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!isUsernameValid(username))
        {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else
        {
            showProgress(true);
            (new UserLoginTask(username, passwordOne, "signup")).execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        if(username == null) return false;
        String regEx = "^[a-zA-z0-9]{3,25}";
        return username.length() >= 3 && username.matches(regEx);
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
               return null;
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<HashMap<String, String>> msgs) {
            showProgress(false);

            for(HashMap<String, String> msg: msgs)
            {
                if(msg.get("action").equals("legalSignup"))
                {
                    String username = msg.get("username");
                    msgs.remove(msg);

                    startActivity(LobbyActivity.initLobbyIntent(LoginActivity.this, username, msgs));
                }
                else if(msg.get("action").equals("explain"))
                {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    Toast.makeText(LoginActivity.this, msg.get("explain"), Toast.LENGTH_LONG);
                }
            }
        }

    }

    public class LoginNetworking {
        public LoginNetworking()
        {  }

        public JSONArray tryLogin(String username, String password)
        {
            try
            {
                String url = Uri.parse("http://localhost:9090/login")
                        .buildUpon()
                        .appendQueryParameter("name", username)
                        .appendQueryParameter("pw", password)
                        .build().toString();

                return new JSONArray(Utils.getUrlString(url));
            }
            catch (Exception e)
            {
                e.getMessage();
                return null;
            }
        }

        public JSONArray trySignup(String username, String password)
        {
            try
            {
                String url = Uri.parse("http://localhost:9090/signup")
                        .buildUpon()
                        .appendQueryParameter("name", username)
                        .appendQueryParameter("pw", password)
                        .build().toString();

                return new JSONArray(Utils.getUrlString(url));
            }
            catch (Exception e)
            {
                e.getMessage();
                return null;
            }
        }
    }
}

