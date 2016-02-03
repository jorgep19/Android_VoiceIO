package com.jpdevs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Ears {
    public static final String GUESSES_PATH = "/guesses";
    public static final String GUESSES_KEY = "com.jpdevs.Ears.guesses";

    private static final String DEFAULT_PROMPT = "I'm all ears";
    private static final String DEFAULT_NO_SUPPORT_MSG = "Unable to receive voice input on this device";

    private Locale locale;
    private int code;
    private String promptMsg;
    private String noVoiceSupportMsg;

    public Ears(int code) {
        this(code, DEFAULT_PROMPT, DEFAULT_NO_SUPPORT_MSG);
    }

    public Ears(int code, String promptMsg, String noVoiceSupportMsg) {
        this.code = code;
        this.promptMsg = promptMsg;
        this.noVoiceSupportMsg = noVoiceSupportMsg;
        this.locale = Locale.getDefault();

    }

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

    public boolean shouldProcessSound(int requestCode, int resultCode, Intent data) {
        return code == requestCode && resultCode == Activity.RESULT_OK && null != data;
    }

    public Guess[] processSound(Intent data) {
        ArrayList<String> strings = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        float[] confidences = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

        Guess[] guesses = new Guess[strings.size()];
        for(int i = 0; i < guesses.length; ++i) {
            guesses[i] = new Guess(strings.get(i), confidences[i]);
        }

        return guesses;
    }

    public void setToEnglish() {
        locale = Locale.US;
    }

    public void setToSpanish() {
        locale = new Locale("es", "ES");
    }

    public void setToFrench() {
        locale = Locale.FRANCE;
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
    }
}
