package com.cuberob.wearaccuracy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.activities.ButtonTest;
import com.cuberob.wearaccuracy.activities.VisibilityTest;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayer extends WearableListenerService {

    public static final String TAG = "DataLayer";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        Log.d(TAG, "Message received: " + messageEvent);

        String path = messageEvent.getPath();

        if      ("/start/2".equals(path)){
            startTwoButtonTest(getIntFromMessage(messageEvent));
        }else if("/start/4".equals(path)){
            startFourButtonTest(getIntFromMessage(messageEvent));
        }else if("/start/vibration".equals(path)){
            vibrateWatch(getIntFromMessage(messageEvent));
        }else if("/start/visibility/default".equals(path)){
            spawnNotification(new String(messageEvent.getData()));
        }else if("/start/visibility/small".equals(path)){
            startSmallTextTest(new String(messageEvent.getData()));
        }else if("/start/visibility/medium".equals(path)){
            startMediumTextTest(new String(messageEvent.getData()));
        }else if("/start/visibility/large".equals(path)){
            startLargeTextTest(new String(messageEvent.getData()));
        }
    }

    private int getIntFromMessage(MessageEvent messageEvent){
        try {
            return Integer.valueOf(new String(messageEvent.getData()));
        }catch(NumberFormatException nfe){
            Log.e(TAG, "Failed to format number");
            return 10;
        }
    }

    private void vibrateWatch(int duration) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(duration);
    }

    private void startTwoButtonTest(int cycles){
        Intent i = new Intent(getApplicationContext(), ButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("cycles", cycles);
        i.putExtra("twoButtonMode", true);
        startActivity(i);
    }

    private void startFourButtonTest(int cycles){
        Intent i = new Intent(getApplicationContext(), ButtonTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("cycles", cycles);
        i.putExtra("twoButtonMode", false);
        startActivity(i);
    }

    private void spawnNotification(String content) {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setContentTitle("Visibility Test")
                        .setContentText(content)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] {100, 100, 100});
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        man.notify(1337 ,notificationBuilder.build());
    }

    private void startSmallTextTest(String content){
        Intent i = new Intent(getApplicationContext(), VisibilityTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("content", content);
        i.putExtra("size", 12);
        startActivity(i);
    }

    private void startMediumTextTest(String content){
        Intent i = new Intent(getApplicationContext(), VisibilityTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("content", content);
        i.putExtra("size", 18);
        startActivity(i);
    }

    private void startLargeTextTest(String content){
        Intent i = new Intent(getApplicationContext(), VisibilityTest.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("content", content);
        i.putExtra("size", 24);
        startActivity(i);
    }
}
