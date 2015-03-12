package com.cuberob.wearaccuracy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

/**
 * Created by rob.knegt on 11-3-2015.
 */
public class MainActivity extends WearCommunicationActivity implements MessageApi.MessageListener {

    private static final String TAG = "MainActivity";
    private SeekBar mSeekBar;
    private TextView mCyclesTextView, mResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCyclesTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mCyclesTextView = (TextView) findViewById(R.id.cyclesTextView);
        mResultsTextView = (TextView) findViewById(R.id.resultsTextView);
    }

    private byte[] getCycles(){
        return mCyclesTextView.getText().toString().getBytes();
    }

    public void onClick(View v){
        int id = v.getId();
        switch(id){
            case R.id.four_button:
                broadcastMessage(getCycles(), "/start/4");
                break;
            case R.id.two_button:
                broadcastMessage(getCycles(), "/start/2");
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
            Thread.sleep(1000);
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

        StringBuilder sb = new StringBuilder();
        sb.append("Test size: " + results.total());
        sb.append("\nCorrect: " + results.correct);
        sb.append("\nIncorrect: " + results.incorrect);
        sb.append("\nAccuracy: " + (results.accuracy() * 100) + "%");
        sb.append("\nDuration per press: " + (results.averageDuration()) + "ms");
        final String resultString = sb.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultsTextView.setText(resultString);
            }
        });
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

        @Override
        public String toString() {
            return "TestResult{" +
                    "correct=" + correct +
                    ", incorrect=" + incorrect +
                    ", duration=" + duration +
                    ", accuracy=" + accuracy() +
                    '}';
        }
    }
}
