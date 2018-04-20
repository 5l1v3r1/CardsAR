package com.wear.cardsar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// GameListAdapter will use a recyclable xml to display all the items in a list of games
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    //public vars
    public static final String MESSAGE_GAME_NAME = "com.wear.cardsar.mapping";

    // class used as the holder for one game
    class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView GameItemView;
        private final TextView GameDescriptionView;
        private final TextView GameCardQuantityView;
        private Game mGame;

        //constructor
        private GameViewHolder(final View itemView) {
            super(itemView);
            //finds all the fields to display things in xml
            GameItemView = itemView.findViewById(R.id.textView);
            GameDescriptionView = itemView.findViewById(R.id.descView);
            GameCardQuantityView = itemView.findViewById(R.id.intView);

            // button on clicks routes to play game activity
            Button playButton = itemView.findViewById(R.id.play_game_button);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Launch GameSetup with gameName argument

                    String gameName = mGame.getGameName();

                    Log.d("GameListAdapter", "play game: " + gameName);
                    Intent intent = new Intent(itemView.getContext(), GameSetup.class);
                    intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);
                    itemView.getContext().startActivity(intent);

                }
            });

            //button on click routes to edit edit game mappings activity
            Button editButton = itemView.findViewById(R.id.edit_game_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String gameName = mGame.getGameName();

                    // Launch GameSetup with gameName as argument
                    Log.d("GameListAdapter", "edit game: " + gameName);

                    Intent intent = new Intent(itemView.getContext(), AddMapping.class);
                    intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);
                    itemView.getContext().startActivity(intent);
                }
            });

            //button on click deletes a game
            Button deleteButton = itemView.findViewById(R.id.delete_game_button);
            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    mModel.delete(mGame);
                }
            });

        }

        //current games info
        public void setGame(Game game){
            mGame = game;
        }
    }

    //private class variables
    private final LayoutInflater mInflater;
    private List<Game> mGames; // Cached copy of words
    private GameViewModel mModel;

    //places all game item in view
    GameListAdapter(Context context, GameViewModel model) {
        mInflater = LayoutInflater.from(context);
        mModel = model;
    }

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
            String truncatedDescription = current.getDescription();
            if (truncatedDescription.length() > 43){
                truncatedDescription = truncatedDescription.substring(0, 40);
                truncatedDescription += "...";
            }
            holder.GameDescriptionView.setText(truncatedDescription);

            holder.GameCardQuantityView.setText(String.valueOf(mModel.getnMappings(current)) + " mappings");

            holder.setGame(current);
        } else {
            // Covers the case of data not being ready yet.
            holder.GameItemView.setText(R.string.no_game);
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