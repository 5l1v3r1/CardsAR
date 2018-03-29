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
public interface MappingsDao {

    @Query("SELECT * FROM mappings")
    LiveData<List<CardMapping>> getAll();

    @Query("SELECT * FROM mappings where card_mapping = :gid ")
    LiveData<List<CardMapping>> findByGame(int gid);

    @Query("SELECT COUNT(*) from mappings where card_mapping = :gid")
    int countMappings(int gid);

    @Insert
    void insertAll(Game... games);

    @Delete
    void delete(Game game);
}