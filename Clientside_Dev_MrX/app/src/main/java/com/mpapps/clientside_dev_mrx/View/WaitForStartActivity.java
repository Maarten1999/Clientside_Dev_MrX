package com.mpapps.clientside_dev_mrx.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mpapps.clientside_dev_mrx.R;

public class WaitForStartActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_start);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
        String gamecode = sharedPref.getString("GameCode", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("games").child(gamecode).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child("started").getValue(boolean.class)){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
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
