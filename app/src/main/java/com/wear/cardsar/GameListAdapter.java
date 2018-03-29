package com.wear.cardsar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Carlos on 3/28/2018.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView GameItemView;

        private GameViewHolder(View itemView) {
            super(itemView);
            GameItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Game> mGames; // Cached copy of words

    GameListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        if (mGames != null) {
            Game current = mGames.get(position);
            holder.GameItemView.setText(current.getGameName());
        } else {
            // Covers the case of data not being ready yet.
            holder.GameItemView.setText("No Word");
        }
    }

    void setGames(List<Game> games){
        mGames = games;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mGames != null)
            return mGames.size();
        else return 0;
    }
}