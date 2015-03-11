package com.cuberob.wearaccuracy.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cuberob.wearaccuracy.activities.FourButtonTest;
import com.cuberob.wearaccuracy.activities.TwoButtonTest;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayer extends WearableListenerService {

    public static final String TAG = "DataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d(TAG, "Message received: " + messageEvent);

        if("/start".equals(messageEvent.getPath())){
            if("2".equals(new String(messageEvent.getData()))){
                startTwoButtonTest();
            }
            if("4".equals(new String(messageEvent.getData()))){
                startFourButtonTest();
            }
        }
    }


    private void startFourButtonTest(){
        Intent i = new Intent(getApplicationContext(), FourButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void startTwoButtonTest(){
        Intent i = new Intent(getApplicationContext(), TwoButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
