package com.example.noah.foodies;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by noah on 11/26/17.
 */

public class FoodieAPI extends AsyncTask<Void, Void, JSONObject>{
    public static final String ROOT_IP = "34.232.146.205:5000";
    public static final String ROOT_URL = "http://" + ROOT_IP + "/foodies/";
    public static final String IMAGE_REQUEST_ROOT = ROOT_URL + "image/";
    public static final String LOGIN_URL = ROOT_URL + "login";
    public static final String CREATE_USER_URL = ROOT_URL + "create_user";
    public static final String IS_FOOD_URL = ROOT_URL + "is_food";
    public static final String SEARCH = ROOT_URL + "search";
    public static final String IMAGE_INFO = ROOT_URL + "imageinfo";
    public static final String LOGOUT = ROOT_URL + "logout";

    String _url_address;
    JSONObject _post;
    JSONObject return_json;

    public FoodieAPI(String url_address,JSONObject post){
        this._url_address = url_address;
        this._post = post;
    }
    public JSONObject post() {

        // TODO: attempt authentication against a network service.
        String return_val = null;



        try {

            URL url = new URL(_url_address);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Host", _url_address);
            conn.connect();

            OutputStreamWriter out =new OutputStreamWriter(conn.getOutputStream());


            // out.writeBytes(URLEncoder.encode(json.toString(),"UTF-8"));
            String data = _post.toString();
            out.write(data);
            out.flush();
            out.close();

            InputStream input = new BufferedInputStream(conn.getInputStream());

            Scanner s = new Scanner(input).useDelimiter("\\A");
            return_val = s.hasNext() ? s.next() : "";



        } catch (IOException e) {
           return null;
        }
        try {
            return new JSONObject(return_val);
        } catch (JSONException e) {
            return null;
        }
    }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            return post();
        }


}
