package com.felipebravo.reproductor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MusicList extends AppCompatActivity {

    private static final String TAG = "MusicList";

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://itunes.apple.com/search?term=deeppurple&mediaType=music&limit=20";
    ArrayList<listSong> androidSong = new ArrayList<listSong>();
    private ProgressDialog pDialog;

    ImageView bg;
    PlayerView playerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        getDataResponse();

        bg = findViewById(R.id.logo);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        final ListView listView = (ListView) findViewById(R.id.listview_flavor);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                listSong o = (listSong) listView.getItemAtPosition(position);
                Log.i("Click", "click en el elemento " + o.getTrackName() + " de mi ListView");

                try {

                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(o.getImage()).getContent());
                    Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    Drawable drawable = new BitmapDrawable(bitmapScaled);
                    bg.setBackgroundDrawable(drawable);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                exoplayer(o.getMusic());
            }
        });
    }



    public void exoplayer(String url){

        playerView = findViewById(R.id.playerview);
        playerView.setControllerShowTimeoutMs(0);
        playerView.setCameraDistance(30);
        SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
        DataSource.Factory datasourcefactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "app"));
        MediaSource audiosource = new ProgressiveMediaSource.Factory(datasourcefactory).createMediaSource(Uri.parse(url));
        simpleExoPlayer.prepare(audiosource);
        simpleExoPlayer.setPlayWhenReady(true);
        //simpleExoPlayer.getDuration();
    }



    private void getDataResponse() {
        pDialog = new ProgressDialog(MusicList.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray media = obj.getJSONArray("results");

                    for(int i = 0; i < media.length(); i++){
                        JSONObject url = media.getJSONObject(i);

                        listSong song = new listSong();

                        song.setImage(url.getString("artworkUrl100"));
                        song.setMusic(url.getString("previewUrl"));
                        song.setTrackName(url.getString("trackName"));
                        song.setArtistName(url.getString("artistName"));
                        song.setReleaseDate(url.getString("releaseDate"));
                        song.setTrackId(url.getInt("trackId"));

                        androidSong.add(song);
                    }
                    AndroidSongAdapter songAdapter = new AndroidSongAdapter(MusicList.this, androidSong);
                    ListView listView = (ListView) findViewById(R.id.listview_song);
                    listView.setAdapter(songAdapter);

                    songAdapter.notifyDataSetChanged();
                    hidePDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error :" + error.toString());
                hidePDialog();
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}