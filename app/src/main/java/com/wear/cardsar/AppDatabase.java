
package com.wear.cardsar;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

// ROOM database has the database creation functions and db populating functions
@Database(entities = {Game.class, CardMapping.class}, version = 9)
public abstract class AppDatabase extends RoomDatabase {

    // private vars
    private static AppDatabase INSTANCE;

    // publi vars
    public abstract GameDao gameDao();
    public abstract MappingsDao mappingDao();

    //executes when there is a change in the db
    private static AppDatabase.Callback sAppDatabaseCallback =
            new AppDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    //start db instance
    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "game-database")
                            // We should not use main thread queries
                            //.allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            //.addCallback(sAppDatabaseCallback)
                            .build();
        }
        return INSTANCE;
    }

    // del db instance
    public static void destroyInstance() {
        INSTANCE = null;
    }

    //populates db
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final GameDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.gameDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();
            Game word = new Game("Game1", "description");
            mDao.insert(word);
            word = new Game("Game2", "description2");
            mDao.insert(word);
            return null;
        }
    }
}

