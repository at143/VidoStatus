package com.growthskyinfotech.vidostatus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.growthskyinfotech.vidostatus.BuildConfig;
import com.growthskyinfotech.vidostatus.MyApplication;
import com.growthskyinfotech.vidostatus.MyUtils;
import com.growthskyinfotech.vidostatus.R;
import com.growthskyinfotech.vidostatus.databinding.ActivitySavedBinding;

import java.io.File;

public class SaveVideoActivity extends AppCompatActivity {

    String path;
    private Activity context;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView exoPlayerVideoDetail;


    ActivitySavedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        context = this;

        Intent intent1 = getIntent();

        if (intent1.getExtras() != null) {

            path = intent1.getStringExtra("finalVideo");
            if (path != null && path.contains(".mp4")) {


                initializePlayer();
                binding.imageView.setVisibility(View.GONE);
            } else {

                Glide.with(context).load(path).into(binding.imageView);
                binding.aspectFrameRatio.setVisibility(View.GONE);

            }

        }


        binding.back.setOnClickListener(view -> onBackPressed());

        binding.whatsapp.setOnClickListener(view -> {

            view.startAnimation(MyUtils.buttonClick);
            MyUtils.shareImageVideoOnWhatsapp(context, path, path.endsWith(".mp4"));
        });


        binding.insta.setOnClickListener(view -> {

            view.startAnimation(MyUtils.buttonClick);
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType(path.endsWith(".mp4") ? "video/*" : "image/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            String stringBuilder2 = getString(R.string.share_txt) +
                    getPackageName();
            intent.putExtra(Intent.EXTRA_TEXT, stringBuilder2);
            intent.setPackage("com.instagram.android");
            try {
                startActivity(Intent.createChooser(intent, "Share Video..."));

            } catch (ActivityNotFoundException unused) {
                Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
            }

        });
        binding.more.setOnClickListener(view -> {

            view.startAnimation(MyUtils.buttonClick);
            shareVideoMore(context, getString(R.string.share_txt) + getPackageName(), path);

        });

    }

    public static void shareVideoMore(Activity context, String text, String path) {
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(path.endsWith(".mp4") ? "video/*" : "image/*");
            Uri uriForFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(intent, "Share Your Video!"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")

    public void initializePlayer() {

        RenderersFactory renderersFactory = new DefaultRenderersFactory(context);

        TrackSelector trackSelector = new DefaultTrackSelector();

        simpleExoPlayer = new SimpleExoPlayer.Builder(context, renderersFactory)
                .setTrackSelector(trackSelector)
                .build();

        exoPlayerVideoDetail.setPlayer(simpleExoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, getString(R.string.app_name));
        CacheDataSource.Factory cacheDataSourceFactory = new CacheDataSource.Factory()
                .setCache(MyApplication.simpleCache)
                .setUpstreamDataSourceFactory(dataSourceFactory);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));


        MediaSource mediaSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(mediaItem);

        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        simpleExoPlayer.prepare(mediaSource);
        exoPlayerVideoDetail.setOnTouchListener(new View.OnTouchListener() {

            private final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);

                    if (!simpleExoPlayer.getPlayWhenReady()) {
                        simpleExoPlayer.setPlayWhenReady(true);
                        exoPlayerVideoDetail.hideController();


                    } else {

                        new Handler(getMainLooper()).postDelayed(() -> simpleExoPlayer.setPlayWhenReady(false), 100);
                    }


                    return true;
                }


                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if (!simpleExoPlayer.getPlayWhenReady()) {
                        simpleExoPlayer.setPlayWhenReady(true);
                    }

                    return super.onDoubleTap(e);

                }
            });


            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);


                return true;
            }
        });

    }

    private void initView() {
        exoPlayerVideoDetail = findViewById(R.id.exo_player_video_detail);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (path != null && path.endsWith(".mp4")) {
            pausePlayer();
        }


    }

    public void pausePlayer() {
        this.simpleExoPlayer.setPlayWhenReady(false);
        this.simpleExoPlayer.getPlaybackState();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (path != null && path.endsWith(".mp4")) {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (path != null && path.endsWith(".mp4")) {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.release();
            }
        }


    }
}