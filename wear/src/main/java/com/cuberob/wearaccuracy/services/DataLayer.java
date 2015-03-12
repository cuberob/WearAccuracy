package com.cuberob.wearaccuracy.services;

import android.content.Intent;
import android.util.Log;

import com.cuberob.wearaccuracy.activities.ButtonTest;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayer extends WearableListenerService {

    public static final String TAG = "DataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d(TAG, "Message received: " + messageEvent);

        if("/start/2".equals(messageEvent.getPath())){
            startTwoButtonTest(getIntFromMessage(messageEvent));
        }else if("/start/4".equals(messageEvent.getPath())){
            startFourButtonTest(getIntFromMessage(messageEvent));
        }
    }

    private int getIntFromMessage(MessageEvent messageEvent){
        try {
            return Integer.valueOf(new String(messageEvent.getData()));
        }catch(NumberFormatException nfe){
            return 10;
        }
    }


    private void startFourButtonTest(int cycles){
        Intent i = new Intent(getApplicationContext(), ButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("cycles", cycles);
        i.putExtra("twoButtonMode", false);
        startActivity(i);
    }

    private void startTwoButtonTest(int cycles){
        Intent i = new Intent(getApplicationContext(), ButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("cycles", cycles);
        i.putExtra("twoButtonMode", true);
        startActivity(i);
    }
}
