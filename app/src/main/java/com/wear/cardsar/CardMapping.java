package com.wear.cardsar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity( tableName = "mappings")
public class CardMapping {

    public CardMapping(@NonNull String mappingName, String game) {
        this.mappingName= mappingName;
        this.game = game;

    }

    @PrimaryKey(autoGenerate = true)
    private int mid;

    @ColumnInfo(name = "mapping_name")
    private String mappingName;

    @ColumnInfo(name = "game")
    private String game;
    ///Getters and Setters for each value

    public int getMid() {
        return this.mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMappingName() {
        return this.mappingName;
    }

    public void setMappingName( String mappingName) {
        this.mappingName = mappingName;
    }

    public String getGame() { return this.game;}

    public void setGame(String gameName) { this.game = gameName;}

    /*
    public int getGid() {
        return this.mid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }
    */
}
