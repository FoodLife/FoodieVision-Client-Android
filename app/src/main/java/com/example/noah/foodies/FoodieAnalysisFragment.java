package com.example.noah.foodies;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodieAnalysisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodieAnalysisFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    static private Bitmap _image;


    public FoodieAnalysisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FoodieAnalysisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoodieAnalysisFragment newInstance(Bitmap image) {
        FoodieAnalysisFragment fragment = new FoodieAnalysisFragment();
        _image = image;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blank, container, false);

        boolean is_food = analyse_image(_image);

        ImageView imageView = view.findViewById(R.id.foodimage);

        imageView.setImageBitmap(_image);

        return view;
    }

    private boolean analyse_image(Bitmap image) {
        int success = 0;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PreferenceKey.MAIN_PREFERENCES, getActivity().MODE_PRIVATE);
        if (sharedPreferences.contains("user_token")) {
            String user_token = sharedPreferences.getString("user_token", "DEFAULT");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

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
                TextView textView = getView().findViewById(R.id.textView);

                if (result.get("result").equals("Y")){

                    textView.setText("Is food");
                }else{
                    textView.setText("aint food");
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
