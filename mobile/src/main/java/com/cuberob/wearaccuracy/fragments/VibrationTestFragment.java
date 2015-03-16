package com.cuberob.wearaccuracy.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.activities.BaseActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class VibrationTestFragment extends Fragment implements MessageApi.MessageListener{

    public static interface SendMessageListener {
        public void sendMessage(byte[] bytes, String path);
    }

    SendMessageListener mListener;

    public static final String TAG = "VibrationFragment";


    public VibrationTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vibration_test, container, false);
        Button b = (Button) v.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage("1000".getBytes(), "/start/vibration");
            }
        });
        setRetainInstance(true);
        return v;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message received");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SendMessageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SendMessageListener");
        }
        GoogleApiClient googleApiClient = ((BaseActivity) getActivity()).getGoogleApiClient();
        if(googleApiClient == null){
            Log.d(TAG, "was null");
            return;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
