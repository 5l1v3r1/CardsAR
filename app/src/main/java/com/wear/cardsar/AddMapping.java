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
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class AddMapping extends AppCompatActivity {

    //Private vars
    private MappingViewModel mMappingViewModel;
    private static final String TAG = "AddMappingActivity";
    private static String gameName;

    //Public vars
    public static final int NEW_MAPPING_ACTIVITY_REQUEST_CODE = 1;

    //Function initializes activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mapping);

        // starts addGame activity on click

        FloatingActionButton addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddMapping.this, SpecifyNewMapping.class);
                intent.putExtra(GameListAdapter.MESSAGE_GAME_NAME, gameName);
                startActivityForResult(intent, NEW_MAPPING_ACTIVITY_REQUEST_CODE);
            }
        });


        gameName = getIntent().getStringExtra(GameListAdapter.MESSAGE_GAME_NAME);
        //Log.v(TAG, "game name: " + gameName);

        //app bar name
        getSupportActionBar().setTitle(gameName);

        // init model
        mMappingViewModel = ViewModelProviders.of(this).get(MappingViewModel.class);

        // gets and list all games in UI
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MappingsListAdapter adapter = new MappingsListAdapter(this, mMappingViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMappingViewModel.getAllMappings(gameName).observe(this, new Observer<List<CardMapping>>() {
            @Override
            public void onChanged(@Nullable final List<CardMapping> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setmMappings(words);
            }
        });

        // inits back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(view.getContext(), MainActivity.class);
                backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(backIntent);
            }
        });
    }

    //function handles all activity results sent to activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handles addGame activity result (both success and fail)
        if (requestCode == NEW_MAPPING_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            int quantity = Integer.parseInt(data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_QUANTITY));
            String mUri= null;
            if(data.getStringExtra("pic").equals("true")){
                //Uri mUri = Uri.parse(data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_URI));
                mUri = data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_URI);

            }else {
                Log.v(TAG, "no bitmap");
            }

            CardMapping mapping = new CardMapping(data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_NAME), gameName
                    , data.getStringExtra(SpecifyNewMapping.EXTRA_REPLY_MAPPING_DESCRIPTION)
                    , quantity, mUri);
            mMappingViewModel.insert(mapping);
        } else {

        }
    }

}
