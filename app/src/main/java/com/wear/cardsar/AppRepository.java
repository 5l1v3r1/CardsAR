package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Carlos on 3/28/2018.
 */

public class AppRepository {

    private GameDao mGameDao;
    private LiveData<List<Game>> mAllGames;

    AppRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        mGameDao = db.gameDao();
        mAllGames = mGameDao.getAll();
    }

    LiveData<List<Game>> getAllGames() {
        return mAllGames;
    }

    public void insert (Game game) {
        new insertAsyncTask(mGameDao).execute(game);
    }

    public void delete (Game game) { new deleteAsyncTask(mGameDao).execute(game); }

    private static class insertAsyncTask extends AsyncTask<Game, Void, Void> {

        private GameDao mAsyncTaskDao;

        insertAsyncTask(GameDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Game... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Game, Void, Void> {
        private GameDao mAsyncTaskDao;

        deleteAsyncTask(GameDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Game... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}