package com.example.noah.foodies;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

public class FoodieAnalysis extends AppCompatActivity {
    Bitmap _image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_foodie_analysis);
        _image = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
        boolean is_food = analyse_image(_image);


        ImageView imageView = findViewById(R.id.foodimage);

        imageView.setImageBitmap(_image);
    }


private boolean analyse_image(Bitmap image){
    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    String user_token = sharedPreferences.getString("user_token","DEFAULT");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

    JSONObject post = new JSONObject();

    try {
        post.put("user_token",user_token);
        post.put("image",encoded);
    } catch (JSONException e) {
        e.printStackTrace();
    }

    try {
        JSONObject result = new FoodieAPI(FoodieAPI.IS_FOOD_URL,post).execute().get();
    } catch (InterruptedException e) {
        e.printStackTrace();
    } catch (ExecutionException e) {
        e.printStackTrace();
    }
    return true;
}
    }

