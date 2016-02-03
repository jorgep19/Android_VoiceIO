package com.jpdevs.myapplication;

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

public class PublishTask extends AsyncTask<Guess, Void, Boolean> {
    private GoogleApiClient gClient;

    public PublishTask(GoogleApiClient gClient){
        this.gClient = gClient;
    }

    @Override
    public Boolean doInBackground(Guess... guesses) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/guesses");
        putDataMapReq.getDataMap().putDataMapArrayList("guesses", putGuessesInDataMapArray(guesses));
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(gClient,  putDataReq).await();

        return result.getStatus().isSuccess();
    }

    private ArrayList<DataMap> putGuessesInDataMapArray(Guess[] guesses) {
        ArrayList<DataMap> mapList = new ArrayList<>();

        for(int i = 0; i < guesses.length; ++i) {
            DataMap guessEntry = new DataMap();
            guessEntry.putString(Ears.MEANING_KEY, guesses[i].getMeaning());
            guessEntry.putFloat(Ears.CONFIDENCE_KEY, guesses[i].getConfidence())
            ;
            mapList.add(guessEntry);
        }

        return mapList;
    }
}