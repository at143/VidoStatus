package com.growthskyinfotech.vidostatus.utils;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.growthskyinfotech.vidostatus.model.VideoModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<VideoModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}