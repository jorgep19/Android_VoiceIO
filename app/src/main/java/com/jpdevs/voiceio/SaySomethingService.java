package com.jpdevs.voiceio;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.jpdevs.Voice;

public class SaySomethingService extends IntentService {
    private final static String PHRASE_KEY = "com.jpdevs.voiceio.SaySomethingService.phrase";

    /**
     * Encapsulates the data to start this service placing the the required data in the
     * intent that will start the service.
     *
     * @param context the context from where the service is getting started from
     * @param phrase the phrase that will be read by the phone when the service runs
     */
    public static void start(Context context, String phrase) {
        Intent saySomethingIntent = new Intent(context, SaySomethingService.class);
        saySomethingIntent.putExtra(PHRASE_KEY, phrase);
        context.startService(saySomethingIntent);
    }

    // default constructor required so that the Android system can instantiate our service
    public SaySomethingService() { this(SaySomethingService.class.getName()); }
    // constructor required by the IntentService class to name the thread we run our service
    // for debugging
    public SaySomethingService(String name) { super(name); }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve the phrase we want the phone to read
        String phraseToSay = intent.getStringExtra(PHRASE_KEY);

        // User a Voice object to actually make the phone say our phrase
        Voice voice = new Voice(this);
        voice.say(phraseToSay, true);
    }
}
