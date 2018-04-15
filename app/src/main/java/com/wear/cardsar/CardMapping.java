package com.wear.cardsar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Carlos on 3/28/2018.
 */

@Entity( tableName = "mappings")
public class CardMapping {

    @PrimaryKey
    private int mid;

    @ColumnInfo(name = "mapping_name")
    private String mappingName;

    // num to 52 each mapping to a playing card
    @ColumnInfo(name = "card_mapping")
    private int cardMapping;

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

    public int getCardName() {
        return this.cardMapping;
    }

    public int getCardMapping() {
        return this.cardMapping;
    }

    public void setCardMapping(int cardMapping) {
        this.cardMapping = cardMapping;
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
