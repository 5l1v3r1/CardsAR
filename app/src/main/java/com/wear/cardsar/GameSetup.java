package com.wear.cardsar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        Intent intent = getIntent();
        String gameName;
        if (intent != null) {
            gameName = intent.getStringExtra(GameListAdapter.MESSAGE_GAME_NAME);
        }else{
            gameName = "None";
        }

        TextView gameNameTextView = (TextView) findViewById(R.id.gameName);
        gameNameTextView.setText(gameName);

        TextView gameDescTextView = (TextView) findViewById(R.id.gameDesc);
        gameDescTextView.setText("Description here");

        Button readyB = (Button)findViewById(R.id.readyButton);

        readyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameSetup.this, CameraView.class));
            }
        });

        Button cancelB = (Button)findViewById(R.id.cancelButton);

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameSetup.this, MainActivity.class));
            }
        });


    }
}