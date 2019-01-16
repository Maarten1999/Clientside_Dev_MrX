package com.mpapps.clientside_dev_mrx.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.mpapps.clientside_dev_mrx.Models.Constants.RC_START_GAME;

public class JoinGameFragment extends DialogFragment {

    public static JoinGameFragment newInstance() {
        return new JoinGameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("games");

        //get shared pref
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String username = sharedPref.getString("displayname", "nonameJoin");

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("countdown_timer_finish", true).apply();
        editor.putString("data", "").apply();

        EditText codeInput = view.findViewById(R.id.joingame_fragment_edittext);
        Button joinGameButton = view.findViewById(R.id.joingame_fragment_btn);
        joinGameButton.setOnClickListener(view1 -> {
            if (codeInput.getText().toString().length() < 4) {
                Toast.makeText(getContext(), "Invalid gamecode, 4 characters needed.", Toast.LENGTH_SHORT).show();
            } else {
                String gameCode = codeInput.getText().toString();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(gameCode).exists()) {
                            mDatabase.child(gameCode).child("players").child(username).setValue(token);
                            String misterX = dataSnapshot.child(gameCode).child("misterX").getValue(String.class);
                            int gamemode = Integer.parseInt(dataSnapshot.child(gameCode).child("gamemode").getValue(String.class));
                            String gamename = dataSnapshot.child(gameCode).child("gamename").getValue(String.class);
                            Map<String,Boolean> players = new HashMap<>();
                            for (DataSnapshot ds : dataSnapshot.child(gameCode).child("players").getChildren()){
                                if (misterX.equals(ds.getKey()))
                                    players.put(ds.getKey(), true);
                                else
                                    players.put(ds.getKey(), false);
                            }

                            GameModel gameModel = new GameModel(gamename,GameMode.values()[gamemode],players,Calendar.getInstance().getTime(),false);
                            CurrentGameInstance.initialize(gameModel,gameCode);
                            setCancelable(false);
                            Intent intent = new Intent(getContext(), WaitForStartActivity.class);
                            startActivityForResult(intent, RC_START_GAME);
                            dismiss();
                        } else
                            Toast.makeText(getContext(), "GameCode not found.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
