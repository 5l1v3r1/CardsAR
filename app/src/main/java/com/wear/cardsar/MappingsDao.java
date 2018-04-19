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

    @Query("SELECT * FROM mappings where game = :gameName ")
    LiveData<List<CardMapping>> findByGame(String gameName);

    @Query("SELECT * FROM mappings where game = :mid")
    CardMapping findById(int mid);

    @Query("SELECT COUNT(*) from mappings")
    int countMappings();

    @Insert
    void insertAll(CardMapping... mappings);

    @Insert
    void insert(CardMapping mapping);

    @Delete
    void delete(CardMapping mapping);

    @Query("DELETE FROM mappings where game = :gameName")
    void deleteGameMappings(String gameName);
}