package com.felipebravo.reproductor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import com.felipebravo.reproductor.R;
import com.felipebravo.reproductor.activities.AddSongActivity;
import com.felipebravo.reproductor.database.DatabaseClass;
import com.felipebravo.reproductor.entity.Song;
import com.felipebravo.reproductor.globalVariables.GlobalVariables;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.UserHolder> {

    public List<Song> songList;
    DatabaseClass db;
    private Context mContext;

    public SongAdapter(List<Song> userList) {
        this.songList = userList;
    }

    @NonNull
    @Override
    public SongAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        mContext = parent.getContext();
        db = Room.databaseBuilder(parent.getContext(),
                DatabaseClass.class, "myDatabase").allowMainThreadQueries().build();

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_adapter, parent, false);
        UserHolder userAdapter = new UserHolder(v);
        return userAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull final SongAdapter.UserHolder userHolder, final int position) {
        userHolder.song.setText(songList.get(position).getSong().toString());
        userHolder.band.setText(songList.get(position).getBand().toString());


        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Editar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                GlobalVariables.id = songList.get(position).getId();
                                GlobalVariables.song = songList.get(position).getSong();
                                GlobalVariables.band = songList.get(position).getBand();
                                GlobalVariables.updateFlag = "update";

                                Intent intent = new Intent(mContext, AddSongActivity.class);
                                mContext.startActivity(intent);
                            }
                        });


                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Eliminar de la lista",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteSong(songList.get(position).getId());
                                dialog.dismiss();
                                songList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, songList.size());


                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }


    private void deleteSong(int id) {
        Song song = new Song();
        song.setId(id);
        db.daoClass().deleteSong(song);
        Toast.makeText(mContext, "Canción Eliminada!", Toast.LENGTH_SHORT).show();
    }


    public class UserHolder extends RecyclerView.ViewHolder {

        private TextView song, band;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.song);
            band = itemView.findViewById(R.id.band);

        }
    }
}
