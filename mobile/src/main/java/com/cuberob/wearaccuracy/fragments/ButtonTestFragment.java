package com.cuberob.wearaccuracy.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;
import com.cuberob.wearaccuracy.views.PieChart;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob.knegt on 11-3-2015.
 */
public class ButtonTestFragment extends Fragment implements MessageApi.MessageListener {

    private static final String TAG = "ButtonTestActivity";
    public static final String RESULTS = "results";
    public static final String PIE_DATA = "pie_data";

    private SeekBar mSeekBar;
    private TextView mTestSizeTextView, mResultsTextView;
    private PieChart mPieChart;

    private SendMessageListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_button_test, container, false);
        mSeekBar = (SeekBar) v.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 2;
                mTestSizeTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTestSizeTextView = (TextView) v.findViewById(R.id.testSizesTextView);
        mResultsTextView = (TextView) v.findViewById(R.id.resultsTextView);
        mPieChart = (PieChart) v.findViewById(R.id.pieChart);

        mPieChart.addItem(getString(R.string.correct_label), 4, getResources().getColor(android.R.color.holo_green_light));
        mPieChart.addItem(getString(R.string.incorrect_label), 1, getResources().getColor(android.R.color.holo_red_light));


        v.findViewById(R.id.two_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(getTestSize(), "/start/2");
            }
        });
        v.findViewById(R.id.four_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(getTestSize(), "/start/4");
            }
        });

        if(savedInstanceState != null){
            Log.d(TAG, "savedInstance not null");
            mResultsTextView.setText(savedInstanceState.getString(RESULTS));
            mPieChart.setData((List<PieChart.Item>)savedInstanceState.getSerializable(PIE_DATA));
        }

        return v;
    }

    private byte[] getTestSize(){
        return mTestSizeTextView.getText().toString().getBytes();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String response = new String(messageEvent.getData());
        Gson gson = new Gson();
        TestResult results = null;
        try {
            results = gson.fromJson(response, TestResult.class);
        }catch (JsonSyntaxException e){
            Log.e(TAG, "Could not parse json!");
            return;
        }

        final String resultString = results.getResultsString();
        final int correct = results.correct;
        final int incorrect = results.incorrect;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultsTextView.setText(resultString);
                mPieChart.clearData();
                mPieChart.addItem(getString(R.string.correct_label), correct, getResources().getColor(android.R.color.holo_green_light));
                mPieChart.addItem(getString(R.string.incorrect_label), incorrect, getResources().getColor(android.R.color.holo_red_light));
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, newConfig.toString());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESULTS, mResultsTextView.getText().toString());
        outState.putSerializable(PIE_DATA, (ArrayList) mPieChart.getData());
    }

    private class TestResult {
        public int correct;
        public int incorrect;
        public long duration;

        public int total(){
            return correct + incorrect;
        }

        public double accuracy(){
            return (double)correct / (double)total();
        }

        public long averageDuration(){
            return duration / (total() - 1);
        }

        private String getResultsString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Test size: " + total());
            sb.append("\nCorrect: " + correct);
            sb.append("\nIncorrect: " + incorrect);
            sb.append("\nAccuracy: " + (accuracy() * 100) + "%");
            sb.append("\nPress Speed: " + (averageDuration()) + "ms");
            return sb.toString();
        }
    }
}
