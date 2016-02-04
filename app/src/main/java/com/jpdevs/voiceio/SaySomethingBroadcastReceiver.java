package com.jpdevs.voiceio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SaySomethingBroadcastReceiver extends BroadcastReceiver {
    public static final int REQ_CODE = 21;

    private static final String PHRASE_KEY = "com.jpdevs.voiceio.SaySomethingBroadcastReceiver.text";

    public static Intent getSaySignalIntent(Context context, String phrase) {
        Intent intent = new Intent(context, SaySomethingBroadcastReceiver.class);
        intent.putExtra(PHRASE_KEY, phrase);
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String phrase = intent.getStringExtra(PHRASE_KEY);
        SaySomethingService.start(context, phrase);
    }
}
