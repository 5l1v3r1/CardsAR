package com.wear.cardsar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SpecifyNewMapping extends AppCompatActivity {

    public static final String EXTRA_REPLY_MAPPING_NAME = "com.example.android.wordlistsql.NAME";
    public static final String EXTRA_REPLY_CARD_MAPPING = "com.example.android.wordlistsql.MAPPING";

    private static final String TAG = "SNewMappingActivity";

    private EditText mEditMappingNameTextView;
    private EditText mEditCardMappingTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specify_new_mapping);
        mEditMappingNameTextView = findViewById(R.id.edit_mapping_name);
        mEditCardMappingTextView = findViewById(R.id.edit_card_mapping);

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(mEditMappingNameTextView.getText()) //TODO check that Card Mapping is an int
                        || TextUtils.isEmpty(mEditCardMappingTextView.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                    Log.v(TAG, "No mapping name!");
                    //TODO: make toast that lets user know what what went wrong

                } else {
                    String mappingName = mEditMappingNameTextView.getText().toString();
                    String cardMapping = mEditCardMappingTextView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY_MAPPING_NAME, mappingName);
                    replyIntent.putExtra(EXTRA_REPLY_CARD_MAPPING, cardMapping);

                    setResult(RESULT_OK, replyIntent);
                    Log.v(TAG, "name:");
                    Log.v(TAG, mappingName);
                    Log.v(TAG, cardMapping);
                }
                finish();
            }
        });
    }
}