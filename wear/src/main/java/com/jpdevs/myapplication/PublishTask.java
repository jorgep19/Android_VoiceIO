package com.jpdevs.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
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
        if(syncWords(words)) {
//            for (Node node : nodes.getNodes()) {
//                MessageApi.SendMessageResult result =
//                    Wearable.MessageApi
//                        .sendMessage(gClient, node.getId(), "/input/length", intToByteArray(words.length))
//                        .await();
//
//                if (result.getStatus().isSuccess()) {
//                    Log.i("VOICE", "Sent words collection length successfully to " + node.getDisplayName());
//                }
//            }
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

    private static byte[] intToByteArray(int a)
    {
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    private boolean syncWords(String... words) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/words");
        putDataMapReq.getDataMap().putStringArray("words", words);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(gClient,  putDataReq).await();

        return result.getStatus().isSuccess();
    }
}