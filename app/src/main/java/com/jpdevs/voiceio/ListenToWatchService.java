package com.jpdevs.voiceio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.jpdevs.Ears;
import com.jpdevs.Ears.Guess;

import java.util.ArrayList;

public class ListenToWatchService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(Ears.GUESSES_PATH) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    ArrayList<Guess> words = parseGuessesDataMap(dataMap);
                    showNotification(words);
                }
            }
        }
    }

    private ArrayList<Guess> parseGuessesDataMap(DataMap dataMap) {
        ArrayList<DataMap> guessesData = dataMap.getDataMapArrayList(Ears.GUESSES_KEY);
        ArrayList<Guess> guesses = new ArrayList<>(guessesData.size());

        for(DataMap data: guessesData) {
            String meaning = data.getString(Guess.MEANING_KEY);
            float confidence = data.getFloat(Guess.CONFIDENCE_KEY);

            guesses.add(new Guess(meaning, confidence));
        }

        return guesses;
    }

    private void showNotification(ArrayList<Guess>  words) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_settings_voice_white_24dp)
                        .setContentTitle("Your watch told me you said:")
                        .setContentText(":)")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(getWordsStr(words)))
                        .addAction(R.drawable.ic_record_voice_over_black_24dp,
                                "Show", createShowNotification(words));



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(19, builder.build());
    }

    private String getWordsStr(ArrayList<Guess> guesses) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < guesses.size(); ++i) {
            sb.append(String.format("* %s\n", guesses.get(i)));
        }

        return sb.toString();
    }

    private PendingIntent createShowNotification(ArrayList<Guess>  guesses) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putParcelableArrayListExtra("words", guesses);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}