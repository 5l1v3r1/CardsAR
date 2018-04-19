package com.wear.cardsar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Carlos on 3/28/2018.
 */

@Entity(tableName = "game")
public class Game {

    public Game(@NonNull String gameName, String description) {
        this.gameName= gameName;
        this.description = description;
    }

    @PrimaryKey(autoGenerate = true)
    private int gid;

    @NonNull
    @ColumnInfo(name = "game_name")
    private String gameName;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "test")
    private  String test;

    /// Getters and Setters for each entity value
    public int getGid() {
        return this.gid;
    }

    public void setGid( int gid) {
        this.gid = gid;
    }

    public String getGameName() {
        return this.gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getDescription() { return this.description; }

    public void setDescription( String description) { this.description = description; }

    public String getTest() { return this.test; }

    public void setTest(String test) { this.test = test; }

}