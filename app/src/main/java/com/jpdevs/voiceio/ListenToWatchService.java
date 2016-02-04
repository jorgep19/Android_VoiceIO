package com.jpdevs.voiceio;

import android.app.Notification;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;
import com.jpdevs.Ears;
import com.jpdevs.Ears.Guess;

import java.util.ArrayList;

/**
 * This class is the Service that listens for data coming from full details are available here:
 * http://developer.android.com/training/wearables/data-layer/data-items.html
 */
public class ListenToWatchService extends WearableListenerService {
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            // When there is an event of data being changed
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Get the event and verify the is the Guess path that we are expecting
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(Ears.GUESSES_PATH) == 0) {
                    // Create Guess objects from the data in the item that was changed
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    ArrayList<Guess> guesses = parseGuessesDataMap(dataMap);

                    showNotification(guesses);
                }
            }
        }
    }

    /**
     * Parses a DataMap with the appropriate data to create a collection of Guess objects.
     *
     * @param dataMap contains an DataMap ArrayList in under the key Ears.GUESSES_KEY with the
     *                data for watch's guesses
     * @return an ArrayList of Guess object created with the data fetch from the DataMap
     */
    private ArrayList<Guess> parseGuessesDataMap(DataMap dataMap) {
        ArrayList<DataMap> guessesData = dataMap.getDataMapArrayList(Ears.GUESSES_KEY);
        ArrayList<Guess> guesses = new ArrayList<>(guessesData.size());

        // Map the data from each data
        for(DataMap data: guessesData) {
            String meaning = data.getString(Guess.MEANING_KEY);
            float confidence = data.getFloat(Guess.CONFIDENCE_KEY);

            guesses.add(new Guess(meaning, confidence));
        }

        return guesses;
    }

    /**
     * Create a expandable notification with the guesses data and show it on the phone
     *
     * @param guesses a collection of Guess objects that will be display on the notification
     *                and send to the MainActivity if the notification is clicked.
     */
    private void showNotification(ArrayList<Guess>  guesses) {
        int notificationId = 19;
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);
        PendingIntent showIntent = createShowIntent(notificationId, guesses);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_settings_voice_white_24dp)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_subtitle))
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(showIntent)
            // Set the expanded content
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getWordsStr(guesses)))
            // add show action
            .addAction(R.drawable.ic_remove_red_eye_white_24dp, getString(R.string.show_action),
                    showIntent);

        // display the notification
        mNotificationManager.notify(notificationId, builder.build());
    }

    /**
     * Creates a String representation of a collection of Guess objects
     *
     * @param guesses a collection of Guess objects
     * @return a string representation of the form shown below:
     *         "* guess1.toString()
     *          * guess2.toString()
     *          * guess3.toString()
     *          ...."
     */
    private String getWordsStr(ArrayList<Guess> guesses) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < guesses.size(); ++i) {
            sb.append(String.format("* %s\n", guesses.get(i)));
        }

        return sb.toString();
    }

    /**
     * Creates a pending intent that will open the MainActivity to show the guesses passed in
     *
     * @param notificationId the id of the notification that will open the MainActivity
     * @param guesses a collection guesses that will be passed to the MainActivity
     * @return a pending intent that will open the MainActivity giving it guesses data to
     *         display on the list
     */
    private PendingIntent createShowIntent(int notificationId, ArrayList<Guess> guesses) {
        return MainActivity.getStartPendingIntent(this, notificationId, guesses);
    }
}