package com.growthskyinfotech.vidostatus.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {
    public static final String SHARED_NAME = "shared_file";
    public static SharedPreferences instance;
    SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {

        instance = context.getSharedPreferences(SHARED_NAME, 0);

        editor = instance.edit();
    }


    public static void init(Context context) {
        if (instance == null) {
            instance = context.getSharedPreferences(SHARED_NAME, 0);
        }

    }


    public static Boolean get(String tag) {
        return instance.getBoolean(tag, false);
    }

    public static void set(String sharedString, String str) {
        Editor edit = instance.edit();
        edit.putString(sharedString, str);
        edit.apply();
    }

    public static void set(String tag, Boolean isTapEnable) {
        Editor edit1 = instance.edit();
        edit1.putBoolean(tag, isTapEnable);
        edit1.apply();
    }

    public void setInt(String PREF_NAME, int VAL) {
        Editor edit1 = instance.edit();
        edit1.putInt(PREF_NAME, VAL);
        edit1.commit();
    }

    public int getInt(String key) {
        return instance.getInt(key, 0);
    }

    public int getInt(String str, int i) {
        return instance.getInt(str, i);
    }

    public static String getString(String sharedString) {
        return instance.getString(sharedString, "");
    }

    public boolean getBoolean(String PREF_NAME) {
        return instance.getBoolean(PREF_NAME, false);
    }

    public void setBoolean(String PREF_NAME, Boolean val) {
        editor.putBoolean(PREF_NAME, val);
        editor.commit();
    }

    public void setString(String PREF_NAME, String VAL) {
        editor.putString(PREF_NAME, VAL);
        editor.commit();
    }

    public boolean getBoolean(String str, boolean z) {
        return instance.getBoolean(str, z);
    }
}
