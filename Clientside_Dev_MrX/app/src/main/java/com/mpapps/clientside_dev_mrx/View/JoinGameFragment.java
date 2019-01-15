package com.mpapps.clientside_dev_mrx.View;

import android.content.Context;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mpapps.clientside_dev_mrx.R;

public class JoinGameFragment extends DialogFragment {

    public static JoinGameFragment newInstance() {return new JoinGameFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_join_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //firebase database
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //get shared pref
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        String username = sharedPref.getString("displayname", "nonameJoin");

        EditText codeInput = view.findViewById(R.id.joingame_fragment_edittext);
        Button joinGameButton = view.findViewById(R.id.joingame_fragment_btn);
        joinGameButton.setOnClickListener(view1 -> {
            if (codeInput.getText().toString().length() < 4) {
                Toast.makeText(getContext(), "Invalid gamecode, 4 characters needed.", Toast.LENGTH_SHORT).show();
            } else {
                String gameCode = codeInput.getText().toString();
                mDatabase.child("games").child(gameCode + "").child("players").child(username).setValue(token);
                dismiss();
            }
        });
    }
}
