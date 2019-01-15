package com.mpapps.clientside_dev_mrx.View;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.mpapps.clientside_dev_mrx.Models.Constants;
import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;
import com.mpapps.clientside_dev_mrx.View.Adapters.GameListAdapter;
import com.mpapps.clientside_dev_mrx.ViewModels.StartActivityVM;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.mpapps.clientside_dev_mrx.Models.Constants.RC_CREATE_GAME;
import static com.mpapps.clientside_dev_mrx.Models.Constants.RC_START_GAME;

public class StartActivity extends AppCompatActivity implements GameListAdapter.onItemClicklistener {
    private final String TAG = "StartActivityTest";
    private StartActivityVM viewModel;
    private GameListAdapter adapter;
    private RecyclerView gamesRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        viewModel = ViewModelProviders.of(this).get(StartActivityVM.class);

        //Room insert testdata
        Map<String, Boolean> temp = new HashMap<>();
        temp.put("Maarten Penning", false);
        temp.put("Hello", true);
        temp.put("S B", false);
//        viewModel.insertGameModel(new GameModel("Test", GameMode.Hard, temp, Calendar.getInstance().getTime(), false));
//        viewModel.insertGameModel(new GameModel("Test2", GameMode.Easy, temp, Calendar.getInstance().getTime(), true));

        viewModel.getHistoryGames().observe(this, gameModels -> {
            assert gameModels != null;
            adapter.setHistoryGames(gameModels);
            adapter.notifyDataSetChanged();

            viewModel.addCurrentGame(new GameModel("CurrentGame", GameMode.Hard, temp, Calendar.getInstance().getTime(), false));
        });

        viewModel.getCurrentGames().observe(this, gameModels -> {
            assert gameModels != null;
            adapter.setCurrentGames(gameModels);
            adapter.notifyDataSetChanged();
        });

        gamesRecyclerview = findViewById(R.id.start_activity_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        gamesRecyclerview.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.gamemodes_recyclerview_divideritemdecoration));
        gamesRecyclerview.addItemDecoration(itemDecoration);

        adapter = new GameListAdapter(this, this);
        gamesRecyclerview.setAdapter(adapter);

        Button createGame = findViewById(R.id.start_activity_btn_new_game);
        createGame.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewGameActivity.class);
            startActivityForResult(intent, RC_CREATE_GAME);
        });

        Button joinGame = findViewById(R.id.start_activity_btn_join_game);
        joinGame.setOnClickListener(view -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.start_activity_fragment_placeholder);
            if(fragment == null){
                JoinGameFragment joinGameFragment = JoinGameFragment.newInstance();
                joinGameFragment.show(fragmentManager, "FRAGMENT_JOIN_GAME");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_CREATE_GAME){
            if(resultCode == Activity.RESULT_OK){
                String name = data.getStringExtra("new_game_name");
                GameMode mode = GameMode.values()[data.getIntExtra("new_game_mode", 0)];
                Map<String, Boolean> players = new HashMap<>();
                players.put("Maarten P", true);
                viewModel.addCurrentGame(new GameModel(name, mode, players, Calendar.getInstance().getTime(), false));
            }
        }else if(requestCode == RC_START_GAME){
            switch (resultCode){
                case Constants.MAPACTIVITY_GAME_LOST_CODE: {
                    GameModel gameModel = CurrentGameInstance.getInstance().getGameModel().getValue();
                    gameModel.setWon(false);
                    viewModel.insertGameModel(gameModel);
                    CurrentGameInstance.stopGame();
                    break;
                }
                case Constants.MAPACTIVITY_STOP_GAME_CODE:
                    CurrentGameInstance.stopGame();
                    break;
                case Constants.MAPACTIVITY_GAME_WON_CODE: {
                    GameModel gameModel = CurrentGameInstance.getInstance().getGameModel().getValue();
                    gameModel.setWon(true);
                    viewModel.insertGameModel(gameModel);
                    CurrentGameInstance.stopGame();
                    break;
                }
            }
        }
    }


    @Override
    public void onItemClick()
    {
        Intent intent = new Intent(this, DetailedGameActivity.class);
        startActivityForResult(intent, RC_START_GAME);
    }
}
