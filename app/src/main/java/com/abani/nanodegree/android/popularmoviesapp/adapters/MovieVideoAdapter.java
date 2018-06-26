package com.abani.nanodegree.android.popularmoviesapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abani.nanodegree.android.popularmoviesapp.R;
import com.abani.nanodegree.android.popularmoviesapp.adapters.interfaces.ItemClickListener;
import com.abani.nanodegree.android.popularmoviesapp.models.MovieVideo;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.VideoViewHolder> {

    private List<MovieVideo> videos;
    private ItemClickListener itemClickListener;

    public MovieVideoAdapter() {
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos = videos;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, final int position) {

        Picasso.get()
                .load(CommonUtils.getVideoThumbnailURL(videos.get(position).getKey()))
                .placeholder(R.drawable.ic_rect_background)
                .error(R.drawable.moviedb_placeholder)
                .into(holder.btnOpenVideo);

        holder.tvVideoTitle.setText(videos.get(position).getName());

        holder.btnOpenVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(MovieVideoAdapter.class, position);
                //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.VIDEO_BASE_URL + videos.get(position).getKey())));
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.btn_open_video)
        ImageButton btnOpenVideo;
        @BindView(R.id.tv_video_title)
        TextView tvVideoTitle;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
