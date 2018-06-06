package com.abani.nanodegree.android.popularmoviesapp;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abani.nanodegree.android.popularmoviesapp.adapters.MoviesAdapter;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieDbResponse;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiClient;
import com.abani.nanodegree.android.popularmoviesapp.networking.ApiInterface;
import com.abani.nanodegree.android.popularmoviesapp.utils.ConnectivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ItemClickListener{

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
    @BindView(R.id.radio_filter_group)
    RadioGroup radioFilterGroup;
    @BindView(R.id.btn_popular)
    RadioButton btnPopular;
    @BindView(R.id.btn_top_rated)
    RadioButton btnTopRated;

    private int mFilterId = Constants.POPULAR_MOVIES_ID;

    private BottomSheetBehavior mBottomSheetBehavior;

    private MoviesAdapter adapter;

    private List<Movie> movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

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

        if (ConnectivityUtils.isConnectedToInternet(this)) {
            /*
            * load movies if internet is connected
            */
            loadMovies();

        } else {
            /*
            * Display network failure error
            */
            rlInternetFailure.setVisibility(View.VISIBLE);
            btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)){
                        loadMovies();
                        rlInternetFailure.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void setupListeners() {

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)) {
                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
            }
        });

        radioFilterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                if (ConnectivityUtils.isConnectedToInternet(MainActivity.this)) {
                    switch (checkedId) {
                        case R.id.btn_popular:
                            tvSelectedFilter.setText(getString(R.string.text_popular_movies_label));
                            mFilterId = Constants.POPULAR_MOVIES_ID;
                            btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chip));
                            btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chip_unselected));
                            break;
                        case R.id.btn_top_rated:
                            tvSelectedFilter.setText(getString(R.string.text_top_rated_movies_label));
                            mFilterId = Constants.TOP_RATED_MOVIES_ID;
                            btnTopRated.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chip));
                            btnPopular.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_chip_unselected));
                            break;
                    }
                    loadMovies();
                } else {
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadMovies(){

        movies = new ArrayList<>();

        ApiInterface apiInterface = ApiClient.getRetrofitClient().create(ApiInterface.class);

        Call<MovieDbResponse> responseCall;
        if (mFilterId == Constants.POPULAR_MOVIES_ID){
            responseCall = apiInterface.getPopularMovies(Constants.API_KEY);
        } else {
            responseCall = apiInterface.getTopRatedMovies(Constants.API_KEY);
        }
        responseCall.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(@Nullable Call<MovieDbResponse> call, @Nullable retrofit2.Response<MovieDbResponse> response) {
                try {
                    movies = response.body().getMovies();
                    adapter = new MoviesAdapter(movies);
                    mRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(MainActivity.this);
                } catch (RuntimeException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@Nullable Call<MovieDbResponse> call, @Nullable Throwable t) {
            }
        });
    }

    /*
    * Called when a movie poster is clicked
    */
    @Override
    public void onItemClicked(int position) {

        Intent movieDetailsIntent = new Intent(this, MovieDetailsActivity.class);
        movieDetailsIntent.putExtra(Constants.PASSING_MOVIE, movies.get(position));
        startActivity(movieDetailsIntent);
    }
}
