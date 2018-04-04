package com.wear.cardsar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GameSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

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