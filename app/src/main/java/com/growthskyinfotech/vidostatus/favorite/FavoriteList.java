package com.growthskyinfotech.vidostatus.favorite;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favoritelist")
public class FavoriteList {

    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "code")
    String code;

    @ColumnInfo(name = "group")
    String group;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "no_of_image")
    String resImageNum;

    @ColumnInfo(name = "thumb_link")
    String thumb_link;

    @ColumnInfo(name = "video_link")
    String video_link;

    @ColumnInfo(name = "zip_link")
    String zip_link;

    @ColumnInfo(name = "template_json")
    String template_json;

    @ColumnInfo(name = "category")
    String category;

    @SerializedName("user_name")
    String user_name;

    @SerializedName("created")
    int created;

    @SerializedName("views")
    int views;

    @SerializedName("premium")
    boolean premium;

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getCreated() {
        return created;
    }

    public int getViews() {
        return views;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResImageNum() {
        return resImageNum;
    }

    public void setResImageNum(String resImageNum) {
        this.resImageNum = resImageNum;
    }

    public String getThumb_link() {
        return thumb_link;
    }

    public void setThumb_link(String thumb_link) {
        this.thumb_link = thumb_link;
    }

    public String getVideo_link() {
        return video_link;
    }

    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public String getZip_link() {
        return zip_link;
    }

    public void setZip_link(String zip_link) {
        this.zip_link = zip_link;
    }

    public String getTemplate_json() {
        return template_json;
    }

    public void setTemplate_json(String template_json) {
        this.template_json = template_json;
    }



}
