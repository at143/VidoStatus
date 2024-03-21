package com.growthskyinfotech.vidostatus.recorder.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.growthskyinfotech.vidostatus.MyApplication;

public class ConnectionReceiver  extends BroadcastReceiver {


   public static ConnectionAvailableListener connectionAvailableListener;

    public ConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();


        if (connectionAvailableListener !=null){
            connectionAvailableListener.isConnected(isConnected);
        }
    }


    public  interface  ConnectionAvailableListener{

        void isConnected(boolean connected);

    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
