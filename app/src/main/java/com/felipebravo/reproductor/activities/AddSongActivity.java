package com.felipebravo.reproductor.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.felipebravo.reproductor.R;
import com.felipebravo.reproductor.database.DatabaseClass;
import com.felipebravo.reproductor.entity.Song;
import com.felipebravo.reproductor.globalVariables.GlobalVariables;

public class AddSongActivity extends AppCompatActivity {


    private EditText mSong, mBand;
    private Button mAddSong;
    private String songString, bandString;

    private Context mContext;
    DatabaseClass db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = AddSongActivity.this;

        mSong = findViewById(R.id.song);
        mBand = findViewById(R.id.band);
        mAddSong = findViewById(R.id.addSong);


        if (GlobalVariables.updateFlag.equals("update")){
            mSong.setText(GlobalVariables.song);
            mBand.setText(GlobalVariables.band);
        }

        db = Room.databaseBuilder(getApplicationContext(),
                DatabaseClass.class, "myDatabase").allowMainThreadQueries().build();

        mAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                songString = mSong.getText().toString();
                bandString = mBand.getText().toString();

                if (songString.length()>0 && bandString.length()>0){

                    if (GlobalVariables.updateFlag.equals("update")){
                        Song song = new Song();
                        song.setId(GlobalVariables.id);
                        song.setSong(songString);
                        song.setBand(bandString);
                        db.daoClass().updateSong(song);
                        Toast.makeText(mContext, "Canción Editada", Toast.LENGTH_SHORT).show();
                        GlobalVariables.updateFlag = "";

                        startActivity(new Intent(AddSongActivity.this, FavoriteSongActivity.class));
                    }else {

                        Song song = new Song();
                        song.setSong(songString);
                        song.setBand(bandString);
                        db.daoClass().addSong(song);
                        Toast.makeText(mContext, "Canción Guardada", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(AddSongActivity.this, FavoriteSongActivity.class));

                    }

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GlobalVariables.updateFlag = "";
    }
}
