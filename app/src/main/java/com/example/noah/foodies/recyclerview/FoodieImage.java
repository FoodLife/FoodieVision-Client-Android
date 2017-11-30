package com.example.noah.foodies.recyclerview;

import android.widget.ImageView;

/**
 * Created by noah on 11/29/17.
 */

public class FoodieImage {
    String image_id;
    ImageView image;

    public FoodieImage(String image_id, ImageView image) {
        this.image_id = image_id;
        this.image = image;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
