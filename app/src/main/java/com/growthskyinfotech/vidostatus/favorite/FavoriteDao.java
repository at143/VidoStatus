package com.growthskyinfotech.vidostatus.favorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.growthskyinfotech.vidostatus.model.VideoModel;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    public void addAllVideos(List<VideoModel> videoModels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addAllVideos(VideoModel videoModels);

    @Query("update videoList set progress =:progress where video_id=:videoId")
    void updateProgress(String videoId, int progress);

    @Delete
    public void deleteVideo(VideoModel videoModel);

    @Query("select * from videoList order by id desc")
    public List<VideoModel> getVideoList();

    @Query("select progress from videoList where video_id=:videoId")
    public int getprogress(String videoId);

}