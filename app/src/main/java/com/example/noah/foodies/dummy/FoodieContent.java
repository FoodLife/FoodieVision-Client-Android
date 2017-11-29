package com.example.noah.foodies.dummy;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.noah.foodies.FoodieAPI;
import com.example.noah.foodies.PreferenceKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class FoodieContent {

public FoodieContent(){

    new getFoodie().execute();
}
    /**
     * An array of sample (dummy) items.
     */
    public static final List<FoodieItem> ITEMS = new ArrayList<FoodieItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, FoodieItem> ITEM_MAP = new HashMap<String, FoodieItem>();

    static getFoodie foodie;
    private static final int COUNT = 808;
/*
    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createFoodieItem(i));
        }
    }*/

    public class getFoodie extends AsyncTask<Void,Void,Boolean>{
        Context _context;
        public getFoodie(){

        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = _context.getSharedPreferences(PreferenceKey.MAIN_PREFERENCES,_context.MODE_PRIVATE);

            String user_token = sharedPreferences.getString(PreferenceKey.USER_KEY,"");
            JSONObject post = null;
            try {
                post= new JSONObject(user_token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject result = new FoodieAPI(FoodieAPI.SEARCH,post).post();
            String json_data = "";
            try {
                json_data = result.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] urls = json_data.replaceAll("{|}|\\[|\\]","").split(",");

            for (String item : urls){
                addItem(new FoodieItem(item),ITEMS,ITEM_MAP);
            }
            return true;
        }
    }
    private static void addItem(FoodieItem item,List<FoodieItem> list ,Map<String,FoodieItem> map) {
        list.add(item);
        map.put(item.id, item);
    }

    private static FoodieItem createFoodieItem(String name) {
        return new FoodieItem(FoodieAPI.IMAGE_REQUEST_ROOT + name);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class FoodieItem {
        public final String id;

        public FoodieItem(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return this.id;
        }
    }
}
