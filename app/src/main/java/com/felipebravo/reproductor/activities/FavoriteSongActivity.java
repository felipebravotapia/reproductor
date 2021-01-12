package com.felipebravo.reproductor.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import com.felipebravo.reproductor.R;
import com.felipebravo.reproductor.adapters.SongAdapter;
import com.felipebravo.reproductor.database.DatabaseClass;
import com.felipebravo.reproductor.entity.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FavoriteSongActivity extends AppCompatActivity {


    DatabaseClass db;

    private RecyclerView mRecyclerView;
    public SongAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Song> songList;

    private String name = "";
    private String emailString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_song_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Lista de canciones Favoritas");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(FavoriteSongActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = Room.databaseBuilder(getApplicationContext(),
                DatabaseClass.class, "myDatabase").allowMainThreadQueries().build();
        getAllData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(FavoriteSongActivity.this, AddSongActivity.class));
            }
        });
    }


    private void getAllData() {
        List<Song> songList = new ArrayList<>();
        songList = db.daoClass().getSongs();

        if (songList.size() > 0) {
            mAdapter = new SongAdapter(songList);
            mRecyclerView.setAdapter(mAdapter);

        } else {
            Toast.makeText(this, "Sin canciones favoritas", Toast.LENGTH_SHORT).show();
        }
    }

/*    private void deleteUser() {
        Song song = new Song();
        song.setId(3);
        db.daoClass().deleteSong(song);

        Toast.makeText(this, "Delete song", Toast.LENGTH_SHORT).show();
    }

    private void updateUser() {
        Song song = new Song();
        song.setId(4);
        song.setSong("Smoke on the Water");
        song.setBand("Deep Purple");
        db.daoClass().updateSong(song);
        Toast.makeText(this, "Song Updated", Toast.LENGTH_SHORT).show();
    }*/


}
