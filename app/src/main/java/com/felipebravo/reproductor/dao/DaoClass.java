package com.felipebravo.reproductor.dao;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.felipebravo.reproductor.entity.Song;

import java.util.List;

@Dao
public interface DaoClass {

    /***
     *  insert data
     * @param song
     */
    @Insert
    public void addSong(Song song);

    /**
     * retrieve all data from song table
     * @return
     */
    @Query("select * from song")
    public List<Song> getSongs();

    /***
     * Delete song from song table
     * @param song
     */
    @Delete
    public void deleteSong(Song song);


    /***
     * Update song from song table
     * @param song
     */
    @Update
    public void updateSong(Song song);
}
