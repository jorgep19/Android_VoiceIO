package com.jpdevs.voiceio;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ArrayRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jpdevs.Ears;
import com.jpdevs.Ears.Guess;
import com.jpdevs.Voice;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int VOICE_INPUT_REQ = 19;

    private static final int START_REQ = 21;
    private static final String NOTIFICATION_ID_KEY  = "com.jpdevs.voiceio.notificationId";
    private static final String GUESSES_DATA_KEY = "com.jpdevs.voiceio.MainActivity";

    private static final String GUESSES_STATE = "com.jpdevs.voiceio.MainActivity.state.guesses";
    private static final String LANG_STATE = "com.jpdevs.voiceio.MainActivity.state.language";
    private static final String SPEED_STATE = "com.jpdevs.voiceio.MainActivity.state.pitch";
    private static final String PITCH_STATE = "com.jpdevs.voiceio.MainActivity.state.pitch";

    /**
     *  This method encapsulates the logic for starting the Main Activity
     *
     * @param context the context from which the activity is being invoked
     * @param notificationId the id of the notification that will start the activity so that
     *                       it can be dismissed
     * @param guesses guesses data that will be shown on a list when the activity starts
     * @return a pending intent with all the data that the MainActivity will expect when starting
     */
    public static PendingIntent getStartPendingIntent(
            Context context, int notificationId, ArrayList<Guess> guesses) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(NOTIFICATION_ID_KEY, notificationId);
        intent.putExtra(GUESSES_DATA_KEY, guesses);
        return PendingIntent.getActivity(context, START_REQ, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private Voice voice;
    private Ears ears;
    private GuessesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voice = new Voice(this);
        // Create our Ears object and setup the listener for the behavior we want whenever
        // voice input is processed
        ears = new Ears(VOICE_INPUT_REQ);
        ears.addListener(new Ears.SoundsProcessedListener() {
            @Override
            public void onSoundProcessed(Guess[] guesses) {
                adapter.setGuesses(guesses);
            }
        });

        // setup voice button
        FloatingActionButton inputFab = (FloatingActionButton) findViewById(R.id.speakFab);
        inputFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ears.startListening((Activity) v.getContext());
            }
        });

        // setup recycler view
        RecyclerView guessList = (RecyclerView) findViewById(R.id.guessesList);
        guessList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        guessList.setLayoutManager(layoutManager);
        adapter = new GuessesAdapter(voice);
        guessList.setAdapter(adapter);

        // setup pitch and speed spinner
        Spinner speedSpinner = (Spinner) findViewById(R.id.speed_spinner);
        setupSimpleSpinner(speedSpinner, R.array.voice_speed_array, new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setVoiceSpeed(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        Spinner pitchSpinner = (Spinner) findViewById(R.id.pitch_spinner);
        setupSimpleSpinner(pitchSpinner, R.array.voice_pitch_array, new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setVoicePitch(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        // Restore the guesses we where showing when appropriate
         if (savedInstanceState != null) {
            ArrayList<Guess> guesses = savedInstanceState.getParcelableArrayList(GUESSES_STATE);
            if(guesses != null) {
                adapter.setGuesses(guesses.toArray(new Guess[guesses.size()]));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // When starting from a intent with guess and notification id data process the data received
        Intent startIntent = getIntent();
        if(startIntent != null) {
            int notificationId = startIntent.getIntExtra(NOTIFICATION_ID_KEY, 0);
            ArrayList<Guess> guesses = startIntent.getParcelableArrayListExtra(GUESSES_DATA_KEY);

            // when there is a notificationId passed in dismiss the notification
            if(notificationId != 0) {
                NotificationManagerCompat notManager = NotificationManagerCompat.from(this);
                notManager.cancel(notificationId);
            }

            // when there is guesses data display them by passing them to our adapter
            if(guesses != null) {
                adapter.setGuesses(guesses.toArray(new Guess[guesses.size()]));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.languages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Based on the language picked by the user see our ears to listen for
        // the appropriate language
        String language;
        switch (item.getItemId()) {
            case R.id.spanish_item:
                language = getString(R.string.spanish);
                ears.setToSpanish();
                voice.setToSpanish();
                break;
            case R.id.english_item:
                language = getString(R.string.english);
                ears.setToEnglish();
                voice.setToEnglish();
                break;
            case R.id.french_item:
                language = getString(R.string.french);
                ears.setToFrench();
                voice.setToFrench();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.speakFab);
        Snackbar.make(fab, String.format("Set language to: %s.", language), Snackbar.LENGTH_LONG)
            .show();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When receiving an activity result pass it to our ears so that
        // they process the sounds when appropriate
        ears.processSound(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        // save guesses data when if needed
        Guess[] guesses = adapter.getGuesses();
        if(guesses.length > 0) {
            ArrayList<Guess> guessesList = new ArrayList<>(Arrays.asList(guesses));
            outState.putParcelableArrayList(GUESSES_STATE, guessesList);
        }
    }

    @Override
    public void onDestroy() {
        // tell the voice object to clean up
        voice.onDestroy();
        super.onDestroy();
    }

    /**
     * For more details check
     * http://developer.android.com/guide/topics/ui/controls/spinner.html
     * @param spinner
     */
    private void setupSimpleSpinner(Spinner spinner, @ArrayRes int data, OnItemSelectedListener listener) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(listener);
    }

    private void setVoicePitch(int position) {
        Voice.Pitch pitch;

        switch (position) {
            case 0:
                pitch = Voice.Pitch.VERY_LOW;
                break;
            case 1:
                pitch = Voice.Pitch.LOW;
                break;
            case 2:
                pitch = Voice.Pitch.NORMAL;
                break;
            case 3:
                pitch = Voice.Pitch.HIGH;
                break;
            case 4:
                pitch = Voice.Pitch.VERY_HIGH;
                break;
            default:
                pitch = Voice.Pitch.NORMAL;
        }

        voice.setPitch(pitch);
    }

    private void setVoiceSpeed(int position) {
        Voice.Speed pitch;

        switch (position) {
            case 0:
                pitch = Voice.Speed.HALF;
                break;
            case 1:
                pitch = Voice.Speed.NORMAL;
                break;
            case 2:
                pitch = Voice.Speed.ONE_AND_HALF;
                break;
            case 3:
                pitch = Voice.Speed.DOUBLE;
                break;
            default:
                pitch = Voice.Speed.NORMAL;
        }

        voice.setSpeechSpeed(pitch);
    }
}
