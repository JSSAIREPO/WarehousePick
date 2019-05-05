package com.jssai.warehousepick.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.jssai.warehousepick.R;
import com.jssai.warehousepick.Util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public boolean mParam2;
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.capture)
    Button capture;
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.pictureExisting)
    TextView pictureExisting;
    Uri bm;
    Bitmap bitmap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private OnFragmentInteractionListener mListener;

    public PictureFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance(String param1, boolean param2) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture, container, false);
        ButterKnife.bind(this, view);
        if (mParam2) {
            picture.setVisibility(View.GONE);
            pictureExisting.setVisibility(View.VISIBLE);
            capture.setText("Re-Capture");
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void makeSigned(){
        if (mParam2) {
            picture.setVisibility(View.GONE);
            pictureExisting.setVisibility(View.VISIBLE);
            capture.setText("Re-Capture");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public boolean checkPermision() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Permission Required");
                builder.setMessage("Permission for accessing the camera and writing files to storage are required for proper functioning of the app, PLease give the permission when dialog pops up");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    }
                });
                builder.show();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            return true;
        }
        return false;
    }
    @OnClick({R.id.picture, R.id.capture, R.id.clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture:
                File root = new File(Environment
                        .getExternalStorageDirectory()
                        + File.separator + "tempdir" + File.separator);
                root.mkdirs();
                File tempImage = new File(root, "temp.jpg");
                Uri outputFileUri = Uri.fromFile(tempImage);
                bm = outputFileUri;
                if (checkPermision()) {
                    Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, 22);
                } else {
                    Toast.makeText(getActivity(), "Please enable Permisssions", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.capture:
                if(capture.getText().equals("Re-Capture")){
                    capture.setText("Capture");
                    Toast.makeText(getContext(),"You can take picture Now, your picture will be over written",Toast.LENGTH_LONG).show();
                    pictureExisting.setVisibility(View.GONE);
                    picture.setVisibility(View.VISIBLE);
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_enhance_black_24dp);
                    picture.setImageDrawable(drawable);
                }else {
                    mListener.Capture(picture, mParam1, bm);
                }
                break;
            case R.id.clear:
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_enhance_black_24dp);
                picture.setImageDrawable(drawable);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            bm = Uri.parse(data.toURI());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), bm);
                if (bitmap != null) {
                    picture.setImageBitmap(bitmap);
                } else {
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_camera_enhance_black_24dp);
                    picture.setImageDrawable(drawable);
                }
                Log.d(Constants.TAG, "onActivityResult: ");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            picture.setImageBitmap(photo);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void Capture(ImageView imageView, String type, Uri bitmap);
//        void HideFab();
//        void ShowFab();

    }
}

