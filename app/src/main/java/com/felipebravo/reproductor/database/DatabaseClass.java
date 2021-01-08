package com.felipebravo.reproductor.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.felipebravo.reproductor.dao.DaoClass;
import com.felipebravo.reproductor.entity.Song;

@Database(entities = Song.class, version = 1)
public abstract class DatabaseClass extends RoomDatabase {
    public abstract DaoClass daoClass();
}
