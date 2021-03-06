package com.example.bottle.weartest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.interfaces.ConnectSetConfigListener;
import io.chirp.connect.models.ChirpError;
import io.chirp.connect.models.ConnectState;

public class MainActivity extends WearableActivity {
    
    private ChirpConnect chirpConnect;
    private static final int RESULT_REQUEST_RECORD_AUDIO = 1;


    String APP_KEY = "1DeC9BE4FBbce7bE4c8Cd024b";
    String APP_SECRET = "fCEE7b0AC9EF0b4f2Aa6cDEA999245f19Ab96FD4CbBeD45Fdb";
    String APP_CONFIG = "Drnxwnq1X2HIpEcCUryxbxnt8ZIc/eaUv4OMWPQti+tEevL82Qx+VSuYai0J9FBHTHKnER92gu2irzYRZFyfYTOsoTmH1W9N2tWI6BixZdm4xqLqZgdDVPTCHig/vUnyJGVO6Qhg+p36jEqXeWE9sNhBahmLiY08MakJfsc8HtCVjFK8iGKCiRlR94FSthJhcpP/yO1SOL732K+JwKIarN8AuNoChHH+qJe/U2l+ulBaT4Nvn+yoEO1mnM05PWSUE1MUfehnBkj+68N3dhlWd0EnEBQF7u51rvpUZNUW4B9XLSIoIEdt/rBGpSvYjlaMxHYwbo17dbldP03RZEApFGfGaXNR/0rsKc26q/L740ayeTUg2qiH4/hh9jn57CBIPhf6cDU1mjvgwrpKyy3a0YQo+TfIY5wqyy8FXzfPTAsutu55l1c7Vz8eBBoTvkVV2KNkOhx2a15uKIoDAoUOAb6IjSFNwRsfqZFHP4PO4Yrczy4xFVUSdZ1rx8IPdKlq8bLt3o6l+bzhBKklb21Sigrk/zeJ3PWF1w9v8Tiby/6cfL4Bqnf3KRXpfoDZ9YYkgdZWIx/jg2ZchelFSOq1QFcsO99rrKzl4XSiHPuO7AB+mJ7RnX3CahP4jZ/w6ezjt7948KZMzyfH7/nW3Xx++Ia5MDQcM+yVNvYlDnAaeVenbRsRNM/Fy8N7VLfTK4hKEVCkyOCQk2DABfh5q38e630WRMMpVLzp4oirrAd5sj7BJ8v97rJ4roXdYlqwGaVoqIpbV9yZ1jLy0bnlGZUXYBfs+ddqdhf0dD++SgXs4PidHZYBunRN1OUtLZPGl88aluZYQZbyIYQDVM47ZVZrP/CNQqzYbCr+X6XRe5AwMFjrbxnW3Zxp+tJNOcUEQa8wAek6Depi0BoPFPQLcGK4rEcFJHNM9cnde6NeBoY0GIpjDJutn+9NRlPPmCWFMD1WiFLd55c9Qsv3rqffeAxUHnAT9cD9UCNqItRnzCdJK+Jg3NnRxrYuaLAfyygVuDrM0rhxaKzRvZ8SkAF4k7W/KQ==";
    String TAG = "WearTest";

    Button startStopSdkBtn;
    Button startStopSendingBtn;

    Boolean startStopSdkBtnPressed = false;
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopSendingBtn = (Button) findViewById(R.id.btn1);
        startStopSdkBtn = (Button) findViewById(R.id.btn2);
        tvVersion = (TextView) findViewById(R.id.tvVersion);

        startStopSendingBtn.setAlpha(.4f);
        startStopSendingBtn.setClickable(false);
        startStopSdkBtn.setAlpha(.4f);
        startStopSdkBtn.setClickable(false);
        // Enables Always-on
        setAmbientEnabled();

        if (APP_KEY.equals("") || APP_SECRET.equals(""))
        {
            Log.e(TAG, "App key or app secret is not set");
            return;
        }

        Log.v("Connect Version: ", ChirpConnect.getVersion());
        tvVersion.setText(ChirpConnect.getVersion());

        chirpConnect = new ChirpConnect(this, APP_KEY, APP_SECRET);

        chirpConnect.setConfig(APP_CONFIG, new ConnectSetConfigListener() {

            @Override
            public void onSuccess() {

                //Set-up the connect callbacks
                chirpConnect.setListener(connectEventListener);
                //Enable Start/Stop button
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startStopSdkBtn.setAlpha(1f);
                        startStopSdkBtn.setClickable(true);
                    }
                });
            }

            @Override
            public void onError(ChirpError setConfigError) {
                Log.e("setConfigError", setConfigError.getMessage());
            }
        });
    }

    ConnectEventListener connectEventListener = new ConnectEventListener() {


        @Override
        public void onSending(byte[] data, byte channel) {
            /**
             * onSending is called when a send event begins.
             * The data argument contains the payload being sent.
             */
            String hexData = "null";

            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            Log.v("connectdemoapp", "ConnectCallback: onSending: " + hexData + " on channel: " + channel);
            updateLastPayload(hexData);
        }

        @Override
        public void onSent(byte[] data, byte channel) {
            /**
             * onSent is called when a send event has completed.
             * The data argument contains the payload that was sent.
             */
            String hexData = "null";
            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            updateLastPayload(hexData);
            Log.v("connectdemoapp", "ConnectCallback: onSent: " + hexData + " on channel: " + channel);
        }

        @Override
        public void onReceiving(byte channel) {
            /**
             * onReceiving is called when a receive event begins.
             * No data has yet been received.
             */
            Log.v("connectdemoapp", "ConnectCallback: onReceiving on channel: " + channel);
        }

        @Override
        public void onReceived(byte[] data, byte channel) {
            /**
             * onReceived is called when a receive event has completed.
             * If the payload was decoded successfully, it is passed in data.
             * Otherwise, data is null.
             */
            String hexData = "null";
            if (data != null) {
                hexData = chirpConnect.payloadToHexString(data);
            }
            Log.v("connectdemoapp", "ConnectCallback: onReceived: " + hexData + " on channel: " + channel);
            updateLastPayload(hexData);
        }

        @Override
        public void onStateChanged(byte oldState, byte newState) {
            /**
             * onStateChanged is called when the SDK changes state.
             */
            ConnectState state = ConnectState.createConnectState(newState);
            Log.v("connectdemoapp", "ConnectCallback: onStateChanged " + oldState + " -> " + newState);
            if (state == ConnectState.ConnectNotCreated) {
                updateStatus("NotCreated");
            } else if (state == ConnectState.AudioStateStopped) {
                updateStatus("Stopped");
            } else if (state == ConnectState.AudioStatePaused) {
                updateStatus("Paused");
            } else if (state == ConnectState.AudioStateRunning) {
                updateStatus("Running");
            } else if (state == ConnectState.AudioStateSending) {
                updateStatus("Sending");
            } else if (state == ConnectState.AudioStateReceiving) {
                updateStatus("Receiving");
            } else {
                updateStatus(newState + "");
            }
        }
        @Override
        public void onSystemVolumeChanged(int oldVolume, int newVolume) {
            /**
             * onSystemVolumeChanged is called when the system volume is changed.
             */
            Log.v("connectdemoapp", "System volume has been changed, notify user to increase the volume when sending data");
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RESULT_REQUEST_RECORD_AUDIO);
        }
        else {
            if (startStopSdkBtnPressed) startSdk();
            Log.v(TAG, "onresumee");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (startStopSdkBtnPressed) stopSdk();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chirpConnect.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "destroy");
        try {
            chirpConnect.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "stopp");
        stopSdk();
    }

    public void updateStatus(final String newStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
           //     status.setText(newStatus);
            }
        });
    }
    public void updateLastPayload(final String newPayload) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
             //   lastChirp.setText(newPayload);
            }
        });
    }

    public void stopSdk() {
        ChirpError error = chirpConnect.stop();
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
            return;
        }
        startStopSendingBtn.setAlpha(.4f);
        startStopSendingBtn.setClickable(false);
        startStopSdkBtn.setText("Start Sdk");
    }

    public void startSdk() {
        ChirpError error = chirpConnect.start();
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
            return;
        }
        startStopSendingBtn.setAlpha(1f);
        startStopSendingBtn.setClickable(true);
        startStopSdkBtn.setText("Stop Sdk");
    }

    public void startStopSdk(View view) {
        /**
         * Start or stop the SDK.
         * Audio is only processed when the SDK is running.
         */
        startStopSdkBtnPressed = true;
        if (chirpConnect.getConnectState() == ConnectState.AudioStateStopped) {
            startSdk();
        } else {
            stopSdk();
        }
    }

    public void sendPayload(View view) {
        /**
         * A payload is a byte array dynamic size with a maximum size defined by the config string.
         *
         * Generate a random payload, and send it.
         */
        long maxPayloadLength = chirpConnect.getMaxPayloadLength();
        long size = (long) new Random().nextInt((int) maxPayloadLength) + 1;
        byte[] payload = chirpConnect.randomPayload(size);
        long maxSize = chirpConnect.getMaxPayloadLength();
        Log.e(TAG,"maxpayloadis" + maxPayloadLength);
        if (maxSize < payload.length) {
            Log.e("ConnectError: ", "Invalid Payload");
            return;
        }
        ChirpError error = chirpConnect.send(payload);
        if (error.getCode() > 0) {
            Log.e("ConnectError: ", error.getMessage());
        }
    }
}
