package com.growthskyinfotech.vidostatus.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.growthskyinfotech.vidostatus.MyApplication;
import com.growthskyinfotech.vidostatus.MyUtils;
import com.growthskyinfotech.vidostatus.R;
import com.growthskyinfotech.vidostatus.activity.DownloadAdapter;
import com.growthskyinfotech.vidostatus.activity.VideoDownloader;
import com.growthskyinfotech.vidostatus.model.VideoModel;

public class DownloadService extends Service {
    private static final String TAG = "groupA";
    public static int counter = 0;
    public static String CHANNEL_ID = "1234";
    public static int NOTIFICATION_ID = 12;
    private Context context;
    private final IBinder downloadBind = new DownloadBinder();
    MediaPlayer mediaPlayer;
    Integer downloadId = 0;

    DownloadAdapter downloadAdapter;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;

    @NonNull
    private Uri getUriForSoundName(@NonNull Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName()
                + "/raw/" + "notification.mp3");
    }

    private void sendNotification() {

        Intent intent;

        intent = new Intent(context, VideoDownloader.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //   Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri uri = Uri.parse("android.resource://" + MyApplication.getInstance().getApplicationContext().getPackageName() + "/" + R.raw.notification);

        AudioAttributes soundAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BrandBanao2";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
            notificationManager.createNotificationChannel(mChannel);
        }


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setAutoCancel(true)
                .setLights(Color.RED, 800, 800)
                .setContentText("Video Download")
                .setSound(uri)
                .setChannelId(CHANNEL_ID);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
        try {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBuilder.setContentTitle(context.getString(R.string.app_name));
        mBuilder.setTicker(context.getString(R.string.app_name));


        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();


        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;

        notificationManager.notify(NOTIFICATION_ID, notification);

        try{

            mediaPlayer = MediaPlayer.create(this,Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
            mediaPlayer.start();

        }catch (Exception e){

            e.printStackTrace();
        }



    }


    private void showForegroundNotification(String contentText) {
        Intent intent = new Intent(this, VideoDownloader.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);


        builder.setProgress(100, 0, false);

        Uri uri = Uri.parse("android.resource://" + MyApplication.getInstance().getApplicationContext().getPackageName() + "/" + R.raw.notification);

        builder.setContentTitle("Downloading")
                .setContentText("Downloading")
                .setSmallIcon(R.drawable.logo)
                .setProgress(100, 0, true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
               // .setSound(uri)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BrandBanao1";
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name,
                            NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        Notification notification = builder.build();

       // notification.sound = getUriForSoundName(this);
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
        startForeground(NOTIFICATION_ID, notification);


       /* try{

            mediaPlayer = MediaPlayer.create(this,Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification));
            mediaPlayer.start();

        }catch (Exception e){

            e.printStackTrace();
        }*/
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.mipmap.ic_launcher;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    private int getColour() {
        return 0x8b5630;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.context = this;
        return Service.START_STICKY;
    }

    public class DownloadBinder extends Binder {
        public DownloadBinder() {
        }

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return this.downloadBind;
    }

    public void addDownloadData(int position, VideoModel VideoModel, DownloadAdapter downloadAdapter) {

        this.downloadAdapter = downloadAdapter;

        NOTIFICATION_ID = Integer.parseInt(VideoModel.getVideo_id());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        showForegroundNotification("Downloading...");
        Intent intent = new Intent("download-progress");

        downloadId = PRDownloader.download(VideoModel.getVideourl(), MyUtils.getStoreVideoExternalStorage(VideoDownloader.getInstance()), VideoModel.getVideo_title() + ".mp4").setTag(VideoModel.getVideo_id()).build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        int bytesWritten = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        //    downloadAdapter.updateProgress(position, bytesWritten,downloadId,viewHolder);

                        builder.setProgress(100, bytesWritten, false);
                        intent.putExtra("progress", bytesWritten);
                        intent.putExtra("position", position);
                        intent.putExtra("downloadId", downloadId);
                        LocalBroadcastManager.getInstance(MyApplication.getInstance().getApplicationContext()).sendBroadcast(intent);
                    }
                }).start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        sendNotification();
                        notificationManager.cancelAll();
                    }

                    @Override
                    public void onError(Error error) {


                    }
                });


    }


}
