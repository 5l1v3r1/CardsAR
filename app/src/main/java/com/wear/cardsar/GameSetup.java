package com.wear.cardsar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class GameSetup extends AppCompatActivity {

    private Game mGame;

    private final String TAG = "GameSetup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        Intent intent = getIntent();
        final String gameName = intent.getStringExtra(GameListAdapter.MESSAGE_GAME_NAME);
        if (gameName.equals("")){
            Log.e(TAG, "Found no game name in intent!");

            finish();
        }

        AppRepository repo = new AppRepository(getApplication());
        mGame = repo.findGameByName(gameName);

        TextView gameNameTextView = findViewById(R.id.gameName);
        gameNameTextView.setText(mGame.getGameName());

        TextView gameDescTextView = findViewById(R.id.gameDesc);
        gameDescTextView.setText(mGame.getDescription());

        PlayingCardMappings mappings = new PlayingCardMappings(mGame, repo);
        mappings.printMappings();
        String removalInstructions = buildDeckInstructions(mappings);

        TextView instructionView = findViewById(R.id.gameInstructions);
        instructionView.setText(removalInstructions);

        Button readyB = findViewById(R.id.readyButton);

        readyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameSetup.this, CameraView.class);
                intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);

                startActivity(intent);
            }
        });

        Button cancelB = findViewById(R.id.cancelButton);

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameSetup.this, MainActivity.class));
            }
        });

        Button mappingB =  findViewById(R.id.mappingsButton);

        mappingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ListPlayingCards.class);
                intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);
                view.getContext().startActivity(intent);
            }
        });

        Log.d("Removal instructions", removalInstructions);

        Log.d("Total cards in deck", String.valueOf(mappings.getnPlayingCards()));
    }

    private String buildDeckInstructions(PlayingCardMappings mappings){

        StringBuilder instructionBuilder = new StringBuilder();

        int nDecks = mappings.getnDecks();

        instructionBuilder.append("Use ");
        instructionBuilder.append(nDecks);
        instructionBuilder.append(" deck");
        if (nDecks > 1){
            instructionBuilder.append('s');
        }
        instructionBuilder.append('\n');

        List<Integer> unusedSuits = mappings.listUnusedSuits();

        for (Integer suit : unusedSuits){
            instructionBuilder.append("Remove all ");
            instructionBuilder.append(PlayingCardMappings.getSuitName(suit));
            instructionBuilder.append('\n');
        }

        for (Pair<Integer, Integer> unusedCard : mappings.listUnusedCards()){
            // if this card's suit is totally unused, go to next
            if (unusedSuits.contains(unusedCard.first / 13)) continue;

            instructionBuilder.append("Remove ");
            instructionBuilder.append(unusedCard.second);
            instructionBuilder.append(" ");
            instructionBuilder.append(PlayingCardMappings.getPlayingCardName(unusedCard.first));
            instructionBuilder.append('\n');
        }

        return instructionBuilder.toString();

    }
}