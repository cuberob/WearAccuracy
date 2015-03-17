package com.cuberob.wearaccuracy.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

import com.cuberob.wearaccuracy.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ButtonTest extends BaseActivity {

    int buttonIds[];
    int toPress;
    int correct = 0;
    int incorrect = 0;
    int cycles;
    boolean twoButtonMode = false;

    long startTime = 0;

    Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_test);

        //Get test settings from intent
        cycles = getIntent().getIntExtra("cycles", 10);
        twoButtonMode = getIntent().getBooleanExtra("twoButtonMode", false);

        //Get vibrator for feedback
        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                handleMode(stub);
                stub.findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));
            }
        });
    }

    private void handleMode(WatchViewStub stub) {
        if(twoButtonMode){
            buttonIds = new int[]{R.id.top, R.id.bottom};
            toPress = buttonIds[randInt(0,1)];
            hideFourButtons(stub);
        }else{
            buttonIds = new int[]{R.id.top_left, R.id.top_right, R.id.bottom_left, R.id.bottom_right};
            toPress = buttonIds[randInt(0,3)];
        }
    }

    private void hideFourButtons(WatchViewStub stub) {
        stub.findViewById(R.id.top_left).setVisibility(View.GONE);
        stub.findViewById(R.id.top_right).setVisibility(View.GONE);
        stub.findViewById(R.id.bottom_left).setVisibility(View.GONE);
        stub.findViewById(R.id.bottom_right).setVisibility(View.GONE);
    }

    private boolean isFinished(){
        int total = correct + incorrect;
        return (total >= cycles);
    }

    private void finishTest(){
        sendResults();
        //TODO: Show overlay with go again button?
        finish(); //TODO: Check if broadcast comes through
    }

    private void sendResults() {
        String msg = null;
        JSONObject results = new JSONObject();
        try {
            results.put("correct", correct);
            results.put("incorrect", incorrect);
            results.put("duration", (System.currentTimeMillis() - startTime));
            msg = results.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            msg = "Error";
        }
        broadcastMessage(msg.getBytes(), "/results");
    }

    private void updateScore(int viewId){
        if(viewId == toPress){
            correct++; //correct += (v.getId() == toPress) ? 1 : 0;
        }else{
            incorrect++;
        }
    }

    public void onClick(View v){
        mVibrator.vibrate(50);

        if(startTime == 0){
            startTime = System.currentTimeMillis();
        }

        updateScore(v.getId());

        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.black));

        if(isFinished()){
            finishTest();
            return;
        }

        Log.d("Score", "Correct: " + correct + " InCorrect: " + incorrect);
        gotoNextCycle();

    }

    private void gotoNextCycle() {
        if(twoButtonMode) {
            toPress = buttonIds[randInt(0, 1)];
        }else{
            toPress = buttonIds[randInt(0,3)];
        }
        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
