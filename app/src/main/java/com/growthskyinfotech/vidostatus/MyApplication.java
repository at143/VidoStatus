package com.growthskyinfotech.vidostatus;

import static com.growthskyinfotech.vidostatus.MyUtils.ADS_ENABLE;
import static com.growthskyinfotech.vidostatus.MyUtils.OPEN_AD_ID;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.growthskyinfotech.vidostatus.recorder.service.ConnectionReceiver;
import com.growthskyinfotech.vidostatus.utils.DownloadService;
import com.growthskyinfotech.vidostatus.view.PreferenceManager;
/*import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;*/

import org.json.JSONObject;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MyApplication extends Application {
    public static SimpleCache simpleCache = null;
    public static Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);
    public static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    public static ExoDatabaseProvider exoDatabaseProvider = null;
    private static MyApplication mInstance;
    public MyApplication context;
    public Intent downloadIntent;
    PreferenceManager preferenceManager;


    public DownloadService downloadService;


    public ServiceConnection downloadConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
            MyApplication.this.downloadService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MyApplication.this.downloadService.stopForeground(false);
        }
    };

    public static synchronized MyApplication getInstance() {
        MyApplication webApplication;
        synchronized (MyApplication.class) {
            webApplication = mInstance;
        }
        return webApplication;
    }
    public void getConnectionStatus(ConnectionReceiver.ConnectionAvailableListener listener) {
        ConnectionReceiver.connectionAvailableListener = listener;
    }

    public void onCreate() {
        super.onCreate();
        context = this;

        mInstance = this;

        preferenceManager = new PreferenceManager(context);


        //set App Open Ads

      //  OneSignal.initWithContext(this);
      //  OneSignal.setAppId(MyUtils.ONESIGNAL_ID);

      /*  List<String> testDeviceIds = Collections.singletonList(MyUtils.ADMOB_TEST_ID);

        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/

        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                freeMemory();
            }
            Log.i("TAG", "onCreate: " + simpleCache.getCacheSpace());
        }

    }


    public void freeMemory() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


}

