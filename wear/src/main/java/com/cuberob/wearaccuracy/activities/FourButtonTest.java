package com.cuberob.wearaccuracy.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;

import java.util.Random;

public class FourButtonTest extends Activity {

    int buttonIds[] = {R.id.top_left, R.id.top_right, R.id.bottom_left, R.id.bottom_right};
    int toPress = R.id.top_right; //Always start top right
    int correct = 0;
    int incorrect = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                stub.findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));
            }
        });
    }


    public void onClick(View v){
        int id = v.getId();
        //correct += (id == toPress) ? 1 : 0;
        if(id == toPress){
            correct++;
        }else{
            incorrect++;
        }
        Log.d("Score", "Correct: " + correct + " InCorrect: " + incorrect);

        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.black));
        toPress = buttonIds[randInt(0,3)];
        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));

    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
