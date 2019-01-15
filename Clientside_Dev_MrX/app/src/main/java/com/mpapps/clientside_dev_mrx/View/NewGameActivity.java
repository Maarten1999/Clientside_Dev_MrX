package com.mpapps.clientside_dev_mrx.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.Models.GameLevel;
import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.View.Adapters.GameModesAdapter;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewGameActivity extends AppCompatActivity {
    private GameModesAdapter adapter;
    private RecyclerView gameModeRecyclerview;
    private DatabaseReference mDatabase;
    private ArrayList<String> gameCodeList;
    private String gameCode;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        //get sharedpref
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String username = sharedPref.getString("displayname", "nonameHost");

        //firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //recyclerview
        gameModeRecyclerview = findViewById(R.id.new_game_activity_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        gameModeRecyclerview.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.gamemodes_recyclerview_divideritemdecoration));
        gameModeRecyclerview.addItemDecoration(itemDecoration);

        adapter = new GameModesAdapter(this, createGameModes());
        gameModeRecyclerview.setAdapter(adapter);

        //editText
        EditText editText = findViewById(R.id.new_game_activity_edittext_name);
        editText.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Clear focus here from edittext
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                editText.clearFocus();
            }
            return false;
        });

        //Button
        Button createButton = findViewById(R.id.new_game_activity_btn_create);
        createButton.setOnClickListener(view -> {

            if (editText.getText().toString().length() == 0) {
                Toast.makeText(this, "Enter a name for the game", Toast.LENGTH_SHORT).show();
            } else if (editText.getText().toString().length() < 4) {
                Toast.makeText(this, "Name is too short, use at least 4 characters", Toast.LENGTH_SHORT).show();
            } else if (adapter.getLastSelectedPos() == -1) {
                Toast.makeText(this, "Select a game mode", Toast.LENGTH_SHORT).show();
            } else {

//                new MyAsyncTask.execute();//

                CurrentGameInstance currentGameInstance = CurrentGameInstance.getInstance();
                currentGameInstance.setGameCode(gameCode + "");

                Intent returnIntent = new Intent();
                returnIntent.putExtra("new_game_name", editText.getText().toString());
                returnIntent.putExtra("new_game_mode", adapter.getLastSelectedPos());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private List<GameLevel> createGameModes() {
        List<GameLevel> gameLevels = new ArrayList<>();
        gameLevels.add(new GameLevel(GameMode.Easy, 15, 2));
        gameLevels.add(new GameLevel(GameMode.Normal, 60, 5));
        gameLevels.add(new GameLevel(GameMode.Hard, 120, 10));
        gameLevels.add(new GameLevel(GameMode.MisterX, 240, 10));

        return gameLevels;
    }


    private String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 4;
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) ASCIIcharacter();
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private int ASCIIcharacter() {
        int character = (int) Math.round(Math.random() * 42 + 48);
        if (character < 58 || character > 64)
            return character;
        else
            return ASCIIcharacter();
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DatabaseReference gameCodes = mDatabase.child("gamecodes");
            gameCodes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        gameCodeList.add(ds.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for (String s : gameCodeList) {
//                if (gamecode == s){

            }
        }
    }
}