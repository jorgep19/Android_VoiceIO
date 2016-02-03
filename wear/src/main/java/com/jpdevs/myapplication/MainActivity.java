package com.jpdevs.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.jpdevs.Ears;

import java.util.Random;

public class MainActivity extends Activity {
    private static final int SPEECH_REQUEST_CODE = 91;

    private Ears ears;
    private ImageButton input;
    private GoogleApiClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        ears = new Ears(SPEECH_REQUEST_CODE);
        gClient = getGoogleClient();

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                input = (ImageButton) stub.findViewById(R.id.input_btn);
                input.setEnabled(false);
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ears.startListening(MainActivity.this);
                        new PublishTask(gClient).execute(getRandomArray(3));
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        gClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ears.shouldProcessSound(requestCode, resultCode, data)) {
            Ears.Guess[] guesses = ears.processSound(data);

            Log.i("VOICE", "Got: " + guesses.length);
            for (Ears.Guess guess : guesses) {
                Log.i("VOICE", "Got: " + guess.getMeaning());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gClient.disconnect();
    }

    private GoogleApiClient getGoogleClient() {
        return new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i("VOICE", "Google API Client connected.");
                        input.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("VOICE", "Google API Client lost connection will try to reconnect");
                        gClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("VOICE", "Google API Client was unable to connect");
                    }
                })
                .build();
    }

    // TODO delete
    private String[] getRandomArray(int length) {
        Random rand = new Random();
        String[] arr = new String[length];

        for(int i = 0; i < length; ++i) {
            arr[i] = "Random" + rand.nextInt();
        }

        return arr;
    }
}
