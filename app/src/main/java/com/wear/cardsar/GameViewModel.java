package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

//holds all the Games information. also handles fetching information from the database
public class GameViewModel extends AndroidViewModel {

    //private vars
    private AppRepository mRepository;
    private LiveData<List<Game>> mAllGames;

    //constructor
    public GameViewModel (Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllGames = mRepository.getAllGames();
    }

    //returns all games in db
    LiveData<List<Game>> getAllGames() { return mAllGames; }

    //inserts a game to the db
    public void insert(Game game) { mRepository.insertGame(game); }

    //
    public void delete(Game game) {mRepository.deleteGame(game);}

    public int getnMappings(Game game) {
        List<CardMapping> mappings = mRepository.findStaticMappingsByName(game.getGameName());
        return mappings.size();
    }

    public Game findGameByName(String gameName) {return mRepository.findGameByName(gameName);}
}