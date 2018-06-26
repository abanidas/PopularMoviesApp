package com.abani.nanodegree.android.popularmoviesapp.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MovieVideoResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<MovieVideo> videos = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos = videos;
    }

}
