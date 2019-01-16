package com.mpapps.clientside_dev_mrx.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.Models.GameState;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;

public class WaitForStartActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_start);

        String gamecode = CurrentGameInstance.getInstance().getGameCode().getValue();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("games/"+gamecode);
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int gameState = Integer.parseInt(dataSnapshot.child("gamestate").getValue(String.class));

                if (gameState >= GameState.Started.ordinal()) {
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    reference.removeEventListener(this);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                
            }
        });
    }
}
