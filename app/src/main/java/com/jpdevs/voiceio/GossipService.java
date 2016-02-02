package com.jpdevs.voiceio;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class GossipService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //check if the message received was on the path we are expecting
        if (messageEvent.getPath().contains("/input/")) {
            String message = new String(messageEvent.getData());
            Log.v("myTag", "Message received on phone is: " + message);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }
}