package com.growthskyinfotech.vidostatus.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.growthskyinfotech.vidostatus.MyApplication;
import com.growthskyinfotech.vidostatus.MyUtils;
import com.growthskyinfotech.vidostatus.R;
import com.growthskyinfotech.vidostatus.databinding.ActivityDownloaderBinding;
import com.growthskyinfotech.vidostatus.favorite.FavoriteDatabase;
import com.growthskyinfotech.vidostatus.model.VideoModel;
import com.growthskyinfotech.vidostatus.recorder.service.ConnectionReceiver;
import com.growthskyinfotech.vidostatus.utils.DownloadService;
import com.growthskyinfotech.vidostatus.view.PermissionUtils;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoDownloader extends AppCompatActivity {


    private static Activity activity;
    ActivityDownloaderBinding binding;

    FavoriteDatabase database;
    List<VideoModel> videoModelList = new ArrayList<>();

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(MyUtils.getPermissionStatus(activity, key));
                        }
                    }
                    if (blockPermissionCheck.contains("blocked")) {
                        showPermissionSettingsDialog();
                    }

                }
            });
    private BroadcastReceiver progressReceiver;

    public static VideoDownloader getInstance() {
        return (VideoDownloader) activity;
    }

    private void showPermissionSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Permissions Required");
        builder.setMessage("This app requires certain permissions to function. Please grant the permissions from the app settings.");
        builder.setPositiveButton("Open Settings", (dialog, which) -> {
            openAppSettings();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        database = Room.databaseBuilder(this, FavoriteDatabase.class, getString(R.string.app_name)).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        PermissionUtils permissionUtils = new PermissionUtils(this, mPermissionResult);

        if (!permissionUtils.isStoragePermissionGranted()) {
            permissionUtils.takeStoragePermission();
        }


        videoModelList = database.favoriteDao().getVideoList();

        if (videoModelList != null && !videoModelList.isEmpty()) {

            setupAdapter();

        } else {

            saveJsonToRoomNSetupAdapter();

        }


        if (MyApplication.getInstance().downloadIntent == null) {

            MyApplication.getInstance().downloadIntent = new Intent(activity, DownloadService.class);
            activity.bindService(MyApplication.getInstance().downloadIntent, MyApplication.getInstance().downloadConnection, 1);
            activity.startService(MyApplication.getInstance().downloadIntent);

        }
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    private void setupAdapter() {
        binding.allVideos.setLayoutManager(new LinearLayoutManager(this));
        videoModelList = database.favoriteDao().getVideoList();

        DownloadAdapter downloadAdapter = new DownloadAdapter(this, videoModelList);


        binding.allVideos.setAdapter(downloadAdapter);

        downloadAdapter.onItemClickListener((position, videoModel, viewHolder) -> {

            if (ConnectionReceiver.isConnected()) {

                try {

                    MyApplication.getInstance().downloadService.addDownloadData(position, videoModel, downloadAdapter);

                } catch (Exception e) {
                    Toast.makeText(activity, "Something Wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
                dialog.setTitle("Network Connection Problem");
                dialog.setMessage("Please turn on network connection");
                dialog.setCancelable(false);
                dialog.setPositiveButton("try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (ConnectionReceiver.isConnected()) {

                            try {

                                MyApplication.getInstance().downloadService.addDownloadData(position, videoModel, downloadAdapter);

                            } catch (Exception e) {
                                Toast.makeText(activity, "Something Wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(VideoDownloader.this, "Still not Turned On", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.create().show();


            }

        });

        progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null && intent.getAction().equals("download-progress")) {
                    int progress = intent.getIntExtra("progress", 0);
                    int position = intent.getIntExtra("position", 0);
                    int downloadId = intent.getIntExtra("downloadId", 0);

                    Log.d("previewDonwloadingItem", "" + progress);

                    if (downloadAdapter != null) {
                        downloadAdapter.updateProgress(position, progress, downloadId);
                    }

                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(progressReceiver,
                new IntentFilter("download-progress"));
    }

    private void saveJsonToRoomNSetupAdapter() {
        try {
            // Read JSON data from raw resource
            InputStream inputStream = getResources().openRawResource(R.raw.videos);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            String json = new String(b);

            // Parse JSON array
            JSONArray jsonArray = new JSONArray(json);

            // Now you can work with your JSON data
            for (int i = 0; i < jsonArray.length(); i++) {

                String videoId = jsonArray.getJSONObject(i).getString("video_id");
                String videoFk = jsonArray.getJSONObject(i).getString("video_fk");
                String views = jsonArray.getJSONObject(i).getString("views");
                String videoUrl = jsonArray.getJSONObject(i).getString("videourl");
                String rendering = jsonArray.getJSONObject(i).getString("rendering");
                String thumbnail = jsonArray.getJSONObject(i).getString("thumbnail");
                String lastUpdate = jsonArray.getJSONObject(i).getString("lastupdate");
                String videoLocalTitle = jsonArray.getJSONObject(i).getString("video_local_title");
                String videoTitle = jsonArray.getJSONObject(i).getString("video_title");

                VideoModel videoModel = new VideoModel();
                videoModel.setVideo_id(videoId);
                videoModel.setVideo_fk(videoFk);
                videoModel.setViews(views);
                videoModel.setVideourl(videoUrl);
                videoModel.setRendering(rendering);
                videoModel.setThumbnail(thumbnail);
                videoModel.setLastupdate(lastUpdate);
                videoModel.setVideo_local_title(videoLocalTitle);
                videoModel.setVideo_title(videoTitle);
                videoModel.setProgress(database.favoriteDao().getprogress(videoId));

                videoModelList.add(videoModel);
            }
            database.favoriteDao().addAllVideos(videoModelList);

            setupAdapter();

        } catch (Exception e) {

            Toast.makeText(activity, "Errorr " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
