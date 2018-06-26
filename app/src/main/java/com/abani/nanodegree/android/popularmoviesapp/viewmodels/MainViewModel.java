package com.abani.nanodegree.android.popularmoviesapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.abani.nanodegree.android.popularmoviesapp.database.AppDatabase;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public MainViewModel(Application application){
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        movies = database.favoriteMovieDao().loadAllFavoriteMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
