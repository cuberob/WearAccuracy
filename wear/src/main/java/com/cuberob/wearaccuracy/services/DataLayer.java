package com.cuberob.wearaccuracy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.cuberob.Paths;
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



        //Start correct test based on path
        String path = messageEvent.getPath();
        if      (Paths.START_TWO_BUTTON_TEST_PATH.equals(path)){
            startActivity(ButtonTest.getStartIntent(getApplicationContext(), getIntFromMessage(messageEvent), true));
        }else if(Paths.START_FOUR_BUTTON_TEST_PATH.equals(path)){
            startActivity(ButtonTest.getStartIntent(getApplicationContext(), getIntFromMessage(messageEvent), false));
        }else if(Paths.START_VIBRATION_TEST_PATH.equals(path)){
            vibrateWatch(getIntFromMessage(messageEvent));
        }else if(Paths.START_VISIBILITY_DEFAULT_TEST_PATH.equals(path)){
            spawnNotification(new String(messageEvent.getData()));
        }else if(Paths.START_VISIBILITY_SMALL_TEST_PATH.equals(path)){
            startActivity(VisibilityTest.getStartIntent(12, new String(messageEvent.getData()), getApplicationContext()));
        }else if(Paths.START_VISIBILITY_MEDIUM_TEST_PATH.equals(path)){
            startActivity(VisibilityTest.getStartIntent(18, new String(messageEvent.getData()), getApplicationContext()));
        }else if(Paths.START_VISIBILITY_LARGE_TEST_PATH.equals(path)){
            startActivity(VisibilityTest.getStartIntent(24, new String(messageEvent.getData()), getApplicationContext()));
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

    private void spawnNotification(String content) {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setContentTitle("Visibility Test")
                        .setContentText(content)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] {100, 100, 100});
        NotificationManager man = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        man.notify(1337, notificationBuilder.build());
    }
}
