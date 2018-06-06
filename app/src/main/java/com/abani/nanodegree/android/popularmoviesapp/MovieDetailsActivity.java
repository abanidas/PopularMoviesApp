package com.abani.nanodegree.android.popularmoviesapp;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.iv_toolbar_movie_poster)
    ImageView ivToolbarMoviePoster;
    @BindView(R.id.iv_movie_poster)
    ImageView ivMoviePoster;
    @BindView(R.id.tv_movie_release_date)
    TextView tvMovieReleaseDate;
    @BindView(R.id.tv_movie_vote)
    TextView tvMovieVote;
    @BindView(R.id.tv_movie_description)
    TextView tvMovieDescription;
    @BindView(R.id.toolbar_layout_movie_details)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_movie_details)
    Toolbar toolbarMovieDetails;


    private Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Intent detailsIntent = getIntent();
        if (detailsIntent != null){
            currentMovie = (Movie) detailsIntent.getSerializableExtra(Constants.PASSING_MOVIE);
        }
        else {
            return;
        }

        collapsingToolbarLayout.setTitle(currentMovie.getTitle());

        setSupportActionBar(toolbarMovieDetails);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        populateMovieDetails();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateMovieDetails() {

        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + currentMovie.getPosterPath())
                .placeholder(R.drawable.moviedb_placeholder)
                .error(R.drawable.moviedb_placeholder)
                .into(ivToolbarMoviePoster);

        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + currentMovie.getPosterPath())
                .placeholder(R.drawable.moviedb_placeholder)
                .error(R.drawable.moviedb_placeholder)
                .into(ivMoviePoster);

        //tvMovieTitle.setText(currentMovie.getTitle());
        tvMovieReleaseDate.setText(currentMovie.getReleaseDate());
        tvMovieVote.setText(String.valueOf(currentMovie.getVoteAverage()));
        tvMovieDescription.setText(currentMovie.getOverview());
    }
}
