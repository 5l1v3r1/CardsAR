package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

//Abstracts all the calls to the
public class AppRepository {

    //Private vars
    private GameDao mGameDao;
    private MappingsDao mappingsDao;
    private LiveData<List<Game>> mAllGames;

    //constructor
    AppRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        mGameDao = db.gameDao();
        mappingsDao = db.mappingDao();
        mAllGames = mGameDao.getAll();
    }

    //return all games in db
    LiveData<List<Game>> getAllGames() {
        return mAllGames;
    }

    //returns all mappings for a game
    LiveData<List<CardMapping>> getMappings(String gameName) { return mappingsDao.findByGame(gameName);
    }

    //insert a mapping to the db
    void insertMapping (CardMapping mapping) { new insertMappingAsyncTask(mappingsDao).execute(mapping);}

    //insert a game to the db
    void insertGame (Game game) {
        new insertAsyncTask(mGameDao).execute(game);
    }

    //delete a game from the game
    void deleteGame (Game game) {
        //Delete all mappings for a game
        new deleteGameMappingsAsyncTask(mappingsDao).execute(game.getGameName());

        //Delete actual game
        new deleteGameAsyncTask(mGameDao).execute(game);
    }

    // delete mapping from db
    void deleteMapping (CardMapping mapping) { new deleteMappingAsyncTask(mappingsDao).execute(mapping); }

    //returns the game with the specified name
    Game findGameByName(String gameName){

        findGameAsyncTask asyncTask = new findGameAsyncTask(mGameDao);
        asyncTask.execute(gameName);

        try {
            return asyncTask.get();
        }catch(InterruptedException e){
            e.printStackTrace();

            return null;

        }catch(ExecutionException e){
            e.printStackTrace();

            return null;
        }
    }

    //return a static (not live data) list of mappings for a game
    List<CardMapping> findStaticMappingsByName(String gameName){

        findGameMappingsAsyncTask asyncTask = new findGameMappingsAsyncTask(mappingsDao);
        asyncTask.execute(gameName);

        try {
            return asyncTask.get();
        }catch(Exception e){
            e.printStackTrace();

            return null;
        }
    }

    //insets game to the database in worker thread
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

    //insets mapping to the database in worker thread
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

    //delete game in the database in worker thread
    private static class deleteGameAsyncTask extends AsyncTask<Game, Void, Void> {
        private GameDao mAsyncTaskDao;

        deleteGameAsyncTask(GameDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Game... params) {
            //Game game = mAsyncTaskDao.findByName(params[0]);
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    //delete mapping in the database in worker thread
    private static class deleteMappingAsyncTask extends AsyncTask<CardMapping, Void, Void> {
        private MappingsDao mAsyncTaskDao;

        deleteMappingAsyncTask(MappingsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CardMapping... params) {
            //Game game = mAsyncTaskDao.findByName(params[0]);
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    //delete mappings in the database in worker thread
    private static class deleteGameMappingsAsyncTask extends AsyncTask<String, Void, Void> {
        private MappingsDao mAsyncTaskDao;

        deleteGameMappingsAsyncTask(MappingsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            //Game game = mAsyncTaskDao.findByName(params[0]);
            mAsyncTaskDao.deleteGameMappings(params[0]);
            return null;
        }
    }

    //returns game in the database in worker thread

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

    //returns all game mappings in the database in worker thread
    private static class findGameMappingsAsyncTask extends AsyncTask<String, Void, List<CardMapping>> {
        private MappingsDao mAsyncTaskDao;

        findGameMappingsAsyncTask(MappingsDao dao){
            mAsyncTaskDao = dao;
        }

        @Override
        protected List<CardMapping> doInBackground(final String... params){

            return mAsyncTaskDao.findStaticByGame(params[0]);
        }
    }
}