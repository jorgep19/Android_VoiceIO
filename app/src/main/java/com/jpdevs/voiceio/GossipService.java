package com.jpdevs.voiceio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class GossipService extends WearableListenerService {
    private final String TAG = GossipService.class.getName();

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/words") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    String[] words = dataMap.getStringArray("words");
                    if(words != null) {
                        showNotification(words);
                    } else {
                        Log.e(TAG, "There was not value for the Key 1`words` in the DataMap received");
                    }
                }
            }
        }
    }

    private void showNotification(String[] words) {
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

    private String getWordsStr(String[] words) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < words.length; ++i) {
            sb.append(String.format("* %s\n", words[i]));
        }

        return sb.toString();
    }

    private PendingIntent createShowNotification(String[] words) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("words", words);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}