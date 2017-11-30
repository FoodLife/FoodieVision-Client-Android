package com.example.noah.foodies;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class FoodieAnalysis extends AppCompatActivity {

    public static final int FROM_CAMERA = 0;
    public static final int FROM_GALLERY = 1;
    Bitmap _image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getExtras().getInt("type");
        setContentView(R.layout.activity_foodie_analysis);

        if (type == FROM_CAMERA) {
            _image = (Bitmap) getIntent().getParcelableExtra("image");
        }

        if(type == FROM_GALLERY){
            Uri uri = getIntent().getParcelableExtra("image");
            InputStream image_stream = null;
            try {
                image_stream = getContentResolver().openInputStream(uri);
                _image = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                finish();
            }
        }
        if (_image != null) {
            ImageView imageView = findViewById(R.id.foodimage);
_image = Bitmap.createScaledBitmap(_image, 195, 260, true);
            imageView.setImageBitmap(_image);
        }

        boolean is_food = analyse_image(_image);
    }


    private boolean analyse_image(Bitmap image) {
        int success = 0;
        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES, MODE_PRIVATE);
        if (sharedPreferences.contains(PreferenceKey.USER_TOKEN)) {
            String user_token = sharedPreferences.getString(PreferenceKey.USER_TOKEN, "DEFAULT");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            String encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


            JSONObject post = new JSONObject();

            try {
                post.put("user_token", user_token);
                post.put("image", encoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject result = new FoodieAPI(FoodieAPI.IS_FOOD_URL, post).execute().get();

                success = result.getInt("success");
                TextView textView = findViewById(R.id.textView);

                if (result.get("result").equals("Y")){

                    textView.setText("IS FOOD");
                }else{
                    textView.setText("IS NOT FOOD");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}