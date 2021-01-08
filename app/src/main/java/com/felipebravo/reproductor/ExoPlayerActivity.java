package com.felipebravo.reproductor;


import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class ExoPlayerActivity extends AppCompatActivity implements Player.EventListener {
    PlayerView playerView = null;
    DataSource.Factory datasourcefactory;
    MediaSource audiosource = null;
    SimpleExoPlayer simpleExoPlayer;
    String videoUri;
    ImageView imageViewExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        imageViewExit = findViewById(R.id.imageViewExit);

        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.stop();
                simpleExoPlayer.seekTo(0);
                finish();

            }
        });

        playerView = findViewById(R.id.videoFullScreenPlayer);

        if (getIntent().hasExtra("URL")) {
             videoUri = getIntent().getStringExtra("URL");
        }

        exoplayer(videoUri);
    }


    public void exoplayer(String url){

        if(audiosource == null){

            playerView.setControllerShowTimeoutMs(0);
            playerView.setCameraDistance(30);


            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(simpleExoPlayer);
            datasourcefactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "app"));
            audiosource = new ProgressiveMediaSource.Factory(datasourcefactory).createMediaSource(Uri.parse(url));


            simpleExoPlayer.prepare(audiosource);
            simpleExoPlayer.setPlayWhenReady(true);
        }


    }

    @Override
    public void onBackPressed()
    {
        simpleExoPlayer.setPlayWhenReady(false);
        simpleExoPlayer.stop();
        simpleExoPlayer.seekTo(0);
        super.onBackPressed();

    }
}
