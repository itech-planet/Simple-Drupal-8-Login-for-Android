package net.iplanet.simpledrupal8login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import net.iplanet.drupal.v8.Authentication;
import net.iplanet.utils.Logger;
import net.iplanet.drupal.v8.Tokens;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    Logger Log = new Logger();
    Authentication Auth = new Authentication();
    private String mCsrfToken;
    private String mAuthorization;
    private String mLogoutToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.SyslogTrace("Set up the login form");
        mUserView = (EditText) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        Log.SyslogTrace("Attempting login");

        if (mAuthTask != null) {
            return;
        }

        mUserView.setError(null);
        mPasswordView.setError(null);
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();
        View focusView = null;

        if (validateFieldsError(user, password, focusView)) {
            Log.SyslogTrace("Validation Error");
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(user, password);
            mAuthTask.execute((Void) null);
        }

    }

    //region VALIDATION PROCEDURES

    private boolean validateFieldsError(String user, String password, View focusView) {
        Log.SyslogTrace("Validating fields");
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            mUserView.requestFocus();
            return true;
        } else if (!isUserValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            mUserView.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus();
            return true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_user));
            mPasswordView.requestFocus();
            return true;
        }
        return false;
    }

    private boolean isUserValid(String user) {
        return user.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    //endregion

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                                     .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.SyslogTrace("Login form load finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUser;
        private final String mPassword;

        UserLoginTask(String user, String password) {
            mUser = user;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try
            {
                JSONObject jsonObject = Auth.doAttemptLogin(mUser, mPassword);
                mCsrfToken = jsonObject.getString(Tokens.KEY_CSRF_TOKEN_RESPONSE);
                mLogoutToken = jsonObject.getString(Tokens.KEY_LOGOUT_TOKEN);
                Log.SyslogTrace(Tokens.KEY_CSRF_TOKEN_RESPONSE + ": " + mCsrfToken);
                Log.SyslogTrace(Tokens.KEY_LOGOUT_TOKEN + ": " + mCsrfToken);
            }
            catch (Exception ex) {
                Log.SyslogException(ex);
                return false;
            }

            // TODO: register the new account here.

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try
                {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(Tokens.KEY_LOGIN, mUser);
                    intent.putExtra(Tokens.KEY_CSRF_TOKEN_RESPONSE, mCsrfToken);
                    intent.putExtra(Tokens.KEY_LOGOUT_TOKEN, mLogoutToken);
                    mAuthorization = Auth.getAuthorization(mUser, mPassword);
                    intent.putExtra(Tokens.KEY_AUTHORIZATION, mAuthorization);
                    startActivity(intent);
                }
                catch (Exception ex) {
                    Log.SyslogException(ex);
                }
            } else {
                Log.SyslogTrace("Post execute error");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

