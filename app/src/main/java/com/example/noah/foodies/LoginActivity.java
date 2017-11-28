package com.example.noah.foodies;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        /*
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin(mEmailView.getText().toString(),mPasswordView.getText().toString());
                    return true;
                }
                return false;
            }
        });
*/
        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String password = mPasswordView.getText().toString();
            String user_name = mEmailView.getText().toString();
                JSONObject post = null;
                try {
                    post = new JSONObject("{\"user_name\":\"test\",\"password\" : \"password\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               // JSONObject result = new FoodieAPI(FoodieAPI.login_url,post).test();


                new Login(getApplicationContext(),user_name,password).execute();
                /*
                if( attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString())){

                }
                finish();*/
            }
            ;


        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private class Login extends AsyncTask<Void,Void,Boolean>{
        String _user_name;
        String _password;
        Context _context;
        public Login(Context context, String user_name, String password){
            _context = context;
            _user_name = user_name;
            _password = password;
        }
        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                Toast.makeText(_context,result.toString(),Toast.LENGTH_LONG);
                setResult(RESULT_OK);
                finish();
               }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            JSONObject post = new JSONObject();
            String user_token = null;
            try {
                post.put("user_name",_user_name);
                post.put("password",_password);
            } catch (JSONException e) {
            }
            JSONObject result = null;
            result = new FoodieAPI(FoodieAPI.login_url,post).test();
            try {
                user_token= result.getString("result");
            } catch (JSONException e) {
            }

            if (user_token != null) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("user_token", user_token);
                edit.commit();
                return true;
            }

            return false;
        }
    }
    protected boolean attemptLogin(String user_name, String password){
        JSONObject post = new JSONObject();
        String user_token = null;
        try {
            post.put("user_name",user_name);
            post.put("password",password);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "error building json input", Toast.LENGTH_SHORT).show();
        }
        JSONObject result = null;
        try {
            result = new FoodieAPI(FoodieAPI.login_url,post).execute().get();
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), "communication error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            Toast.makeText(getApplicationContext(), result.getString("result"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "unexpected result", Toast.LENGTH_SHORT).show();
        }
        try {
            user_token= result.getString("user_token");
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "unexpected result", Toast.LENGTH_SHORT).show();
        }

        if (user_token != null) {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("user_token", user_token);
            edit.apply();
            setContentView(R.layout.activity_main);
            return true;
        }

        return false;
    }
}
