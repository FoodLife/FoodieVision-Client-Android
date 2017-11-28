package com.example.noah.foodies;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FoodieGetPictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FoodieGetPictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodieGetPictureFragment extends Fragment {
    private static final int CAMERA_REQUEST = 2;
    private static final int GALLERY_REQUEST = 3;
    private static final int ANALYSIS_REQUEST = 4;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private OnFragmentInteractionListener mListener;

    public FoodieGetPictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment FoodieGetPictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoodieGetPictureFragment newInstance() {
        FoodieGetPictureFragment fragment = new FoodieGetPictureFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

                View view = inflater.inflate(R.layout.fragment_foodie_get_picture, container, false);
        Button camera = (Button)view.findViewById(R.id.Camera_button);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            ;


        });

        Button gallery = (Button)view.findViewById(R.id.Gallery_button);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                getActivity().startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
            ;


        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap photo = null;
        if (resultCode == Activity.RESULT_OK) {

            Intent intent = new Intent(getActivity(),FoodieAnalysis.class);

            if (requestCode == CAMERA_REQUEST){
                photo = (Bitmap) data.getExtras().get("data");

            }else if (requestCode == GALLERY_REQUEST){
                Uri selectedImageUri = data.getData();
                try {
                    photo = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    Toast.makeText(getActivity().getApplicationContext(),"couldn't load image",Toast.LENGTH_SHORT);
                }
            }
            if (photo != null){
                intent.putExtra("BitmapImage",photo);
                startActivityForResult(intent,ANALYSIS_REQUEST);
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
