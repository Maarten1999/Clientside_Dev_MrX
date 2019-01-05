package com.mpapps.clientside_dev_mrx.Services.Room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.mpapps.clientside_dev_mrx.Models.GameModel;

import java.util.List;

public class GameModelRepository
{
    private GameModelDao gameModelDao;
    private LiveData<List<GameModel>> gameModels;

    public GameModelRepository(Application application)
    {
        DatabaseService database = DatabaseService.getInstance(application);
        gameModelDao = database.gameModelDao();
        gameModels = gameModelDao.loadAllGameModels();
    }

    public LiveData<List<GameModel>> getAllGameModels()
    {
        return gameModels;
    }

    public void insertGameModel(GameModel model){
        new InsertGameModelAsyncTask(gameModelDao).execute(model);
    }

    private static class InsertGameModelAsyncTask extends AsyncTask<GameModel, Void, Void>{

        private GameModelDao gameModelDao;
        private InsertGameModelAsyncTask(GameModelDao dao){this.gameModelDao = dao; }

        @Override
        protected Void doInBackground(GameModel... gameModels)
        {
            gameModelDao.insertGameModel(gameModels[0]);
            return null;
        }
    }
}
