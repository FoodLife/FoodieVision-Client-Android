package com.example.noah.foodies;

import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
static final int login = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);


        setContentView(R.layout.activity_main);

        if(!sharedPreferences.contains("user_token")) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,login);
        }
        Button test = (Button) findViewById(R.id.test_post);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String user_token = sharedPreferences.getString("user_token","no");
        if (requestCode == login) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this,Main2Activity.class);
                startActivity(intent);
            }
        }
    }
public void test(){
        BufferedReader reader = null;
        try
        {
            JSONObject json = new JSONObject();
            String address = "http://34.239.222.152:5000/hello";
            try {
                json.put("json","test");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Defined URL  where to send data
            URL url = new URL(address);

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            String data = json.toString();
            wr.write( data);
            wr.flush();

            // Get the server response

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


        }
        catch(Exception ex)
        {

        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {}
        }
    }


    public class post_test  extends AsyncTask<Void, Void, Boolean> {
        JSONObject return_json;
        @Override
        protected Boolean doInBackground(Void... voids) {

            // TODO: attempt authentication against a network service.
            JSONObject json = new JSONObject();
            String address = "http://34.239.222.152:5000/hello";
            try {
                json.put("json","test");
            } catch (JSONException e) {
                e.printStackTrace();

            }
            try {

                URL url = new URL(address);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Host", address);
                conn.connect();

                OutputStreamWriter out =new OutputStreamWriter(conn.getOutputStream ());


               // out.writeBytes(URLEncoder.encode(json.toString(),"UTF-8"));
                String data = json.toString();
                out.write(data);
                out.flush();
                out.close();

                InputStream input = new BufferedInputStream(conn.getInputStream());

                Scanner s = new Scanner(input).useDelimiter("\\A");
                String return_val = s.hasNext() ? s.next() : "";

                try {
                    return_json = new JSONObject(return_val);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostUpdate(Integer... progress){
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;

            try {
                Toast.makeText(context, return_json.getString("result"), duration).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
