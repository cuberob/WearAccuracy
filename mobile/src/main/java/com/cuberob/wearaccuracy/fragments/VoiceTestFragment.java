package com.cuberob.wearaccuracy.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cuberob.Paths;
import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoiceTestFragment extends Fragment implements MessageApi.MessageListener {

    private TextView mResponseTextView;
    private SendMessageListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_voice_test, container, false);

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(null, Paths.START_VOICE_INPUT_TEST);
            }
        });

        mResponseTextView = (TextView) v.findViewById(R.id.responseTextView);

        return v;
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        if(messageEvent.getPath().equals(Paths.RESULTS_VOICE_TEST_PATH)){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResponseTextView.setText(new String(messageEvent.getData()));
                }
            });
        }
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
    }
}
