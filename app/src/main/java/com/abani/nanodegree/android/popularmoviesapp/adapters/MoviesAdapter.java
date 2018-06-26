package com.abani.nanodegree.android.popularmoviesapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abani.nanodegree.android.popularmoviesapp.R;
import com.abani.nanodegree.android.popularmoviesapp.adapters.interfaces.ItemClickListener;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private List<Movie> movies;
    private ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        /*
         * Used butterknife library to simplify initialization of views
         */
        @BindView(R.id.iv_movie_poster)
        ImageView ivMovieYear;
        @BindView(R.id.tv_movie_title)
        TextView tvMovieTitle;
        @BindView(R.id.tv_avg_vote)
        TextView tvAvgVote;
        @BindView(R.id.tv_release_date)
        TextView tvReleaseDate;
        @BindView(R.id.progressbar_rating)
        ProgressBar progressBarRating;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public MoviesAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, final int position) {

        Picasso.get()
                .load(Constants.IMAGE_BASE_URL + movies.get(position).getPosterPath())
                .placeholder(R.drawable.ic_rect_background)
                .error(R.drawable.moviedb_placeholder)
                .into(holder.ivMovieYear);

        holder.tvMovieTitle.setText(movies.get(position).getTitle());
        holder.tvAvgVote.setText(String.valueOf(movies.get(position).getVoteAverage()));
        holder.progressBarRating.setProgress((int) (movies.get(position).getVoteAverage() * 10));

        String releaseDateToDisplay = "N/A";
        try {
            Date responseDate = CommonUtils.getResponseFormatter().parse(movies.get(position).getReleaseDate());
            releaseDateToDisplay = CommonUtils.getFormatterToDisplay().format(responseDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvReleaseDate.setText(releaseDateToDisplay);

        holder.ivMovieYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(MoviesAdapter.class, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
