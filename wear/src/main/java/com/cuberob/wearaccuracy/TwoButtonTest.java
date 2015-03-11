package com.cuberob.wearaccuracy;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class TwoButtonTest extends Activity {

    int buttonIds[] = {R.id.top, R.id.bottom};
    int toPress = R.id.top; //Always start top right
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

                stub.findViewById(R.id.top_left).setVisibility(View.GONE);
                stub.findViewById(R.id.top_right).setVisibility(View.GONE);
                stub.findViewById(R.id.bottom_left).setVisibility(View.GONE);
                stub.findViewById(R.id.bottom_right).setVisibility(View.GONE);
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
        toPress = buttonIds[randInt(0,1)];
        findViewById(toPress).setBackgroundColor(getResources().getColor(R.color.green));

    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
