package com.cuberob.wearaccuracy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.activities.ButtonTest;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayer extends WearableListenerService {

    public static final String TAG = "DataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d(TAG, "Message received: " + messageEvent);

        String path = messageEvent.getPath();

        if("/start/2".equals(path)){
            startTwoButtonTest(getIntFromMessage(messageEvent));
        }else if("/start/4".equals(path)){
            startFourButtonTest(getIntFromMessage(messageEvent));
        }else if("/start/vibration".equals(path)){
            int duration = getIntFromMessage(messageEvent);
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(duration);
        }else if("/start/notification".equals(path)){
            spawnNotification(new String(messageEvent.getData()));
        }
    }

    private void spawnNotification(String content) {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                .setContentTitle("Visibility Test")
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] {0, 100});
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        man.notify(1337 ,notificationBuilder.build());
    }

    private int getIntFromMessage(MessageEvent messageEvent){
        try {
            return Integer.valueOf(new String(messageEvent.getData()));
        }catch(NumberFormatException nfe){
            Log.e(TAG, "Failed to format number");
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
