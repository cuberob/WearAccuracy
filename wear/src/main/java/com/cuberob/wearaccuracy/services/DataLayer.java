package com.cuberob.wearaccuracy.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.cuberob.Paths;
import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.activities.ButtonTest;
import com.cuberob.wearaccuracy.activities.VisibilityTest;
import com.cuberob.wearaccuracy.activities.VoiceTest;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DataLayer extends WearableListenerService {

    public static final String TAG = "DataLayer";
    public static final int NOTIFICATION_ID = 1337;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

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
        }else if(Paths.START_VOICE_INPUT_TEST.equals(path)){
            spawnVoiceTestNotification();
        }
    }

    private int getIntFromMessage(MessageEvent messageEvent){
        try {
            return Integer.valueOf(new String(messageEvent.getData()));
        }catch(NumberFormatException nfe){
            Log.e(TAG, "Failed to format number, returning default 10");
            return 10;
        }
    }

    private void vibrateWatch(int duration) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(duration);
    }

    private void spawnNotification(String content) {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this)
                        .setContentTitle(getString(R.string.visibility_test_notification_title))
                        .setContentText(content)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVibrate(new long[] {100, 100, 100});
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void spawnVoiceTestNotification() {
        RemoteInput remoteInput = new RemoteInput.Builder(VoiceTest.EXTRA_VOICE_REPLY)
                .setLabel(getString(R.string.reply_label))
                .build();

        // Create an intent for the reply action
        Intent replyIntent = new Intent(this, VoiceTest.class);
        PendingIntent replyPendingIntent =
                PendingIntent.getActivity(this, 0, replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        Notification.Action action =
                new Notification.Action.Builder(R.drawable.microphone_test_icon,
                        getString(R.string.start), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification and add the action via WearableExtender
        Notification notification =
                new Notification.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.voice_test_notification_contents))
                        .extend(new Notification.WearableExtender().addAction(action))
                        .build();

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }
}
