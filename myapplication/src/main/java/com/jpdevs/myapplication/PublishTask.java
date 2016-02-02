package com.jpdevs.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class PublishTask extends AsyncTask<String, Void, Void> {
    private GoogleApiClient gClient;

    public PublishTask(GoogleApiClient gClient){
        this.gClient = gClient;
    }

    @Override
    public Void doInBackground(String... words) {
        Log.i("VOICE", "In the async task");
//            byte[][] data = convertToBytes(words);
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(gClient).await();

        Log.i("VOICE", "Founds this many nodes: " + nodes.getNodes().size());
        for (Node node : nodes.getNodes()) {
            for(int i = 0; i < words.length; ++i) {
                MessageApi.SendMessageResult result =
                        Wearable.MessageApi.sendMessage(gClient, node.getId(), "/input/length", convertToBytes(words.length)).await();
                Log.i("VOICE", "Sent words collection length successfully to " + node.getDisplayName());

                if(result.getStatus().isSuccess()) {
                    result =
                            Wearable.MessageApi.sendMessage(gClient, node.getId(), "/input/" + i, words[i].getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.i("VOICE", "Message: {" + words[i] + "} sent to: " + node.getDisplayName());
                    } else {
                        Log.i("VOICE", "ERROR: failed to send Message");
                    }
                } else {
                    Log.i("VOICE", "ERROR: failed to send words collection length");
                }
            }
        }

        return null;
    }

    protected void onPostExecute(Void... result) {
        Log.i("VOICE", "Done sending words");
    }

//        private byte[][] convertToBytes(String[] strings) {
//            byte[][] data = new byte[strings.length][];
//            for (int i = 0; i < strings.length; i++) {
//                String string = strings[i];
//                data[i] = string.getBytes(Charset.defaultCharset()); // you can chose charset
//            }
//            return data;
//        }

    public byte[] convertToBytes(int n)
    {
        byte[] ret = new byte[4];
        ret[0] = (byte) (n & 0xFF);
        ret[1] = (byte) ((n >> 8) & 0xFF);
        ret[2] = (byte) ((n >> 16) & 0xFF);
        ret[3] = (byte) ((n >> 24) & 0xFF);
        return ret;
    }
}