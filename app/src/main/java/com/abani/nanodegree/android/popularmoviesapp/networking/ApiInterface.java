package com.abani.nanodegree.android.popularmoviesapp.networking;

import com.abani.nanodegree.android.popularmoviesapp.models.MovieDbResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/popular")
    Call<MovieDbResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieDbResponse> getTopRatedMovies(@Query("api_key") String apiKey);

}