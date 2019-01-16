package com.mpapps.clientside_dev_mrx.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.Models.Player;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.View.Adapters.ParticipantsAdapter;
import com.mpapps.clientside_dev_mrx.ViewModels.DetailedGameVM;
import com.mpapps.clientside_dev_mrx.Volley.Requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedGameActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DetailedGameVM viewModel;
    private RecyclerView participantRecyclerview;
    private ParticipantsAdapter adapter;
    private TextView modeTitle, modeDescription, gameCode;
    private boolean textViewsFilled = false;
    String gamecodeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_game);


        //get sharedpref
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String username = sharedPref.getString("displayname", "nonameHost");



        //firebase database

        mDatabase = FirebaseDatabase.getInstance().getReference();
        gamecodeString = CurrentGameInstance.getInstance().getGameCode().getValue();
        DatabaseReference playerReference = mDatabase.child("games").child(gamecodeString).child("players");

        ArrayList<String> strings = new ArrayList<>();
        playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    strings.add(nameSnapshot.getValue(String.class));

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("START_GAME_DB_REFERENCE", "Failed to read value.", error.toException());
            }
        });

        viewModel = ViewModelProviders.of(this).get(DetailedGameVM.class);

        participantRecyclerview = findViewById(R.id.detailed_game_activity_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        participantRecyclerview.setLayoutManager(layoutManager);

        adapter = new ParticipantsAdapter(this);
        participantRecyclerview.setAdapter(adapter);

        Button startGame = findViewById(R.id.detailed_game_activity_btn_start);
        startGame.setOnClickListener(view -> {
            //TODO Uncomment check below for the presentation
//            if(strings.size() <= 1){
//                Toast.makeText(getApplicationContext(), "You must have at least 2 players to start the game", Toast.LENGTH_SHORT).show();
//            }else {
            Requests.createMessagingGroup(username, strings, this);

            Intent intent = new Intent(this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
            //}
        });

        modeTitle = findViewById(R.id.detailed_game_activity_mode);
        modeDescription = findViewById(R.id.detailed_game_activity_mode_description);
        gameCode = findViewById(R.id.detailed_game_activity_textview_gamecode);
        gameCode.setText(gamecodeString);

        viewModel.getGameCode().observe(this, s -> {
            assert s != null;
            gameCode.setText(s);
        });

        viewModel.getGameModel().observe(this, gameModel -> {
            assert gameModel != null;
            adapter.setPlayers(gameModel.getPlayers());
            adapter.notifyDataSetChanged();
            if (!textViewsFilled)
                fillTextViews();
        });

        testDataPlayers();
        playerListener();
    }

    private void testDataPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("MP", "MAARTEN P", false));
        players.add(new Player("SB", "Sven B", false));
        players.add(new Player("BP", "Bart P", true));
        CurrentGameInstance.getInstance().setPlayers(players);

    }

    private void fillTextViews() {
        if (viewModel.getGameModel().getValue() == null)
            return;
        String title = "", info = "";
        switch (viewModel.getGameModel().getValue().getMode()) {
            case Easy:
                title = getResources().getString(R.string.gamemode_easy_title);
                info = getResources().getString(R.string.gamemode_easy_description);
                break;
            case Normal:
                title = getResources().getString(R.string.gamemode_normal_title);
                info = getResources().getString(R.string.gamemode_normal_description);
                break;
            case Hard:
                title = getResources().getString(R.string.gamemode_hard_title);
                info = getResources().getString(R.string.gamemode_hard_description);
                break;
            case MisterX:
                title = getResources().getString(R.string.gamemode_misterx_title);
                info = getResources().getString(R.string.gamemode_misterx_description);
                break;
        }

        modeTitle.setText(title);
        modeDescription.setText(info);
        textViewsFilled = true;
    }

    private void playerListener() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("games").child(gamecodeString);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String misterX = dataSnapshot.child("misterX").getValue(String.class);
                GameModel gameModel = viewModel.getGameModel().getValue();
                Map<String, Boolean> players = gameModel.getPlayers();
                for (DataSnapshot ds : dataSnapshot.child("players").getChildren()) {
                    if (misterX.equals(ds.getKey()))
                        players.put(ds.getKey(), true);
                    else
                        players.put(ds.getKey(), false);
                }
                viewModel.getGameModel().postValue(gameModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
