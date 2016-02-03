package com.jpdevs.voiceio;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jpdevs.Ears;
import com.jpdevs.Voice;

public class GuessesAdapter extends RecyclerView.Adapter<GuessesAdapter.ViewHolder> {
    private Ears.Guess[] guesses;
    private Voice voice;

    public GuessesAdapter(Voice voice) {
        guesses = new Ears.Guess[0];
        this.voice = voice;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.guess_item_layout, parent, false);

        return new ViewHolder(rootView, voice);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setItem(guesses[position]);
    }

    @Override
    public int getItemCount() {
        return guesses.length;
    }

    public void setGuesses(Ears.Guess[] guesses) {
        this.guesses = guesses;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        private Voice voice;
        private String message;

        public TextView titleText;
        public TextView subtitleText;

        public ViewHolder(View root, Voice voice) {
            super(root);

            this.voice = voice;

            titleText = (TextView) root.findViewById(R.id.meaning_text);
            subtitleText = (TextView) root.findViewById(R.id.confidence_text);
            root.findViewById(R.id.readBtn).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            voice.say(message, false);
        }

        public void setItem(Ears.Guess guess) {
            String confidenceStr = String.format("%.2f", (guess.confidence*100));
            message = String.format("I'm %s percent sure you said %s", confidenceStr, guess.meaning);

            titleText.setText(guess.meaning);
            subtitleText.setText(confidenceStr);
        }
    }
}