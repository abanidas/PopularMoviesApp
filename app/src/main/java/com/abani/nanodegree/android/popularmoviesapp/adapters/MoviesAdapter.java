package com.abani.nanodegree.android.popularmoviesapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abani.nanodegree.android.popularmoviesapp.R;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Movie;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>{

    private List<Movie> movies;
    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        /*
         * Used butterknife library to simplify initialization of views
         */
        @BindView(R.id.iv_movie_poster)
        ImageView ivMovieYear;
        @BindView(R.id.tv_year)
        TextView tvYear;
        @BindView(R.id.tv_avg_vote)
        TextView tvAvgVote;
        @BindView(R.id.tv_vote_count)
        TextView tvVoteCount;

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

        holder.tvYear.setText(movies.get(position).getReleaseDate().substring(0, 4));
        holder.tvAvgVote.setText(String.valueOf(movies.get(position).getVoteAverage()));
        holder.tvVoteCount.setText(CommonUtils.formatWithParenthesis(String.valueOf(movies.get(position).getVoteCount())));

        holder.ivMovieYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

}
