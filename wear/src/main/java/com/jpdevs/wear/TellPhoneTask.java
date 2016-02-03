package com.jpdevs.wear;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.jpdevs.Ears;
import com.jpdevs.Ears.Guess;

import java.util.ArrayList;

public class TellPhoneTask extends AsyncTask<Guess, Void, Boolean> {
    private GoogleApiClient gClient;

    public TellPhoneTask(GoogleApiClient gClient){
        this.gClient = gClient;
    }

    /**
     * This method run on an background thread and published the guesses data so that the phone
     * (or other watches) can access it.
     *
     * @param guesses a collection of guesses for the user voice input.
     * @return true if the guesses were send successfully according Wearable Data API, false otherwise.
     */
    @Override
    public Boolean doInBackground(Guess... guesses) {
        // Prepare the publication request by placing the guesses data into a DataMap
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(Ears.GUESSES_PATH);
        putDataMapReq.getDataMap().putDataMapArrayList(Ears.GUESSES_KEY, putGuessesInDataMapArray(guesses));
        PutDataRequest putRequest = putDataMapReq.asPutDataRequest();

        // Actually send the request so other connected devices can use the input data
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(gClient, putRequest).await();

        return result.getStatus().isSuccess();
    }

    /**
     * Helper method to convert an array of Guess object in to an ArrayList of DataMap objects
     * which can be send to other devices through the Wearable Data API
     *
     * @param guesses a collection of guesses for the user voice input.
     * @return an ArrayList of DataMap objects containing the guesses data.
     */
    private ArrayList<DataMap> putGuessesInDataMapArray(Guess[] guesses) {
        ArrayList<DataMap> mapList = new ArrayList<>(guesses.length);

        // Foreach Guess objects place each of the attributes into a DataMap
        for(int i = 0; i < guesses.length; ++i) {
            DataMap guessEntry = new DataMap();
            guessEntry.putString(Guess.MEANING_KEY, guesses[i].meaning);
            guessEntry.putFloat(Guess.CONFIDENCE_KEY, guesses[i].confidence);

            mapList.add(guessEntry);
        }

        return mapList;
    }
}