package com.wear.cardsar;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

//This class has all the SQL queries that are called on the bd
@Dao
public interface MappingsDao {

    //return all mappings
    @Query("SELECT * FROM mappings")
    LiveData<List<CardMapping>> getAll();

    //return all mappings that match the gamename (Live Data)
    @Query("SELECT * FROM mappings where game = :gameName")
    LiveData<List<CardMapping>> findByGame(String gameName);

    //return al mappings that match the gamename (static data)
    @Query("SELECT * FROM mappings where game = :gameName")
    List<CardMapping> findStaticByGame(String gameName);

    //returns all the mappings that match an id
    @Query("SELECT * FROM mappings where game = :mid")
    CardMapping findById(int mid);

    //returns count of mappings
    @Query("SELECT COUNT(*) from mappings")
    int countMappings();

    //insert mutiple mappings to db
    @Insert
    void insertAll(CardMapping... mappings);

    //insert a mapping to db
    @Insert
    void insert(CardMapping mapping);

    //delete mapping from db
    @Delete
    void delete(CardMapping mapping);

    //delete specific mapping from db
    @Query("DELETE FROM mappings where game = :gameName")
    void deleteGameMappings(String gameName);
}