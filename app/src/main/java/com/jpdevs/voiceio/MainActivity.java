package com.jpdevs.voiceio;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

        ears = new Ears(VOICE_INPUT_REQ);
        voice = new Voice(this);

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
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
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

        Intent startIntent = getIntent();
        if(startIntent != null) {
            int notificationId = startIntent.getIntExtra(NOTIFICATION_ID_KEY, 0);
            ArrayList<Guess> guesses = startIntent.getParcelableArrayListExtra(GUESSES_DATA_KEY);

            if(notificationId != 0) {
                NotificationManagerCompat notManager = NotificationManagerCompat.from(this);
                notManager.cancel(notificationId);
            }

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
        String language;
        switch (item.getItemId()) {
            case R.id.spanish_item:
                language = getString(R.string.spanish);
                ears.setToSpanish();
                break;
            case R.id.english_item:
                language = getString(R.string.english);
                ears.setToEnglish();
                break;
            case R.id.french_item:
                language = getString(R.string.french);
                ears.setToFrench();
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
        if(ears.shouldProcessSound(requestCode, resultCode, data)) {
            adapter.setGuesses(ears.processSound(data));
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        Guess[] guesses = adapter.getGuesses();
        ArrayList<Guess> guessesList = new ArrayList<>(Arrays.asList(guesses));
        outState.putParcelableArrayList(GUESSES_STATE, guessesList);
    }

    @Override
    public void onDestroy() {
        voice.onDestroy();
        super.onDestroy();
    }
}
