package com.felipebravo.reproductor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.room.Room;


import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.felipebravo.reproductor.activities.BottomSheetFavoriteSong;
import com.felipebravo.reproductor.database.DatabaseClass;
import com.felipebravo.reproductor.entity.Song;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;
public class MainActivity extends AppCompatActivity {

    Animation atg, atgtwo, cardAmin;
    ImageView logo_bg, imageViewListLikeSong;
    LinearLayout viewList, viewAlbum, searchLl;
    TextView textView, nameSong, nameBand, date;
    FloatingActionButton btnLike, btnListPlayers;

    private BroadcastReceiver mNetworkReceiver;
    static TextView tv_check_connection;
    private static final String TAG = MainActivity.class.getName();
    ArrayList<listSong> androidSong = new ArrayList<listSong>();

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private ImageLoader imageLoader;
    private NetworkImageView imageView;

    PlayerView playerView = null;
    DataSource.Factory datasourcefactory;
    MediaSource audiosource = null;
    SimpleExoPlayer simpleExoPlayer;
    DatabaseClass db;

    //ActivityMainBinding bi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //bi = DataBindingUtil.setContentView(this, R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db = Room.databaseBuilder(getApplicationContext(),
                DatabaseClass.class, "myDatabase").allowMainThreadQueries().build();

        tv_check_connection = (TextView) findViewById(R.id.tv_check_connection);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcastForNougat();

        Onboarding();

        imageView = (NetworkImageView) findViewById(R.id.imageView);
        viewAlbum = findViewById(R.id.viewAlbum);
        textView = findViewById(R.id.textView);
        logo_bg = findViewById(R.id.logo_bg);
        viewList = findViewById(R.id.viewList);
        searchLl = findViewById(R.id.searchLl);
        playerView = findViewById(R.id.playerview);
        btnListPlayers = findViewById(R.id.imageViewPlayers);
        imageViewListLikeSong = findViewById(R.id.imageViewListLikeSong);


        nameSong = findViewById(R.id.name_song);
        nameBand = findViewById(R.id.name_group);
        date = findViewById(R.id.date);

        viewAlbum.setVisibility(View.GONE);
        viewList.setVisibility(View.GONE);
        playerView.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = viewList.getLayoutParams();
        params.height = 1700;
        viewList.setLayoutParams(params);


        final EditText edittext = (EditText) findViewById(R.id.searchSong);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Toast.makeText(MainActivity.this, edittext.getText(), Toast.LENGTH_SHORT).show();
                    getDataResponse(edittext.getText().toString());
                    viewAlbum.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo);
        cardAmin = AnimationUtils.loadAnimation(this, R.anim.ancard);

        searchLl.startAnimation(atg);
        logo_bg.startAnimation(atg);


        imageViewListLikeSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation hyperspaceJump = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shakemylikes);
                imageViewListLikeSong.startAnimation(hyperspaceJump);

                BottomSheetFavoriteSong bottomSheet = new BottomSheetFavoriteSong();
                bottomSheet.show(getSupportFragmentManager(),bottomSheet.getTag());
                /*Intent intent = new Intent(MainActivity.this, FavoriteSongActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());*/
            }
        });

        btnLike = findViewById(R.id.fablike);

        final ListView listView = (ListView) findViewById(R.id.listview_song);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                listSong itemAlbum = (listSong) listView.getItemAtPosition(position);
                Log.i("Click", "click en el elemento " + itemAlbum.getTrackName() + " de mi ListView");

                btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake);
                        btnLike.startAnimation(shake);

                        Song song = new Song();
                        song.setSong(itemAlbum.getTrackName());
                        song.setBand(itemAlbum.getArtistName());
                        db.daoClass().addSong(song);
                        Toast.makeText(getApplicationContext(), "Like - " + itemAlbum.getTrackName(), Toast.LENGTH_SHORT).show();

                    }
                });

                btnListPlayers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        simpleExoPlayer.setPlayWhenReady(false);
                        simpleExoPlayer.stop();
                        simpleExoPlayer.seekTo(0);

                        Intent intent = new Intent(MainActivity.this, ExoPlayerActivity.class);
                        intent.putExtra("URL", itemAlbum.getMusic());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());

                    }
                });


                nameSong.setText(itemAlbum.getTrackName());
                nameBand.setText(itemAlbum.getArtistName());

                String sourceDate = itemAlbum.getReleaseDate();
                DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                DateFormat destDf = new SimpleDateFormat("dd/MM/YYYY");
                try {
                    Date dateformat = srcDf.parse(sourceDate);
                    String targetDate = destDf.format(dateformat);
                    date.setText(targetDate);

                } catch (ParseException ex) {
                    ex.printStackTrace();
                }

                viewAlbum.setVisibility(View.VISIBLE);
                viewAlbum.startAnimation(cardAmin);

                loadImage(itemAlbum.getImage());
                ViewGroup.LayoutParams params = viewList.getLayoutParams();
                params.height = 1250;
                viewList.setLayoutParams(params);
                exoplayer(itemAlbum.getMusic());
            }
        });
    }


    private void Onboarding() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(R.id.searchLl)
                .setPrimaryText("Busca tus artistas y canciones favoritas")
                .setSecondaryText("Podrás escuchar lo que te guste")
                .setBackgroundColour(getColor(R.color.red_color))
                //.setIcon(R.drawable.ic_baseline_search_24)
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                            showLikeSongsPrompt();
                        }
                    }
                })
                .show();
    }

    public void showLikeSongsPrompt() {
        new MaterialTapTargetPrompt.Builder(MainActivity.this)
                .setTarget(findViewById(R.id.imageViewListLikeSong))
                .setIcon(R.drawable.ic_baseline_queue_music_gray)
                .setBackgroundColour(getColor(R.color.red_yellow))
                .setPrimaryText("Lista de canciones")
                .setSecondaryText("Lista de canciones Favoritas")
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .show();
    }


    private void loadImage(String url) {
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(imageView,
                R.drawable.dom, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(url, imageLoader);
    }


    public void exoplayer(String url) {
        if (audiosource == null) {

            playerView.setVisibility(View.VISIBLE);
            playerView.startAnimation(atg);

            playerView.setControllerShowTimeoutMs(0);
            playerView.setCameraDistance(30);

            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(simpleExoPlayer);
            datasourcefactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
            audiosource = new ProgressiveMediaSource.Factory(datasourcefactory).createMediaSource(Uri.parse(url));

            simpleExoPlayer.prepare(audiosource);
            simpleExoPlayer.setPlayWhenReady(true);


        } else {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.seekTo(0);

            audiosource = new ProgressiveMediaSource.Factory(datasourcefactory).createMediaSource(Uri.parse(url));

            simpleExoPlayer.prepare(audiosource);
            simpleExoPlayer.setPlayWhenReady(true);
        }

    }


    private void showComponent() {
        viewList.setVisibility(View.VISIBLE);
    }

    private void animation() {
        viewList.startAnimation(atgtwo);
    }


    private void getDataResponse(String search) {
        androidSong.clear();
        String url = "https://itunes.apple.com/search?term=" + search + "&mediaType=music&limit=20";

        mRequestQueue = Volley.newRequestQueue(this);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray media = obj.getJSONArray("results");

                    for (int i = 0; i < media.length(); i++) {
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
                    AndroidSongAdapterMain songAdapter = new AndroidSongAdapterMain(MainActivity.this, androidSong);
                    ListView listView = (ListView) findViewById(R.id.listview_song);
                    listView.setAdapter(songAdapter);

                    songAdapter.notifyDataSetChanged();

                    showComponent();
                    animation();


                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "Error :(", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error :" + error.toString());

                Toast.makeText(MainActivity.this, "Sin Resultados :(", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringRequest);
    }


    public static void dialog(boolean value) {

        if (value) {
            tv_check_connection.setText("Conectado");
            tv_check_connection.setBackgroundColor(Color.parseColor("#4ED1C1"));
            tv_check_connection.setTextColor(Color.WHITE);

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    tv_check_connection.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(delayrunnable, 3000);
        } else {
            tv_check_connection.setVisibility(View.VISIBLE);
            tv_check_connection.setText("Sin Conexión");
            tv_check_connection.setBackgroundColor(Color.parseColor("#F0657A"));
            tv_check_connection.setTextColor(Color.WHITE);
        }
    }


    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }


}