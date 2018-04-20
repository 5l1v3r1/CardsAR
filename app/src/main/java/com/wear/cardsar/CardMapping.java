package com.wear.cardsar;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//Db card mapping entity
@Entity( tableName = "mappings")
public class CardMapping {

    //constructor
    public CardMapping(@NonNull String mappingName, String game, String mappingDescription, int quantity, String mappingUri) {
        this.mappingName= mappingName;
        this.game = game;
        this.mappingDescription = mappingDescription;
        this.quantity = quantity;
        this.mappingUri = mappingUri;
    }

    // all tables col per card mapping
    @PrimaryKey(autoGenerate = true)
    private int mid;

    @ColumnInfo(name = "mapping_name")
    private String mappingName;

    @ColumnInfo(name = "description")
    private String mappingDescription;

    @ColumnInfo(name = "game")
    private String game;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "uri")
    private String mappingUri;


    ///Getters and Setters for each value

    int getMid() {
        return this.mid;
    }

    void setMid(int mid) {
        this.mid = mid;
    }

    String getMappingName() {
        return this.mappingName;
    }

    void setMappingName( String mappingName) {
        this.mappingName = mappingName;
    }

    String getGame() { return this.game;}

    void setGame(String gameName) { this.game = gameName;}

    int getQuantity() {return this.quantity;}

    void setQuantity(int quantity){ this.quantity = quantity;}

    void setMappingDescription(String mappingDescription) {
        this.mappingDescription = mappingDescription;
    }

    String getMappingDescription() {
        return this.mappingDescription;
    }

    String getMappingUri() { return this.mappingUri; }

    void setMappingUri(String mappingUri) { this.mappingUri = mappingUri; }

}
