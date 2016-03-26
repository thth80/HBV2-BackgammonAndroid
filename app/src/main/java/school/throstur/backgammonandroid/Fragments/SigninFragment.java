package school.throstur.backgammonandroid.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import school.throstur.backgammonandroid.LoginActivity;
import school.throstur.backgammonandroid.R;

/**
 * Created by Þröstur on 25.3.2016.
 */
public class SigninFragment extends Fragment {

    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordViewTwo;
    private View mProgressView;
    private View mSigninFormView;
    private Button mToLogin;


    public SigninFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin, container, false);

        mUsernameView = (AutoCompleteTextView)v.findViewById(R.id.username);

        mPasswordView = (EditText)v.findViewById(R.id.signin_password);
        if( mPasswordView != null ) {
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.signIn || id == EditorInfo.IME_NULL) {
                        return true;
                    }
                    return false;
                }
            });
        }

        mPasswordViewTwo = (EditText)v.findViewById(R.id.signin2_password);
        // Hef ekki hugmynd hvað þetta gerir
        if( mPasswordViewTwo != null ) {
            mPasswordViewTwo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.signIn || id == EditorInfo.IME_NULL) {
                        return true;
                    }
                    return false;
                }
            });
        }

        Button mUserSignInButton = (Button)v.findViewById(R.id.user_sign_in_button);
        if ( mUserSignInButton != null) {
            mUserSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptSignup();
                }
            });
        }

        mSigninFormView = v.findViewById(R.id.signin_form);
        mProgressView = v.findViewById(R.id.auth_progress);

        // Skipta úr SignInFragment í LoginFragment í auth_container
        mToLogin = (Button)v.findViewById(R.id.suggest_log_in_button);
        mToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.auth_container, loginFragment);
                ft.commit();
            }
        });
        return v;
    }

    private boolean isUsernameValid(String username) {
        if(username == null) return false;
        String regEx = "^[a-zA-z0-9]{3,25}";
        return username.length() >= 3 && username.matches(regEx);
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() >= 3;
    }

    private void attemptSignup() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String passwordOne = mPasswordView.getText().toString();
        String passwordTwo = mPasswordViewTwo.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(passwordOne))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (isPasswordValid(passwordOne) && !passwordOne.equals(passwordTwo))
        {
            mPasswordViewTwo.setError(getString(R.string.error_password_nomatch));
            focusView = mPasswordViewTwo;
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
            // Make LoginActivity perform the sign up logic
            ((LoginActivity)getActivity()).PerformSignUp(username, passwordOne);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSigninFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSigninFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
