/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.noah.foodies.recyclerview;

import com.example.noah.foodies.FoodieAPI;
import com.example.noah.foodies.FoodieView;
import com.example.noah.foodies.PreferenceKey;
import com.example.noah.foodies.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class FoodieAdapter extends RecyclerView.Adapter<FoodieAdapter.ViewHolder> {
    Context _context;
    private static final String TAG = "FoodieAdapter";

    private String[] mDataSet;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FoodieImage imageView;
        private final String imageId;
        public ViewHolder(View v) {
            super(v);
            imageView = new FoodieImage(null,null);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                new launch_intent(v.getContext(),imageView).execute();
                }
            });
            imageView.setImage( (ImageView) v.findViewById(R.id.IMAGE_VIEW));
            imageId = null;
        }

        public ImageView getImageView(){return imageView.getImage();}
        public void setImageId(String id){imageView.setImage_id(id);};
        public String getImageId(){return imageId;}
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public FoodieAdapter(String[] dataSet, Context context) {
        mDataSet = dataSet;
        _context = context;
    }
    public class launch_intent extends AsyncTask<Void,Void,Boolean>{
        boolean _private =false;
        boolean _favorite = false;
        boolean _owner = false;
        boolean _is_food = false;
        Context _context;
        FoodieImage _img;

        public launch_intent(Context context, FoodieImage img){
            _context = context;
            _img = img;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            SharedPreferences sharedPreferences = _context.getSharedPreferences(PreferenceKey.MAIN_PREFERENCES, _context.MODE_PRIVATE);
            if (sharedPreferences.contains("_user_token")) {
                String user_token = sharedPreferences.getString("_user_token", "DEFAULT");
                JSONObject post = new JSONObject();

                try {
                    post.put("_user_token",user_token);
                    post.put("picture_id",_img.getImage_id());
                } catch (JSONException e) {
                    return false;
                }

                JSONObject result = new FoodieAPI(FoodieAPI.IMAGE_INFO,post).post();
                boolean success = false;
                try {
                    success = (result.getString("success").equalsIgnoreCase("1"));
                } catch (JSONException e){
                    return false;
                }

                if (!success){
                    return success;
                }

                try {
                    _favorite = (result.getString("favorite").equalsIgnoreCase("Y"));
                    _private = (result.getString("private").equalsIgnoreCase("Y"));
                    _owner = (result.getString("owner").equalsIgnoreCase("Y"));
                    _is_food = (result.getString("is_food").equalsIgnoreCase("Y"));


                    return true;
                } catch (JSONException e) {
                    return false;
                }


            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if (result){
                _img.getImage().setDrawingCacheEnabled(true);
                Intent intent = new Intent(_context, FoodieView.class);
                intent.putExtra("image", _img.getImage().getDrawingCache());
                intent.putExtra("private", _private);
                intent.putExtra("favorite", _favorite);
                intent.putExtra("ower", _owner);
                intent.putExtra(("is_food"),_is_food);

                _context.startActivity(intent);
            }


        }
    }


    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
//viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));


         Picasso.with(viewHolder.getImageView().getContext()).load(FoodieAPI.IMAGE_REQUEST_ROOT + mDataSet[position] + ".jpg").into(viewHolder.getImageView());
        viewHolder.setImageId(mDataSet[position]);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
