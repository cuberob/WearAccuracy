package com.cuberob.wearaccuracy.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.views.PieChart;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob.knegt on 11-3-2015.
 */
public class MainActivity extends WearCommunicationActivity implements MessageApi.MessageListener {

    private static final String TAG = "MainActivity";
    public static final String RESULTS = "results";
    public static final String PIE_DATA = "pie_data";

    private SeekBar mSeekBar;
    private TextView mTestSizeTextView, mResultsTextView;
    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTestSizeTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTestSizeTextView = (TextView) findViewById(R.id.testSizesTextView);
        mResultsTextView = (TextView) findViewById(R.id.resultsTextView);
        mPieChart = (PieChart) findViewById(R.id.pieChart);

        mPieChart.addItem(getString(R.string.correct_label), 4, getResources().getColor(android.R.color.holo_green_light));
        mPieChart.addItem(getString(R.string.incorrect_label), 1, getResources().getColor(android.R.color.holo_red_light));

        if(savedInstanceState != null){
            mResultsTextView.setText(savedInstanceState.getString(RESULTS));
            mPieChart.setData((List<PieChart.Item>)savedInstanceState.getSerializable(PIE_DATA));
        }

    }

    private byte[] getTestSize(){
        return mTestSizeTextView.getText().toString().getBytes();
    }

    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.four_button:
                broadcastMessage(getTestSize(), "/start/4");
                break;
            case R.id.two_button:
                broadcastMessage(getTestSize(), "/start/2");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setupListener();
            }
        }).start();

    }

    private void setupListener() {
        try {
            Thread.sleep(1000); //Give WearCommunicationActivity a second to setup connection to Google Play
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!getGoogleApiClient().isConnected()) {
            Log.e(TAG, "Google Api Client Not Connected in onResume");
            return;
        }
        Wearable.MessageApi.addListener(getGoogleApiClient(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(getGoogleApiClient(), this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String response = new String(messageEvent.getData());
        Gson gson = new Gson();
        TestResult results = gson.fromJson(response, TestResult.class);

        final String resultString = results.getResultsString();
        final int correct = results.correct;
        final int incorrect = results.incorrect;

        runOnUiThread(new Runnable() {
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(RESULTS, mResultsTextView.getText().toString());
        outState.putSerializable(PIE_DATA, (ArrayList) mPieChart.getData());
        super.onSaveInstanceState(outState);
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
