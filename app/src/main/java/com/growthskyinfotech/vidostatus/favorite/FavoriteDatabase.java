package com.growthskyinfotech.vidostatus.favorite;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.growthskyinfotech.vidostatus.model.VideoModel;
import com.growthskyinfotech.vidostatus.utils.Converters;

@Database(entities = {FavoriteList.class, VideoModel.class}, version = 10)
@TypeConverters({Converters.class})
public abstract class FavoriteDatabase extends RoomDatabase {
    public abstract FavoriteDao favoriteDao();

}
 