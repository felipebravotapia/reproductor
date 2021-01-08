package com.felipebravo.reproductor.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "song")
public class Song {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "song")
    private String song;

    @ColumnInfo(name = "band")
    private String band;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }
}
