package com.wear.cardsar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class addGame extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private EditText mEditGameView;

    private static final String TAG = "addGame";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);
        mEditGameView = findViewById(R.id.edit_game);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditGameView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String game = mEditGameView.getText().toString();
                    Log.v(TAG, "Game: " + game);
                    replyIntent.putExtra(EXTRA_REPLY, game);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}
