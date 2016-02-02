package com.jpdevs.voiceio;

import android.app.Activity;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.Locale;
import java.util.Random;

public class Voice implements TextToSpeech.OnInitListener {
    private final String TAG = Voice.class.getName();

    private Activity activity;
    private TextToSpeech tts;

    public Voice(Activity context) {
        this.activity = context;
        tts = new TextToSpeech(this.activity, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Snackbar.make(
                        activity.findViewById(android.R.id.content),
                        "This Language is not supported",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        } else {
            Log.e(TAG, "TTS Initialization Failed!");
        }
    }

    public void say(String text, boolean shouldSayNow) {
        int queueMode = shouldSayNow ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
        String reqId = String.valueOf(new Random().nextLong());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, queueMode, null, reqId);
        } else {
            tts.speak(text, queueMode, null);
        }
    }

    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        activity = null;
    }
}