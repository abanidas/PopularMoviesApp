package com.abani.nanodegree.android.popularmoviesapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abani.nanodegree.android.popularmoviesapp.adapters.MovieVideoAdapter;
import com.abani.nanodegree.android.popularmoviesapp.adapters.MoviewReviewAdapter;
import com.abani.nanodegree.android.popularmoviesapp.adapters.interfaces.ItemClickListener;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.database.AppDatabase;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieReviewResponse;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieVideo;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieVideoResponse;
import com.abani.nanodegree.android.popularmoviesapp.models.Review;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiClient;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiInterface;
import com.abani.nanodegree.android.popularmoviesapp.utils.AppExecutors;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;
import com.abani.nanodegree.android.popularmoviesapp.viewmodels.AddToFavViewModel;
import com.abani.nanodegree.android.popularmoviesapp.viewmodels.AddToFavViewModelFactory;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements ItemClickListener {

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
    @BindView(R.id.rv_movie_videos)
    RecyclerView rvMovieVideos;
    @BindView(R.id.rv_movie_reviews)
    RecyclerView rvMovieReviews;
    @BindView(R.id.btn_add_to_favorite)
    FloatingActionButton btnAddToFavorite;
    @BindView(R.id.progress_bar_rating)
    ProgressBar progressBarRating;
    @BindView(R.id.tv_review_not_available)
    TextView tvReviewNotAvailable;
    @BindView(R.id.tv_video_not_available)
    TextView tvVideoNotAvailable;


    private Movie currentMovie;
    private List<MovieVideo> videos;
    private MovieVideoAdapter videoAdapter;
    private List<Review> reviews;
    private MoviewReviewAdapter reviewAdapter;

    private AppDatabase mDb;
    private boolean isFavorite = false;

    private int ratingPercent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        Intent detailsIntent = getIntent();
        if (detailsIntent != null){
            currentMovie = (Movie) detailsIntent.getSerializableExtra(Constants.PASSING_MOVIE);
        }
        else {
            return;
        }

        videos = new ArrayList<>();
        rvMovieVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvMovieVideos.setHasFixedSize(true);
        videoAdapter = new MovieVideoAdapter();
        videoAdapter.setVideos(videos);
        videoAdapter.setOnItemClickListener(this);
        rvMovieVideos.setAdapter(videoAdapter);

        reviews = new ArrayList<>();
        rvMovieReviews.setLayoutManager(new LinearLayoutManager(this));
        rvMovieReviews.setHasFixedSize(true);
        rvMovieReviews.setNestedScrollingEnabled(false);
        reviewAdapter = new MoviewReviewAdapter(this);
        reviewAdapter.setReviews(reviews);
        rvMovieReviews.setAdapter(reviewAdapter);

        collapsingToolbarLayout.setTitle(currentMovie.getTitle());

        setSupportActionBar(toolbarMovieDetails);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        populateMovieDetails();

        populateMovieVideos();
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
                .load(Constants.IMAGE_BASE_URL + currentMovie.getBackdropPath())
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .into(ivToolbarMoviePoster);

        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + currentMovie.getPosterPath())
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .into(ivMoviePoster);

        String releaseDateToDisplay = "N/A";
        try {
            Date responseDate = CommonUtils.getResponseFormatter().parse(currentMovie.getReleaseDate());
            releaseDateToDisplay = CommonUtils.getFormatterToDisplay().format(responseDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvMovieReleaseDate.setText(releaseDateToDisplay);
        tvMovieVote.setText(String.valueOf(currentMovie.getVoteAverage()));
        animateRating();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                CommonUtils.makeTextViewResizable(tvMovieDescription, 3, currentMovie.getOverview(), Constants.VIEW_MORE, true);
            }
        }
        tvMovieDescription.setText(currentMovie.getOverview());

        AddToFavViewModelFactory viewModelFactory = new AddToFavViewModelFactory(mDb, currentMovie.getId());
        AddToFavViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddToFavViewModel.class);

        viewModel.getMovieId().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                if (currentMovie.getId().equals(integer)){
                    btnAddToFavorite.setImageResource(R.drawable.ic_favorite_selected);
                    isFavorite = true;
                } else {
                    btnAddToFavorite.setImageResource(R.drawable.ic_favorite);
                    isFavorite = false;
                }
            }
        });

        btnAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        if (isFavorite){
                            mDb.favoriteMovieDao().deleteFavoriteMovie(currentMovie);
                            showMessage("Removed from favorite");
                        } else {
                            mDb.favoriteMovieDao().insertFavoriteMovie(currentMovie);
                            showMessage("Added to favorite");
                        }


                    }
                });
            }
        });
    }

    public void animateRating() {

        new Thread(new Runnable() {
            public void run() {
                while (ratingPercent < (currentMovie.getVoteAverage()*10)) {
                    ratingPercent += 1;
                    try {
                        Thread.sleep(15);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarRating.setProgress(ratingPercent);
                            tvMovieVote.setText(String.valueOf((float) ratingPercent/10));
                        }
                    });
                }
            }
        }).start();
    }

    private void showMessage(String message) {
        //Toast.makeText(MovieDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void populateMovieVideos() {

        ApiInterface apiInterface = ApiClient.getRetrofitClient().create(ApiInterface.class);
        Call<MovieVideoResponse> videoResponseCall = apiInterface.getMovieVideos(currentMovie.getId(), Constants.API_KEY);
        videoResponseCall.enqueue(new Callback<MovieVideoResponse>() {
            @Override
            public void onResponse(Call<MovieVideoResponse> call, Response<MovieVideoResponse> response) {
                videos = response.body().getVideos();
                if (!videos.isEmpty()){
                    tvVideoNotAvailable.setVisibility(View.GONE);
                }
                videoAdapter.setVideos(videos);
                rvMovieVideos.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
                populateMovieReviews();
            }

            @Override
            public void onFailure(Call<MovieVideoResponse> call, Throwable t) {

            }
        });
    }

    private void populateMovieReviews() {

        ApiInterface apiInterface = ApiClient.getRetrofitClient().create(ApiInterface.class);
        Call<MovieReviewResponse> reviewResponseCall = apiInterface.getMovieReviews(currentMovie.getId(), Constants.API_KEY);
        reviewResponseCall.enqueue(new Callback<MovieReviewResponse>() {
            @Override
            public void onResponse(Call<MovieReviewResponse> call, Response<MovieReviewResponse> response) {
                reviews = response.body().getReviews();
                if (!reviews.isEmpty()){
                    tvReviewNotAvailable.setVisibility(View.GONE);
                }
                reviewAdapter.setReviews(reviews);
                rvMovieReviews.setAdapter(reviewAdapter);
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieReviewResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemClicked(Class adapterClass, int position) {

        if (adapterClass == MovieVideoAdapter.class){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.VIDEO_BASE_URL + videos.get(position).getKey())));
        }

    }
}
