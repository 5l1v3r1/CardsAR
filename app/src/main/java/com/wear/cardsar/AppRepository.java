package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AppRepository implements LifecycleOwner{

    private GameDao mGameDao;
    private MappingsDao mappingsDao;
    private LiveData<List<Game>> mAllGames;
    private LiveData<List<CardMapping>> mGameCardMappings;
    private Lifecycle lifecycle = null;

    AppRepository(Application application) {
        AppDatabase db = AppDatabase.getAppDatabase(application);
        mGameDao = db.gameDao();
        mappingsDao = db.mappingDao();
        mAllGames = mGameDao.getAll();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {

        if (lifecycle == null){
            lifecycle = new Lifecycle() {
                @Override
                public void addObserver(@NonNull LifecycleObserver observer) {

                }

                @Override
                public void removeObserver(@NonNull LifecycleObserver observer) {

                }

                @NonNull
                @Override
                public State getCurrentState() {
                    return State.RESUMED;
                }
            };
        }
        return lifecycle;
    }

    LiveData<List<Game>> getAllGames() {
        return mAllGames;
    }

    LiveData<List<CardMapping>> getMappings(String gameName) { return mappingsDao.findByGame(gameName);
    }

    public  void  insertMapping (CardMapping mapping) { new insertMappingAsyncTask(mappingsDao).execute(mapping);}

    public void insertGame (Game game) {
        new insertAsyncTask(mGameDao).execute(game);
    }

    public void deleteGame (Game game) {
        //Delete all mappings for a game
        new deleteGameMappingsAsyncTask(mappingsDao).execute(game.getGameName());

        //Delete actual game
        new deleteGameAsyncTask(mGameDao).execute(game);
    }

    public void deleteMapping (CardMapping mapping) { new deleteMappingAsyncTask(mappingsDao).execute(mapping); }

    public Game findGameByName(String gameName){

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

    public CardMapping findMappingById(int id){

        findMappingAsyncTask asyncTask = new findMappingAsyncTask(mappingsDao);
        asyncTask.execute(id);

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

    private static class findMappingAsyncTask extends AsyncTask<Integer, Void, CardMapping> {
        private MappingsDao mAsyncTaskDao;

        findMappingAsyncTask(MappingsDao dao) {

            mAsyncTaskDao = dao;
        }

        @Override
        protected CardMapping doInBackground(final Integer... params) {

            return mAsyncTaskDao.findById(params[0]);
        }

    }
}