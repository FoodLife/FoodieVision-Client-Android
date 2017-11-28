package com.example.noah.foodies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int LOGIN_INTENT = 1;
    private static final int CAMERA_REQUEST =2;
    private static final int GALLERY_REQUEST = 3;
    private static final int ANALYSIS_REQUEST = 4;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selected = FoodieGetPictureFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    selected = ItemFragment.newInstance(2);
                    break;
                case R.id.navigation_notifications:
                    selected = ItemFragment.newInstance(3);
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
        SharedPreferences.Editor edit = sharedPreferences.edit();
        //edit.remove(PreferenceKey.USER_KEY);
        edit.commit();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, ItemFragment.newInstance(2));
        transaction.commit();

        if(!sharedPreferences.contains(PreferenceKey.USER_KEY)) {
            launch_intent(LOGIN_INTENT);
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
private void launch_intent(int request){
        if (request == LOGIN_INTENT){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent,LOGIN_INTENT);
        }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        Bitmap photo = null;
        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,MODE_PRIVATE);
        switch(requestCode){
            case LOGIN_INTENT:
            // Make sure the request was successful
            if (resultCode != RESULT_OK) {
                finish();
            }
            break;

        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            Intent intent = new Intent(this,FoodieAnalysis.class);

            if (requestCode == CAMERA_REQUEST){
                photo = (Bitmap) data.getExtras().get("data");

            }else if (requestCode == GALLERY_REQUEST){
                Uri selectedImageUri = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    Toast.makeText(this.getApplicationContext(),"couldn't load image",Toast.LENGTH_SHORT);
                }
            }
            if (photo != null){
                intent.putExtra("BitmapImage",photo);
                startActivityForResult(intent,ANALYSIS_REQUEST);
            }
        }
    }
}
