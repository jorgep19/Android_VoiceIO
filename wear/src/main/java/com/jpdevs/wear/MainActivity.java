package com.jpdevs.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.jpdevs.Ears;

import java.util.Random;

public class MainActivity extends Activity {
    private static final int SPEECH_REQUEST_CODE = 19;

    private Ears ears;
    private ImageButton micBtn;
    private GoogleApiClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create our Ears object and setup the listener for the behavior we want whenever
        // voice input is processed
        ears = new Ears(SPEECH_REQUEST_CODE);
        ears.addListener(new Ears.SoundsProcessedListener() {
            @Override
            public void onSoundProcessed(Ears.Guess[] guesses) {
                new TellPhoneTask(gClient).execute(guesses);
            }
        });

        gClient = buildGoogleApiClient();

        // Setup microphone button behavior
        micBtn = (ImageButton) findViewById(R.id.mic_btn);
        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ears.startListening(MainActivity.this);
                Random rand = new Random();
                new TellPhoneTask(gClient)
                    .execute(new Ears.Guess("1", rand.nextFloat()),
                             new Ears.Guess("2", rand.nextFloat()),
                             new Ears.Guess("3", rand.nextFloat()));
            }
        });

        // Disable the button temporally so that the user can't press it until the Google
        // Client has connected
        micBtn.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to Google API Client when the activity starts
        gClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect from the Google API Client when the activity ends
        gClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When receiving an activity result pass it to our ears so that
        // they process the sounds when appropriate
        ears.processSound(requestCode, resultCode, data);
    }

    private GoogleApiClient buildGoogleApiClient() {
        // Full detail about the Google API Client configuration can be seen here
        // https://developers.google.com/android/guides/api-client
        // For the purpose of this tutorial the most important line is the first
        // one on the build process which is what provides us with the Android
        // wear API to send data to the phone from the wear device.
        return new GoogleApiClient.Builder(this)
                // Required the Android Wear API
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        micBtn.setEnabled(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        retryToConnectGClient();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        retryToConnectGClient();
                    }
                })
                .build();
    }

    /**
     * Attempts to connect the Google API Client and disables the microphone button since
     * the gClient must be connected in order to send the input data to the phone.
     */
    private void retryToConnectGClient() {
        gClient.connect();
        micBtn.setEnabled(false);
    }
}
