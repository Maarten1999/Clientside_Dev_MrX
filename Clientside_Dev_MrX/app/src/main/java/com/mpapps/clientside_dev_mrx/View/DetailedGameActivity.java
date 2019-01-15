package com.mpapps.clientside_dev_mrx.View;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.Models.Player;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.View.Adapters.ParticipantsAdapter;
import com.mpapps.clientside_dev_mrx.ViewModels.DetailedGameVM;

import java.util.ArrayList;
import java.util.List;

public class DetailedGameActivity extends AppCompatActivity
{

    private DetailedGameVM viewModel;
    private RecyclerView participantRecyclerview;
    private ParticipantsAdapter adapter;
    private TextView modeTitle, modeDescription,gameCode;
    private boolean textViewsFilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_game);

        viewModel = ViewModelProviders.of(this).get(DetailedGameVM.class);

        participantRecyclerview = findViewById(R.id.detailed_game_activity_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        participantRecyclerview.setLayoutManager(layoutManager);

        adapter = new ParticipantsAdapter(this);
        participantRecyclerview.setAdapter(adapter);

        Button startGame = findViewById(R.id.detailed_game_activity_btn_start);
        startGame.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        });

        modeTitle = findViewById(R.id.detailed_game_activity_mode);
        modeDescription = findViewById(R.id.detailed_game_activity_mode_description);
        gameCode = findViewById(R.id.detailed_game_activity_textview_gamecode);

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
    }

    private void testDataPlayers(){
        List<Player> players = new ArrayList<>();
        players.add(new Player("MP", "MAARTEN P", false));
        players.add(new Player("SB", "Sven B", false));
        players.add(new Player("BP", "Bart P", true));
        CurrentGameInstance.getInstance().setPlayers(players);

    }

    private void fillTextViews(){
        if(viewModel.getGameModel().getValue() == null)
            return;
        String title = "", info = "";
        switch (viewModel.getGameModel().getValue().getMode()){
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
}
