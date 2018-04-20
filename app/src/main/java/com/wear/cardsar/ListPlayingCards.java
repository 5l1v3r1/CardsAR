package com.wear.cardsar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class ListPlayingCards extends AppCompatActivity {

    private final String TAG = "GameSetup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_playing_cards);

        Intent intent = getIntent();
        final String gameName = intent.getStringExtra(GameListAdapter.MESSAGE_GAME_NAME);
        if (gameName.equals("")){
            Log.e(TAG, "Found no game name in intent!");

            finish();
        }

        AppRepository repo = new AppRepository(getApplication());
        Game game = repo.findGameByName(gameName);
        PlayingCardMappings mappings = new PlayingCardMappings(game, repo);

        // Populate UI elements
        TextView mappingsListView = findViewById(R.id.playingCardsList);

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 52; i++){
            CardMapping mapping = mappings.lookupPlayingCard(i);
            if (mapping != null){
                builder.append(PlayingCardMappings.getPlayingCardName(i));
                builder.append(" to ");
                builder.append(mapping.getMappingName());
                builder.append('\n');
            }
        }
        mappingsListView.setText(builder.toString());
    }
}
