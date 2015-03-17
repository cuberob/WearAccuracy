package com.cuberob.wearaccuracy.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.cuberob.Paths;
import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class VibrationTestFragment extends Fragment{

    SendMessageListener mListener;
    Spinner mSpinner;

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
                mListener.sendMessage(getVibrateDuration(), Paths.START_VIBRATION_TEST_PATH);
            }
        });

        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        return v;
    }

    private byte[] getVibrateDuration(){
        String[] values = getResources().getStringArray(R.array.vibration_duration_spinner_values);
        return values[mSpinner.getSelectedItemPosition()].getBytes();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
