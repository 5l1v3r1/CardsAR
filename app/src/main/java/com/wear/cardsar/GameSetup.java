package com.wear.cardsar;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        TextView gameNameTextView = (TextView) findViewById(R.id.gameName);
        gameNameTextView.setText(mGame.getGameName());

        TextView gameDescTextView = (TextView) findViewById(R.id.gameDesc);
        gameDescTextView.setText(mGame.getDescription()); // TO-DO: Get description from mGame

        Button readyB = (Button)findViewById(R.id.readyButton);

        readyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameSetup.this, CameraView.class);
                intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);

                startActivity(intent);
            }
        });

        Button cancelB = (Button)findViewById(R.id.cancelButton);

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameSetup.this, MainActivity.class));
            }
        });

        PlayingCardMappings mappings = new PlayingCardMappings(mGame, repo);
        String removalInstructions = buildDeckInstructions(mappings);

        Log.d("Removal instructions", removalInstructions);

        Log.d("Total cards in deck", String.valueOf(mappings.getnPlayingCards()));
    }

    private String buildDeckInstructions(PlayingCardMappings mappings){

        StringBuilder instructionBuilder = new StringBuilder();

        int nDecks = (mappings.getnPlayingCards() / 52) + 1;

        instructionBuilder.append("Use ");
        instructionBuilder.append(nDecks);
        instructionBuilder.append(" deck");
        if (nDecks > 1){
            instructionBuilder.append("s");
        }

        for (Pair<Integer, Integer> unusedCard : mappings.listUnusedCards()){
            instructionBuilder.append("Remove ");
            instructionBuilder.append(unusedCard.second);
            instructionBuilder.append(" ");
            instructionBuilder.append(PlayingCardMappings.getPlayingCardName(unusedCard.first));
            if (unusedCard.second > 1) {
                instructionBuilder.append("s");
            }
            instructionBuilder.append("\n");
        }

        return instructionBuilder.toString();

    }
}