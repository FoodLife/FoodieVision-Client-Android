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

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noah.foodies.FoodieAPI;
import com.example.noah.foodies.PreferenceKey;
import com.example.noah.foodies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FoodieViewFragment extends Fragment {

    private static final String TAG = "FoodieViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private String user_key;
    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;


    protected RecyclerView mRecyclerView;
    protected FoodieAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;
    protected String _user_token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         boolean all = false;
        Bundle args = getArguments();
        if(args != null) {

           all = getArguments().getBoolean("all", false);
        }

        if (all){
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(PreferenceKey.MAIN_PREFERENCES, getContext().MODE_PRIVATE);
            _user_token = sharedPreferences.getString(PreferenceKey.USER_TOKEN,null);
        }

        initDataset();
    }

    public static FoodieViewFragment newInstance(boolean all){
        FoodieViewFragment foodieView = new FoodieViewFragment();

        Bundle args = new Bundle();
        args.putBoolean("all", all);
        foodieView.setArguments(args);

        return foodieView;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new FoodieAdapter(mDataset,rootView.getContext());
        // Set FoodieAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)




        setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {


            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        try {
            mDataset = new doSearch(_user_token,null,null).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class doSearch extends AsyncTask<Void,Void,String[]> {
String _user_name;
String _is_food;
String _user_token;

    public doSearch(String user_token,String user_name,String is_food){
        _user_name = user_name;
        _is_food = is_food;
        _user_token = user_token;
    };
        @Override
        protected String[] doInBackground(Void... voids) {

            JSONObject post = new JSONObject();
            try {
                if (_user_name != null) {
                    post.put("user_name", _user_name);
                }
                if (_is_food != null){
                    post.put("is_food",_is_food);

                }
                post.put("dummy","dummy");

                if (_user_token != null){
                    post.put("user_token",_user_token);
                }
                } catch (JSONException e) {

            }

            JSONObject result = new FoodieAPI(FoodieAPI.SEARCH,post).post();
String test = "{|}\\[|]|";
            JSONArray json = null;
            try {
                json = result.getJSONArray("result");
            } catch (JSONException e) {
            }

            ArrayList<String> urls = new ArrayList<String>();

            for (int i = 0 ; i < json.length(); i++){
                try {
                    urls.add(json.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
         }
            return urls.toArray(new String[urls.size()]);

        }
}
}
