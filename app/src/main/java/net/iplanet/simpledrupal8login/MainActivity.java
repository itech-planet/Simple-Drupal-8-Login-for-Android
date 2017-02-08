package net.iplanet.simpledrupal8login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.iplanet.drupal.v8.Authentication;
import net.iplanet.drupal.v8.Tokens;
import net.iplanet.utils.Logger;

import org.apache.http.HttpResponse;

public class MainActivity extends AppCompatActivity {

    Logger Log = new Logger();
    private String mUser;
    private String mCsrfToken;
    private String mLogoutToken;
    private String mAuth;
    private TextView mUserView;
    private TextView mCsrfView;
    private TextView mLogoutView;
    private TextView mAuthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();

        //region GET SESSION TOKENS
        if (extras != null) {
            mUser  = extras.getString(Tokens.KEY_LOGIN);
            mCsrfToken  = extras.getString(Tokens.KEY_CSRF_TOKEN_RESPONSE);
            mLogoutToken  = extras.getString(Tokens.KEY_LOGOUT_TOKEN);
            mAuth  = extras.getString(Tokens.KEY_AUTHORIZATION);
        }

        mUserView = (TextView) findViewById(R.id.txtUser);
        mCsrfView = (TextView) findViewById(R.id.mTxtCsrfToken);
        mLogoutView = (TextView) findViewById(R.id.mTxtLogoutToken);
        mAuthView = (TextView) findViewById(R.id.mTxtAuthorization);

        mUserView.setText(mUser);
        mCsrfView.setText(mCsrfToken);
        mLogoutView.setText(mLogoutToken);
        mAuthView.setText(mAuth);
    }

    public void logout_click(View view){
        try {
            new logOutTask().execute();
        }catch (Exception ex) {
            Log.SyslogException(ex);
        }
    }

    private class logOutTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... params) {
            try {
                Authentication Auth = new  Authentication();
                HttpResponse response = Auth.doLogout(mCsrfToken, mLogoutToken, mAuth);
            }catch (Exception ex) {
                Log.SyslogException(ex);
                return false;
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
