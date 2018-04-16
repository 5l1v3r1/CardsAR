package com.wear.cardsar;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class AddMapping extends AppCompatActivity {

    private MappingViewModel mMappingViewModel;
    public static final int NEW_MAPPING_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "AddMappingActivity";
    private static String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mapping);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMapping.this, SpecifyNewMapping.class);
                intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);
                startActivityForResult(intent, NEW_MAPPING_ACTIVITY_REQUEST_CODE);
            }
        });

        gameName = getIntent().getStringExtra(GameListAdapter.MESSAGE_GAME_NAME);
        Log.v(TAG, "game name: " + gameName);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MappingsListAdapter adapter = new MappingsListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMappingViewModel = ViewModelProviders.of(this).get(MappingViewModel.class);
        mMappingViewModel.getAllMappings(gameName).observe(this, new Observer<List<CardMapping>>() {
            @Override
            public void onChanged(@Nullable final List<CardMapping> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setmMappings(words);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_MAPPING_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.v(TAG, "New mapping is:");
            Log.v(TAG, data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_NAME));
            Log.v(TAG, gameName);
            Log.v(TAG, data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_CARD_MAPPING));

            CardMapping mapping = new CardMapping(data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_NAME),
                                                  gameName,
                                                  Integer.parseInt(data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_CARD_MAPPING)));
            mMappingViewModel.insert(mapping);
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        }
    }
}
