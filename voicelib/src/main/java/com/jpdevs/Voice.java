package com.jpdevs;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

/**
 * Helper class that encapsulates the logic required for reading text through the phone's speakers,
 * aka Text to Speech.
 */
public class Voice implements TextToSpeech.OnInitListener {
    private final String TAG = Voice.class.getName();

    private Context context;
    private TextToSpeech tts;
    private Locale locale;

    public Voice(Context context) {
        this.context = context;
        tts = new TextToSpeech(this.context, this);
        locale = Locale.getDefault();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            setLanguage(Locale.US);
        } else {
            // log any initialization problems that could come from the TextToSpeech object
            Log.e(TAG, "TTS Initialization Failed! Status: " + status);
        }
    }

    /**
     * Cleanup method to release the TextToSpeech resource and release the context object to avoid
     * memory leaks
     */
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        context = null;
    }

    /**
     * Will make the phone read the phrase passed in through the speakers
     *
     * @param phrase what the phone will read
     * @param shouldSayNow if phrase passed should be said right now stopping anything being said
     *                     through the phone speakers or if should be queue to be said
     *                     when appropriate
     */
    public void say(String phrase, boolean shouldSayNow) {
        int queueMode = shouldSayNow ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
        String reqId = String.valueOf(new Random().nextLong());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(phrase, queueMode, null, reqId);
        } else {
            tts.speak(phrase, queueMode, null);
        }
    }

    /**
     * Sets the locale for which the voice input will be parsed to American English
     */
    public void setToEnglish() {
        setLanguage(Locale.US);
    }

    /**
     * Sets the locale for which the voice input will be parsed to Spanish
     */
    public void setToSpanish() {
        setLanguage(new Locale("es", "ES"));
    }

    /**
     * Sets the locale for which the voice input will be parsed to French
     */
    public void setToFrench() {
        setLanguage(Locale.FRANCE);
    }


    private void setLanguage(Locale language) {
        // set the reading language
        locale = language;
        int result = tts.setLanguage(locale);

        // manage cases when the language is not supported
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(
                    context,
                    "This Language is not supported",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public enum Speed { HALF, NORMAL, ONE_AND_HALF, DOUBLE }
    public void setSpeechSpeed(Speed speed) {
        switch (speed) {
            case HALF:
                tts.setSpeechRate(0.5f);
                break;
            case NORMAL:
                tts.setSpeechRate(1f);
                break;
            case ONE_AND_HALF:
                tts.setSpeechRate(1.5f);
                break;
            case DOUBLE:
                tts.setSpeechRate(2f);
                break;
        }
    }

    public enum Pitch { VERY_LOW, LOW, NORMAL, HIGH, VERY_HIGH }
    public void setPitch(Pitch pitch) {
        switch (pitch) {
            case VERY_LOW:
                tts.setPitch(0.01f);
                break;
            case LOW:
                tts.setPitch(0.5f);
                break;
            case NORMAL:
                tts.setPitch(1f);
                break;
            case HIGH:
                tts.setPitch(1.5f);
                break;
            case VERY_HIGH:
                tts.setPitch(2f);
                break;
        }
    }
}