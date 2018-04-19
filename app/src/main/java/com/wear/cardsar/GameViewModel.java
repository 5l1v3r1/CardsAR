package com.wear.cardsar;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

/**
 * Created by Carlos on 3/28/2018.
 */

public class GameViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<Game>> mAllGames;

    public GameViewModel (Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllGames = mRepository.getAllGames();
    }

    LiveData<List<Game>> getAllGames() { return mAllGames; }

    public void insert(Game game) { mRepository.insertGame(game); }

    public void delete(Game game) {mRepository.deleteGame(game);}

    public int getnMappings(Game game) {
        List<CardMapping> mappings = mRepository.findStaticMappingsByName(game.getGameName());
        return mappings.size();
    }

    public Game findGameByName(String gameName) {return mRepository.findGameByName(gameName);}
}