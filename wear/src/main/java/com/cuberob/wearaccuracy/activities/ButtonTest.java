package com.cuberob.wearaccuracy.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

import com.cuberob.wearaccuracy.R;

import java.util.Random;

public class ButtonTest extends Activity {

    int buttonIds[];
    int toPress;
    int correct = 0;
    int incorrect = 0;
    boolean twoButtonMode = false;

    Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoButtonMode = getIntent().getBooleanExtra("twoButtonMode", false);
        handleMode();

        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));
                if(twoButtonMode) {
                    hideFourButtons(stub);
                }
            }
        });
    }

    private void handleMode() {
        if(twoButtonMode){
            buttonIds = new int[]{R.id.top, R.id.bottom};
            toPress = buttonIds[randInt(0,1)];
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


    public void onClick(View v){
        mVibrator.vibrate(50);
        int id = v.getId();
        //correct += (id == toPress) ? 1 : 0;
        if(id == toPress){
            correct++;
        }else{
            incorrect++;
        }
        Log.d("Score", "Correct: " + correct + " InCorrect: " + incorrect);

        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.black));

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
