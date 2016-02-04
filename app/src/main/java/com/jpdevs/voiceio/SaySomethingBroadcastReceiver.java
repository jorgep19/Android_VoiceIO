package com.jpdevs.voiceio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class SaySomethingBroadcastReceiver extends BroadcastReceiver {
    public static final int REQ_CODE = 21;

    private static final String PHRASE_KEY = "com.jpdevs.voiceio.SaySomethingBroadcastReceiver.text";
    private static final String INTENT_ACTION = "com.jpdevs.voiceio.SaySomethingBroadcastReceiver.Action";

    /**
     * Encapsulates the data to start this service placing the the required data in the
     * intent that will start the service.
     *
     * @param context the context from where the service is getting started from
     * @param phrase the phrase that will be passed to the SaySomethingService
     */
    public static Intent getSaySignalIntent(Context context, String phrase) {
        Intent intent = new Intent(context, SaySomethingBroadcastReceiver.class);
        intent.setAction(INTENT_ACTION);
        intent.putExtra(PHRASE_KEY, phrase);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // retrieve the phrase and start the SaySomethingService with it.
        String phrase = intent.getStringExtra(PHRASE_KEY);
        SaySomethingService.start(context, phrase);
    }

}
