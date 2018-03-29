package com.wear.cardsar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Carlos on 3/28/2018.
 */

@Entity(tableName = "game")
public class Game {

    @PrimaryKey
    private int gid;

    @ColumnInfo(name = "game_name")
    private String gameName;


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


}