package com.example.noah.foodies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.noah.foodies.recyclerview.FoodieViewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static final int LOGIN_INTENT = 1;
    public static final int CAMERA_REQUEST =2;
    public static final int GALLERY_REQUEST = 3;
    public static final int ANALYSIS_REQUEST = 4;


    FoodieGetPictureFragment getPicture = FoodieGetPictureFragment.newInstance();
    FoodieViewFragment myPics;
    FoodieViewFragment searchPics;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    selected = (getPicture == null) ? FoodieGetPictureFragment.newInstance() : getPicture;

                    break;
                case R.id.navigation_dashboard:

                    selected = (myPics == null) ? FoodieViewFragment.newInstance(true): myPics;
                    break;
                case R.id.navigation_notifications:

                    selected = (searchPics == null) ? FoodieViewFragment.newInstance(false) : searchPics;
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, selected);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,MODE_PRIVATE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, getPicture);
        transaction.commit();

        if(!sharedPreferences.contains(PreferenceKey.USER_TOKEN)) {
            launch_intent(LOGIN_INTENT);
        }


    }
    private void launch_intent(int request){
        if (request == LOGIN_INTENT){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,LOGIN_INTENT);
        }
    } @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Bitmap photo = null;
        switch(requestCode){
            case LOGIN_INTENT:
                // Make sure the request was successful
                if (resultCode != RESULT_OK) {
                    finish();
                }
                break;

        }if (resultCode == Activity.RESULT_OK &&
                (requestCode == CAMERA_REQUEST || requestCode == GALLERY_REQUEST)) {

            Intent intent = new Intent(this,FoodieAnalysis.class);

            if (requestCode == CAMERA_REQUEST){
                Uri imageUri = data.getData();
                photo = (Bitmap) data.getExtras().get("data");

                if (photo != null){
                    intent.putExtra("image",photo);
                    intent.putExtra("type",FoodieAnalysis.FROM_CAMERA);
                }else{
                    return;
                }

            }else if (requestCode == GALLERY_REQUEST){
                Uri selectedImageUri = data.getData();
                intent.putExtra("image", selectedImageUri);

                intent.putExtra("type",FoodieAnalysis.FROM_GALLERY);
             /*   try {
                    InputStream image_stream = getContentResolver().openInputStream(selectedImageUri);
                    photo = BitmapFactory.decodeStream(image_stream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/

            }else{
                return;
            }

            startActivityForResult(intent,ANALYSIS_REQUEST);
        }
    }
    @Override
    public void onBackPressed() {

        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,MODE_PRIVATE);

        String user_token = sharedPreferences.getString(PreferenceKey.USER_TOKEN,null);

        if (user_token == null){
            logout();
        }

        JSONObject post = new JSONObject();

        try {
            post.put("user_token",user_token);
        } catch (JSONException e) {
            logout();
        }
        try {
            new FoodieAPI(FoodieAPI.LOGOUT,post).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            logout();
        }

    logout();
    }
    private void logout(){
        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(PreferenceKey.USER_TOKEN);
        edit.apply();
        finish();
    }
}