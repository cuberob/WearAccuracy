package com.cuberob.wearaccuracy.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisibilityTestFragment extends Fragment implements MessageApi.MessageListener{

    public static final String TAG = "VisibilityTest";
    SendMessageListener mListener;
    EditText mEditText;
    Spinner mSpinner;

    public VisibilityTestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visibility_test, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        mSpinner.setSelection(3); //Set to default on Default Notification

        v.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedItemPosition = mSpinner.getSelectedItemPosition();
                launchTest(selectedItemPosition);
            }
        });

        mEditText = (EditText) v.findViewById(R.id.editText);

        return v;
    }

    private void launchTest(int size) {
        switch(size){
            case 0:
                //small
                mListener.sendMessage(mEditText.getText().toString().getBytes(), "/start/visibility/small");
                break;
            case 1:
                //medium
                mListener.sendMessage(mEditText.getText().toString().getBytes(), "/start/visibility/medium");
                break;
            case 2:
                //large
                mListener.sendMessage(mEditText.getText().toString().getBytes(), "/start/visibility/large");
                break;
            case 3:
                //default notification
                mListener.sendMessage(mEditText.getText().toString().getBytes(), "/start/visibility/default");
                break;
            default:
                Log.e(TAG, "Invalid size, should be in range 0 - 3");
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

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
}
