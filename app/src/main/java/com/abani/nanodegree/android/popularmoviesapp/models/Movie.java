
package com.abani.nanodegree.android.popularmoviesapp.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorite_movie")
public class Movie implements Serializable{

    @SerializedName("vote_count")
    private Integer voteCount;
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private Integer id;
    @SerializedName("video")
    private Boolean video;
    @ColumnInfo(name = "vote_average")
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("title")
    private String title;
    @SerializedName("popularity")
    private Double popularity;
    @ColumnInfo(name = "poster_path")
    @SerializedName("poster_path")
    private String posterPath;
    @ColumnInfo(name = "original_language")
    @SerializedName("original_language")
    private String originalLanguage;
    @ColumnInfo(name = "original_title")
    @SerializedName("original_title")
    private String originalTitle;
    @ColumnInfo(name = "backdrop_path")
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("adult")
    private Boolean adult;
    @SerializedName("overview")
    private String overview;
    @ColumnInfo(name = "release_date")
    @SerializedName("release_date")
    private String releaseDate;

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
