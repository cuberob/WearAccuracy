package com.cuberob.wearaccuracy.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This is a base activity we use to setup and handle communication with the watch
 * It handles the connection with Google Api Client
 * It provides some convenience methods to send messages to wearable counterpart
 */
public class BaseActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener{

    private static final String TAG = "BaseActivity";

    private static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    private static final long CONNECTION_TIME_OUT_MS = 5000;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Connected Nodes, null if not initialized
     */
    private List<Node> mConnectedNodes;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // Optionally, add additional APIs and scopes if required.
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     *
     * @return The Google Api Client
     */
    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    /**
     * Forces refresh of the connected Nodes index
     */
    public void refreshConnectedNodes(){
        mConnectedNodes = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!mGoogleApiClient.isConnected()) {
                    Log.e(TAG, "Google Api Client was not connected inside refreshConnectedNodes()");
                    mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                }
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                mConnectedNodes = result.getNodes();
            }
        }).start();
    }

    /**
     * Get a list of all connected nodes
     * @return
     */
    public List<Node> getConnectedNodes(){
        if(mConnectedNodes == null){
            Log.e(TAG, "Connected nodes was null, calling refreshConnectedNodes for you");
            refreshConnectedNodes();
            return null;
        }
        return mConnectedNodes;
    }

    /**
     * Retrieve the id of device that is most likely to be the connected Android Wear device
     * @return Returns node id of node at index 0 of the getConnectedNodes call
     */
    public String getPrimaryNode(){
        if(mConnectedNodes == null){
            Log.e(TAG, "Connected nodes was null, calling refreshConnectedNodes for you");
            refreshConnectedNodes();
            return null;
        }
        return mConnectedNodes.get(0).getId();
    }

    /**
     * Send message to all connected nodes
     * @param message the message you want to send
     * @param path the path on which the message should be send
     */
    public void broadcastMessage(byte[] message, String path) {
        Log.d(TAG, "Request to send message to path: " + path);
        if(path.charAt(0) != '/'){
            Log.e(TAG, "Path should start with /, cancelling message...");
            return;
        }
        if(!mGoogleApiClient.isConnected()){
            Log.e(TAG, "Google Api Client is not connected, cancelling message...");
            return;
        }
        if(mConnectedNodes == null){
            Log.e(TAG, "Nodes index was empty! Calling refreshConnectedNodes() for you but cancelling message...");
            refreshConnectedNodes();
            return;
        }


        for(Node node : mConnectedNodes){
            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    if(!sendMessageResult.getStatus().isSuccess()){
                        Log.e(TAG, "Error when sending message: " + sendMessageResult.getStatus().getStatusCode());
                    }
                }
            });
        }
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        refreshConnectedNodes();
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }



    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }
        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }
        mIsInResolution = true;
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
            retryConnecting();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message Received, use override to handle in your activity");
    }
}
