package com.jssai.warehousepick.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import com.jssai.warehousepick.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.signature_pad)
    SignaturePad signaturePad;
    @BindView(R.id.capture)
    Button capture;
    @BindView(R.id.clear)
    Button clear;
    @BindView(R.id.signedTex)
    TextView signedTex;
    @BindView(R.id.sign_border_layout)
    FrameLayout mBorderLayout;


    // TODO: Rename and change types of parameters
    private String userType;
    public boolean signed;

    private OnFragmentInteractionListener mListener;

    public SignFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @return A new instance of fragment SignFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignFragment newInstance(String param1, boolean param2) {
        SignFragment fragment = new SignFragment();
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
            userType = getArguments().getString(ARG_PARAM1);
            signed = getArguments().getBoolean(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign, container, false);
        ButterKnife.bind(this, view);
        if (signed) {
            mBorderLayout.setVisibility(View.GONE);
            signaturePad.setVisibility(View.GONE);
            capture.setText("Re-Capture");
            signedTex.setVisibility(View.VISIBLE);
            signedTex.setText(userType+" Already Signed");
        }
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // Hidefab();

                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.up_down);
                capture.startAnimation(shake);
                clear.startAnimation(shake);
                shake.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        capture.setVisibility(View.INVISIBLE);
                        clear.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }

            @Override
            public void onSigned() {
                //     ShowFab();
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.down_up);
                capture.startAnimation(shake);
                capture.setVisibility(View.VISIBLE);
                clear.startAnimation(shake);
                clear.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClear() {

            }
        });
        return view;
    }

    public void makeSigned(){
        if (signed) {
            mBorderLayout.setVisibility(View.GONE);
            signaturePad.setVisibility(View.GONE);
            capture.setText("Re-Capture");
            signedTex.setVisibility(View.VISIBLE);
            signedTex.setText(userType+" Already Signed");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void capture(SignaturePad signaturePad, String usertype) {
        if (mListener != null) {
            mListener.Capture(signaturePad, usertype);
        }
    }

    public void Clear(SignaturePad signaturePad) {
        if (mListener != null) {
            mListener.Capture(signaturePad, userType);
        }
    }

//    public void Hidefab(){
//        if(mListener != null){
//            mListener.HideFab();
//        }
//    }
//    public void ShowFab(){
//        if(mListener != null){
//            mListener.ShowFab();
//        }
//    }


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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick({R.id.capture, R.id.clear})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.capture:
                if(capture.getText().equals("Re-Capture")){
                    Toast.makeText(getContext(),"You can sign Now, your sign will be over written",Toast.LENGTH_LONG).show();
                    capture.setText("Capture");
                    signedTex.setVisibility(View.GONE);
                    mBorderLayout.setVisibility(View.VISIBLE);
                    signaturePad.setVisibility(View.VISIBLE);
                }else {
                    capture(signaturePad, userType);
                }
                break;
            case R.id.clear:
                signaturePad.clear();
                break;
        }
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
        void Capture(SignaturePad signaturePad, String type);
//        void HideFab();
//        void ShowFab();
    }
}
