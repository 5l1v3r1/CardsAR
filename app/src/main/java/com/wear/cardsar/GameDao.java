package com.wear.cardsar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.wear.cardsar.Game;

import java.util.List;

/**
 * Created by Carlos on 3/28/2018.
 */

@Dao
public interface GameDao {

    @Query("SELECT * FROM game")
    LiveData<List<Game>> getAll();

    @Query("SELECT * FROM game where game_name LIKE  :gameName ")
    Game findByName(String gameName);

    @Query("SELECT COUNT(*) from game")
    int countGames();

    @Insert
    void insertAll(Game... games);

    @Insert
    void insert(Game game);

    @Delete
    void delete(Game game);
}