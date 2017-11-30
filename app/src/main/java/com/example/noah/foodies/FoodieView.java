package com.example.noah.foodies;

import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodieView extends AppCompatActivity {
Bitmap _image;
boolean _own;
boolean _favorite;
boolean _priv;
boolean _is_food;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodie_view);

        _image = getIntent().getParcelableExtra("image");
        _priv = getIntent().getExtras().getBoolean("private");
       _favorite = getIntent().getExtras().getBoolean("favorite");
       _own = getIntent().getExtras().getBoolean("ower");
       _is_food = getIntent().getExtras().getBoolean("is_food");


        ImageView image = findViewById(R.id.foodieimg);

        ImageButton priv = findViewById(R.id.privateButton);
        ImageButton fav = findViewById(R.id.favButton);
        ImageButton delete = findViewById(R.id.deleteButton);


            if (_priv) {
                priv.setImageResource(android.R.drawable.ic_secure);
            }
            else{
                priv.setImageResource(android.R.drawable.ic_partial_secure);
            }
            if (_favorite){
                fav.setImageResource(android.R.drawable.btn_star_big_off);
            }else{
                fav.setImageResource(android.R.drawable.btn_star_big_on);
            }
        if(!_own){
                delete.setVisibility(View.GONE);
        }


        TextView textView = findViewById(R.id.foodienessText);

        if (_is_food){
            textView.setText("IS FOOD");
        }else{
            textView.setText("IS NOT FOOD");
        }
        image.setImageBitmap(_image);


    }
}
