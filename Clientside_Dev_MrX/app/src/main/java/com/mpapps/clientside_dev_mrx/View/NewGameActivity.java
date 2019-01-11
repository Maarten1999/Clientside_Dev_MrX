package com.mpapps.clientside_dev_mrx.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mpapps.clientside_dev_mrx.Models.GameLevel;
import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.View.Adapters.GameModesAdapter;

import java.util.ArrayList;
import java.util.List;

public class NewGameActivity extends AppCompatActivity
{

    private GameModesAdapter adapter;
    private RecyclerView gameModeRecyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

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
            if(actionId==EditorInfo.IME_ACTION_DONE){
                //Clear focus here from edittext
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                editText.clearFocus();
            }
            return false;
        });

        //Button
        Button createButton = findViewById(R.id.new_game_activity_btn_create);
        createButton.setOnClickListener(view -> {
            if(editText.getText().toString().length() == 0){
                Toast.makeText(this, "Enter a name for the game", Toast.LENGTH_SHORT).show();
            }else if(editText.getText().toString().length() < 4){
                Toast.makeText(this, "Name is too short, use at least 4 characters", Toast.LENGTH_SHORT).show();
            }else if(adapter.getLastSelectedPos() == -1){
                Toast.makeText(this, "Select a game mode", Toast.LENGTH_SHORT).show();
            }else {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("new_game_name", editText.getText().toString());
                returnIntent.putExtra("new_game_mode", adapter.getLastSelectedPos());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    private List<GameLevel> createGameModes(){
        List<GameLevel> gameLevels = new ArrayList<>();
        gameLevels.add(new GameLevel(GameMode.Easy, 15, 2));
        gameLevels.add(new GameLevel(GameMode.Normal, 60, 5));
        gameLevels.add(new GameLevel(GameMode.Hard, 120, 10));
        gameLevels.add(new GameLevel(GameMode.MisterX, 240, 10));

        return gameLevels;
    }
}
