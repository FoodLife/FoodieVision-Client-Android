package com.example.noah.foodies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
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
        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);

         Button signin = (Button) findViewById(R.id.email_sign_in_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String password = mPasswordView.getText().toString();
            String user_name = mEmailView.getText().toString();

            if (user_name.equalsIgnoreCase("")){
                user_name = "guest";
            }

if(user_name.equalsIgnoreCase("guest") && !password.equalsIgnoreCase("")){
    TextInputLayout username = (TextInputLayout) findViewById(R.id.usernameLayout);
    username.setError("guest user cannot have a password");
    return;
}

                new Login(getApplicationContext(),user_name,password).execute();

            }
            ;


        });

        Button signup = (Button) findViewById((R.id.CreateUser));

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mPasswordView.getText().toString();
                String user_name = mEmailView.getText().toString();
                if (user_name.equalsIgnoreCase("") || user_name.equalsIgnoreCase("guest")){
                    user_name = "guest";
                    Toast.makeText(getApplicationContext(),"Invalid username for sign up",Toast.LENGTH_SHORT);
                    return;
                }

                JSONObject post = null;
                try {
                    post = new JSONObject("{\"user_name\":\"post\",\"password\" : \"password\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // JSONObject result = new FoodieAPI(FoodieAPI.LOGIN_URL,post).post();


                new Signup(user_name,password).execute();

            }
            ;


        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    private class Signup extends AsyncTask<Void,String,Boolean>{
        String _user_name;
        String _password;
        String message = "";
        public Signup(String user_name, String password){
            _user_name = user_name;
            _password = password;
        }
        @Override
        protected void onPostExecute(Boolean result){


            if (result){
                new Login(getApplicationContext(),_user_name,_password).execute();
            }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
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
            result = new FoodieAPI(FoodieAPI.CREATE_USER_URL,post).post();
            try {
                success = result.getInt("success");

                switch(success){
                    case  1:
                        if(result.getInt("result") > 0){
                            return true;
                        }
                    case 0:
                        message = "user already exists";
                        break;
                    case -1:
                        message = "database error";
                        break;
                }
            } catch (JSONException e) {
                message="unexpected result";
            }
    publishProgress(message);
            return false;
        }
    }
    private class Login extends AsyncTask<Void,String,Boolean>{
        String _user_name;
        String _password;
        String message = "";
        Context _context;
        public Login(Context context,String user_name, String password){
            _user_name = user_name;
            _password = password;
        }
        @Override
        protected void onPostExecute(Boolean result){

            if (result){
                setResult(RESULT_OK);
                finish();
               }
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
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
            result = new FoodieAPI(FoodieAPI.LOGIN_URL,post).post();
            try {
                success = result.getInt("success");

                switch(success){
                    case  1:
                        user_token = result.getString("result");
                        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(PreferenceKey.USER_TOKEN, user_token);
                        edit.apply();
                        return true;
                    case 0:
                        message = "invalid login";
                        break;
                    case -1:
                        message = "database error";
                        break;
                }
            } catch (JSONException e) {
                       message="unexpected result";
            }
            publishProgress(message);
            return false;
        }
    }
}
