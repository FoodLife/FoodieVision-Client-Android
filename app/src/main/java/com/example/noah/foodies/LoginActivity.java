package com.example.noah.foodies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

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
         Button signin = (Button) findViewById(R.id.email_sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String password = mPasswordView.getText().toString();
            String user_name = mEmailView.getText().toString();
                JSONObject post = null;
                try {
                    post = new JSONObject("{\"user_name\":\"post\",\"password\" : \"password\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

               // JSONObject result = new FoodieAPI(FoodieAPI.login_url,post).post();


                new Login(user_name,password).execute();
                /*
                if( attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString())){

                }
                finish();*/
            }
            ;


        });

        Button signup = (Button) findViewById((R.id.CreateUser));

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mPasswordView.getText().toString();
                String user_name = mEmailView.getText().toString();
                JSONObject post = null;
                try {
                    post = new JSONObject("{\"user_name\":\"post\",\"password\" : \"password\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // JSONObject result = new FoodieAPI(FoodieAPI.login_url,post).post();


                new Signup(user_name,password).execute();

            }
            ;


        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    private class Signup extends AsyncTask<Void,Void,Boolean>{
        String _user_name;
        String _password;
        String message = "";
        public Signup(String user_name, String password){
            _user_name = user_name;
            _password = password;
        }
        @Override
        protected void onPostExecute(Boolean result){

            Handler handler =  new Handler(getApplicationContext().getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
                }
            });
            if (result){
                new Login(_user_name,_password).execute();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int success;
            JSONObject post = new JSONObject();
            String user_token = null;
            try {
                post.put("user_name",_user_name);
                post.put("password",_password);
            } catch (JSONException e) {
            }
            JSONObject result = null;
            result = new FoodieAPI(FoodieAPI.create_usr_url,post).post();
            try {
                success = result.getInt("success");

                switch(success){
                    case  1:
                        if(result.getInt("result") > 0){
                            return true;
                        }
                    case 0:
                        message = "invalid login";
                        return false;
                    case -1:
                        message = "database error";
                        return false;
                }
            } catch (JSONException e) {
                message="unexpected result";
            }

            return false;
        }
    }
    private class Login extends AsyncTask<Void,Void,Boolean>{
        String _user_name;
        String _password;
        String message = "";
        public Login(String user_name, String password){
            _user_name = user_name;
            _password = password;
        }
        @Override
        protected void onPostExecute(Boolean result){

            Handler handler =  new Handler(getApplicationContext().getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
                }
            });
            if (result){
                setResult(RESULT_OK);
                finish();
               }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int success;
            JSONObject post = new JSONObject();
            String user_token = null;
            try {
                post.put("user_name",_user_name);
                post.put("password",_password);
            } catch (JSONException e) {
            }
            JSONObject result = null;
            result = new FoodieAPI(FoodieAPI.login_url,post).post();
            try {
                success = result.getInt("success");

                switch(success){
                    case  1:
                        user_token = result.getString("result");
                        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("_user_token", user_token);
                        edit.apply();
                        return true;
                    case 0:
                        message = "invalid login";
                        return false;
                    case -1:
                        message = "database error";
                        return false;
                }
            } catch (JSONException e) {
                       message="unexpected result";
            }

            return false;
        }
    }
}
