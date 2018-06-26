package com.abani.nanodegree.android.popularmoviesapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abani.nanodegree.android.popularmoviesapp.adapters.MoviesAdapter;
import com.abani.nanodegree.android.popularmoviesapp.adapters.interfaces.ItemClickListener;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieDbResponse;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiClient;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiInterface;
import com.abani.nanodegree.android.popularmoviesapp.utils.ConnectivityUtils;
import com.abani.nanodegree.android.popularmoviesapp.viewmodels.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements ItemClickListener{

    /*
    * Used butterknife library to simplify initialization of views
    */
    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.rl_internet_failure)
    RelativeLayout rlInternetFailure;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.bottom_sheet_filter)
    View bottomSheetFilter;

    @BindView(R.id.tv_selected_filter)
    TextView tvSelectedFilter;
    @BindView(R.id.btn_filter)
    Button btnFilter;
    @BindView(R.id.btn_popular)
    Button btnPopular;
    @BindView(R.id.btn_top_rated)
    Button btnTopRated;
    @BindView(R.id.btn_favorites)
    Button btnFavorites;

    private int mFilterId = Constants.POPULAR_MOVIES_ID;

    private BottomSheetBehavior mBottomSheetBehavior;

    private ApiInterface apiInterface;

    private MoviesAdapter adapter;

    private List<Movie> movies;

    MainViewModel viewModel;

    private int currentItemPosition = 0;

    private static final String SAVED_FILTER_ID = "filterId";
    private static final String SAVED_VISIBLE_POSITION = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        apiInterface = ApiClient.getRetrofitClient().create(ApiInterface.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movies = new ArrayList<>();
        adapter = new MoviesAdapter(movies);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_count), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        if (Constants.API_KEY.isEmpty()){
            Toast.makeText(this, "Please add your Api_key in Constants", Toast.LENGTH_SHORT).show();
            return;
        }

        /*
        * Initializing bottom sheet and hiding it on first loading(with or without internet)
        */
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetFilter);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        /*
        * setup button listeners
        */
        setupListeners();

        /*
        * restore state on orientation change
        */
        updateUIFromSavedState(savedInstanceState);

        if (ConnectivityUtils.isConnectedToInternet(this)) {
            /*
            * load movies if internet is connected
            */
            loadMovies();

        } else {
            /*
            * Display network failure error
            */
            showInternetError();

        }
    }

    private void updateUIFromSavedState(Bundle savedInstanceState) {

        if (savedInstanceState != null){

            mFilterId = savedInstanceState.getInt(SAVED_FILTER_ID, Constants.POPULAR_MOVIES_ID);
            currentItemPosition = savedInstanceState.getInt(SAVED_VISIBLE_POSITION, 0);
            if (mFilterId == Constants.TOP_RATED_MOVIES_ID){
                tvSelectedFilter.setText(getString(R.string.text_top_rated_movies_label));
                btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background_selected));
                btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
            } else if (mFilterId == Constants.FAVORITES_MOVIES_ID){
                tvSelectedFilter.setText(getString(R.string.text_favorites_movies_label));
                btnFavorites.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background_selected));
                btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
            }
        }
    }

    private void setupListeners() {

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)){
                    loadMovies();
                    hideInternetError();
                }
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }

            }
        });

        btnPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedFilter.setText(getString(R.string.text_popular_movies_label));
                btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background_selected));
                if (mFilterId == Constants.TOP_RATED_MOVIES_ID) {
                    btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                } else {
                    btnFavorites.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                }
                mFilterId = Constants.POPULAR_MOVIES_ID;
                if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)) {
                    loadMovies();
                } else {
                    showInternetError();
                }
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        btnTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedFilter.setText(getString(R.string.text_top_rated_movies_label));
                btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background_selected));
                if (mFilterId == Constants.POPULAR_MOVIES_ID) {
                    btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                } else {
                    btnFavorites.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                }
                mFilterId = Constants.TOP_RATED_MOVIES_ID;
                if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)) {
                    loadMovies();
                } else {
                    showInternetError();
                }
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSelectedFilter.setText(getString(R.string.text_favorites_movies_label));
                btnFavorites.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background_selected));
                if (mFilterId == Constants.TOP_RATED_MOVIES_ID) {
                    btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                } else {
                    btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_rect_background));
                }
                mFilterId = Constants.FAVORITES_MOVIES_ID;
                if (!ConnectivityUtils.isConnectedToInternet(MainActivity.this)) {
                    hideInternetError();
                }
                loadMovies();
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private void showInternetError(){
        /*if (!movies.isEmpty()){
            movies.clear();
        }*/
        if (rlInternetFailure.getVisibility() != View.VISIBLE) {
            rlInternetFailure.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void hideInternetError(){
        if (rlInternetFailure.getVisibility() == View.VISIBLE) {
            rlInternetFailure.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadMovies(){

        if (mFilterId == Constants.FAVORITES_MOVIES_ID) {

            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    MainActivity.this.movies = movies;
                    adapter.setMovies(MainActivity.this.movies);
                    adapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(currentItemPosition);
                    currentItemPosition = 0;
                }
            });
        } else {

            Call<MovieDbResponse> responseCall;
            if (mFilterId == Constants.POPULAR_MOVIES_ID) {
                responseCall = apiInterface.getPopularMovies(Constants.API_KEY);
            } else {
                responseCall = apiInterface.getTopRatedMovies(Constants.API_KEY);
            }
            responseCall.enqueue(new Callback<MovieDbResponse>() {
                @Override
                public void onResponse(@Nullable Call<MovieDbResponse> call, @Nullable retrofit2.Response<MovieDbResponse> response) {
                    try {
                        movies = response.body().getMovies();
                        //adapter = new MoviesAdapter(movies);
                        adapter.setMovies(movies);
                        //mRecyclerView.setAdapter(adapter);
                        //adapter.setOnItemClickListener(MainActivity.this);
                        adapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(currentItemPosition);
                        currentItemPosition = 0;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@Nullable Call<MovieDbResponse> call, @Nullable Throwable t) {
                }
            });
        }
    }

    /*
    * Called when a movie poster is clicked
    */
    @Override
    public void onItemClicked(Class adapterClass, int position) {

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(Constants.PASSING_MOVIE, movies.get(position));
        startActivity(movieDetailsIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_FILTER_ID, mFilterId);
        currentItemPosition = ((GridLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(SAVED_VISIBLE_POSITION, currentItemPosition);
    }
}
