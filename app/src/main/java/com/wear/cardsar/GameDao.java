package com.wear.cardsar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

//class contains all the sql queries used to get db content
@Dao
public interface GameDao {

    //gets all the games in game table
    @Query("SELECT * FROM game")
    LiveData<List<Game>> getAll();

    //returns a game specified
    @Query("SELECT * FROM game where game_name LIKE  :gameName ")
    Game findByName(String gameName);

    //returns the number of games
    @Query("SELECT COUNT(*) from game")
    int countGames();

    //inserts games into db
    @Insert
    void insertAll(Game... games);

    //inserts a game into the db
    @Insert
    void insert(Game game);

    //deletes a game from db
    @Delete
    void delete(Game game);

    //deletes all games
    @Query("DELETE FROM game")
    void deleteAll();
}