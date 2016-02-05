package com.jpdevs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * Helper class that encapsulates the logic for prompting and processing voice input.
 */
public class Ears {
    public static final String GUESSES_PATH = "/guesses";
    public static final String GUESSES_KEY = "com.jpdevs.Ears.guesses";

    private static final String DEFAULT_PROMPT = "I'm all ears";
    private static final String DEFAULT_NO_SUPPORT_MSG = "Unable to receive voice input on this device";

    private int code;
    private Locale locale;
    private String promptMsg;
    private String noVoiceSupportMsg;
    private List<SoundsProcessedListener> listeners;

    public Ears(int code) {
        this(code, DEFAULT_PROMPT, DEFAULT_NO_SUPPORT_MSG);
    }

    public Ears(int code, String promptMsg, String noVoiceSupportMsg) {
        this.code = code;
        this.promptMsg = promptMsg;
        this.noVoiceSupportMsg = noVoiceSupportMsg;
        this.locale = Locale.getDefault();
        this.listeners = new LinkedList<>();
    }

    /**
     * Will start the Android Speech recognizer from the activity passed in with the configuration
     * stored on the object. NOTE: the results of the voice input will be send to the activity's
     * onActivityResult method.
     *
     * @param activity the activity from the the voice input is being requested
     */
    public void startListening(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptMsg);
        try {
            activity.startActivityForResult(intent, code);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity, noVoiceSupportMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Parses the intent received back from the Android Speech Recognizer into Guess objects
     *
     * @param data the intent received back from the Android Speech Recognizer
     */
    public void processSound(int requestCode, int resultCode, Intent data) {
        if(shouldProcessSound(requestCode, resultCode, data)) {
            ArrayList<String> strings = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            float[] confidences = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

            Guess[] guesses = new Guess[strings.size()];
            for(int i = 0; i < guesses.length; ++i) {
                guesses[i] = new Guess(strings.get(i), confidences[i]);
            }

            notifyListeners(guesses);
        }
    }

    /**
     * Sets the locale for which the voice input will be parsed to American English
     */
    public void setToEnglish() {
        locale = Locale.US;
    }

    /**
     * Sets the locale for which the voice input will be parsed to Spanish
     */
    public void setToSpanish() {
        locale = new Locale("es", "ES");
    }

    /**
     * Sets the locale for which the voice input will be parsed to French
     */
    public void setToFrench() {
        locale = Locale.FRANCE;
    }

    /**
     * Add listener that will receive the guess for voice input processed
     * @param listener the listener to be added
     */
    public void addListener(SoundsProcessedListener listener)  {
        listeners.add(listener);
    }

    /**
     * Removes a previously added listener
     * @param listener the listener to be removed
     */
    public void removeListener(SoundsProcessedListener listener) {
        listeners.remove(listener);
    }

    /**
     * Should be call from the onActivityResult of the activity that is using this Ears object to
     * verify if the result is the one expected from the voice input
     *
     * @param requestCode the activity result's request code
     * @param resultCode the activity result's result code
     * @param data the activity result's intent
     * @return true if the parameters passed in determined that the result should be
     *         processed for voice input, false otherwise
     */
    private boolean shouldProcessSound(int requestCode, int resultCode, Intent data) {
        return code == requestCode && resultCode == Activity.RESULT_OK && null != data;
    }

    private void notifyListeners(Guess[] guesses) {
        for(SoundsProcessedListener l : listeners) {
            l.onSoundProcessed(guesses);
        }
    }

    public interface SoundsProcessedListener {
        void onSoundProcessed(Guess[] guesses);
    }

    public static class Guess implements Parcelable {
        public static final String MEANING_KEY = "com.jpdevs.Ears.Guess.meaning";
        public static final String CONFIDENCE_KEY = "com.jpdevs.Ears.Guess.confidence";

        public static final Creator<Guess> CREATOR = new Creator<Guess>() {
            @Override
            public Guess createFromParcel(Parcel in) {
                return new Guess(in);
            }

            @Override
            public Guess[] newArray(int size) {
                return new Guess[size];
            }
        };

        public final String meaning;
        public final float confidence;

        public Guess(String meaning, float confidence) {
            this.meaning = meaning;
            this.confidence = confidence;
        }

        protected Guess(Parcel in) {
            meaning = in.readString();
            confidence = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(meaning);
            dest.writeFloat(confidence);
        }

        @Override
        public String toString() {
            return String.format("%s - %.2f", meaning, confidence);
        }
    }
}
