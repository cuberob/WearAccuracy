package com.cuberob.wearaccuracy.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cuberob.wearaccuracy.R;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisibilityTestFragment extends Fragment implements MessageApi.MessageListener{


    public VisibilityTestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visibility_test, container, false);
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
}
