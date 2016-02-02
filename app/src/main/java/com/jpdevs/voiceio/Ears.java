package com.jpdevs.voiceio;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Ears {
    private Locale locale;
    private int code;

    public Ears(int code){
        this.code = code;
        locale = Locale.getDefault();
    }

    public void startListening(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, activity.getString(R.string.voice_input_prompt));
        try {
            activity.startActivityForResult(intent, code);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity,
                    activity.getString(R.string.no_support_for_voice),
                    Toast.LENGTH_SHORT).show();
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

    public static class Guess {
        String meaning;
        float confidence;

        public Guess(String meaning, float confidence) {
            this.meaning = meaning;
            this.confidence = confidence;
        }

        public String getMeaning() { return meaning; }
        public float getConfidence() { return confidence; }
    }
}
