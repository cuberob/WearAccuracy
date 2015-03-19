package com.cuberob.wearaccuracy.activities;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.ConfirmationActivity;

import com.cuberob.Paths;
import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.services.DataLayer;

public class VoiceTest extends BaseActivity {

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_test);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        //starting delayed thread to allow BaseActivity to refresh nodes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int success = broadcastMessage(getMessageText(getIntent()).getBytes(), Paths.RESULTS_VOICE_TEST_PATH);
                showConfirmation(success == 0);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(DataLayer.NOTIFICATION_ID); //Dismiss Voice Test Notification
                finish();
            }
        }, 500);
    }

    private void showConfirmation(boolean success) {
        int animation = success ? ConfirmationActivity.SUCCESS_ANIMATION : ConfirmationActivity.FAILURE_ANIMATION;

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                animation);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                getString(R.string.done));

        startActivity(intent);
    }

    private String getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
        }
        return null;
    }
}
