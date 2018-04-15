package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

/**
 * Created by Carlos on 3/28/2018.
 */

public class AppRepository {

    private GameDao mGameDao;
    private MappingsDao mappingsDao;
    private LiveData<List<Game>> mAllGames;
    private LiveData<List<CardMapping>> mGameCardMappings;

    AppRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        mGameDao = db.gameDao();
        mappingsDao = db.mappingDao();
        mAllGames = mGameDao.getAll();
    }

    LiveData<List<Game>> getAllGames() {
        return mAllGames;
    }

    LiveData<List<CardMapping>> getMappings(String gameName) { return mappingsDao.findByGame(gameName);
    }

    public  void  insert (CardMapping mapping) { new insertMappingAsyncTask(mappingsDao).execute(mapping);}

    public void insert (Game game) {
        new insertAsyncTask(mGameDao).execute(game);
    }

    public void delete (Game gameName) { new deleteAsyncTask(mGameDao).execute(gameName); }

    public Game findGameByName(String gameName){

        /*
        List<Game> games = mAllGames.getValue();

        if (games == null){
            Log.e("AppRepository","No games in mAllGame");
            return null;
        }

        for ( Game game : mAllGames.getValue()){
            if (game.getGameName().equals(gameName)){
                return game;
            }
        }

        */


        //AsynchReturnStruct<Game> asynchReturn = new AsynchReturnStruct<>();

        findGameAsyncTask asyncTask = new findGameAsyncTask(mGameDao);
        asyncTask.execute(gameName);

        try {
            return asyncTask.get();
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }
    }

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

    private static class insertMappingAsyncTask extends AsyncTask<CardMapping, Void, Void> {

        private MappingsDao mAsyncTaskDao;

        insertMappingAsyncTask(MappingsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CardMapping... params) {
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
            //Game game = mAsyncTaskDao.findByName(params[0]);
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class findGameAsyncTask extends AsyncTask<String, Void, Game> {
        private GameDao mAsyncTaskDao;

        findGameAsyncTask(GameDao dao) {

            mAsyncTaskDao = dao;
        }

        @Override
        protected Game doInBackground(final String... params) {
            Game game = mAsyncTaskDao.findByName(params[0]);

            Log.d("AppRepository", "found game by name in dao");

            return game;
        }

    }

    private static class AsynchReturnStruct<T>{
        public boolean ready;
        public T value;

        public AsynchReturnStruct(){
            ready = false;
            value = null;
        }
    }
}