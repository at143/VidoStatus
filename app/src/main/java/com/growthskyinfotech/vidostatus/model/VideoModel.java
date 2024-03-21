package com.growthskyinfotech.vidostatus.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.growthskyinfotech.vidostatus.utils.Converters;


@Entity(tableName = "videoList")
@TypeConverters(Converters.class)
public class VideoModel {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "video_id")

    public String video_id;

    @ColumnInfo(name = "video_fk")
    public String video_fk;
    @ColumnInfo(name = "views")
    public String views;
    @ColumnInfo(name = "videourl")
    public String videourl;

    @ColumnInfo(name = "rendering")
    public String rendering;

    @ColumnInfo(name = "thumbnail")
    public String thumbnail;

    @ColumnInfo(name = "lastupdate")
    public String lastupdate;

    @ColumnInfo(name = "video_local_title")
    public String video_local_title;

    @ColumnInfo(name = "video_title")
    public String video_title;

    @ColumnInfo(name = "progress")
    public int progress;
    @ColumnInfo(name = "download_id")
    public int download_id;

    public int getDownload_id() {
        return download_id;
    }

    public void setDownload_id(int download_id) {
        this.download_id = download_id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_fk() {
        return video_fk;
    }

    public void setVideo_fk(String video_fk) {
        this.video_fk = video_fk;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getRendering() {
        return rendering;
    }

    public void setRendering(String rendering) {
        this.rendering = rendering;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getVideo_local_title() {
        return video_local_title;
    }

    public void setVideo_local_title(String video_local_title) {
        this.video_local_title = video_local_title;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }
}
