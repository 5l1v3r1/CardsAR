package com.wear.cardsar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//addGame class handles all the functions in the add game activity
public class addGame extends AppCompatActivity {

    public static final String EXTRA_REPLY_NAME = "com.example.android.gamelistsql.REPLY_NAME";
    public static final String EXTRA_REPLY_DESCRIPTION = "com.example.android.gamelistsql.REPLY_DESCRIPTION";

    private EditText mEditGameView;
    private EditText mEditDescriptionGameView;

    private static final String TAG = "addGame";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        //ItemViews
        mEditGameView = findViewById(R.id.edit_game);
        mEditDescriptionGameView = findViewById(R.id.edit_game_description);
        final Button button = findViewById(R.id.button_save);


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                // checks new game and description is not empty
                if (TextUtils.isEmpty(mEditGameView.getText())
                        || TextUtils.isEmpty(mEditDescriptionGameView.getText())
                        ) {
                    setResult(RESULT_CANCELED, replyIntent);
                    // sends a reply with the game and game description
                } else {
                    String game = mEditGameView.getText().toString();
                    String description = mEditDescriptionGameView.getText().toString();
                    Log.v(TAG, "Game: " + game);
                    replyIntent.putExtra(EXTRA_REPLY_NAME, game);
                    replyIntent.putExtra(EXTRA_REPLY_DESCRIPTION, description);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
