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

import com.example.noah.foodies.dummy.FoodieContent;

public class MainActivity extends AppCompatActivity {
    public static final int LOGIN_INTENT = 1;
    public static final int CAMERA_REQUEST =2;
    public static final int GALLERY_REQUEST = 3;
    public static final int ANALYSIS_REQUEST = 4;

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


        if(!sharedPreferences.contains(PreferenceKey.USER_KEY)) {
            launch_intent(LOGIN_INTENT);
        }
        else {
            initial_fragment();
        }
    }
    public void initial_fragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, ItemFragment.newInstance(2));
        transaction.commit();


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
                else{
                  //  initial_fragment();
                }
                break;

        }
        if (resultCode == Activity.RESULT_OK &&
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
}
