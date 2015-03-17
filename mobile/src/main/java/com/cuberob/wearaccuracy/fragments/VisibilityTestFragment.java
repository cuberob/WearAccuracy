package com.cuberob.wearaccuracy.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisibilityTestFragment extends Fragment implements MessageApi.MessageListener{

    SendMessageListener mListener;

    EditText mEditText;

    public VisibilityTestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_visibility_test, container, false);

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(mEditText.getText().toString().getBytes(), "/start/notification/default");
            }
        });

        mEditText = (EditText) v.findViewById(R.id.editText);

        return v;
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

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
}
