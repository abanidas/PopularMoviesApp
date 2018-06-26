package com.abani.nanodegree.android.popularmoviesapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.abani.nanodegree.android.popularmoviesapp.R;
import com.abani.nanodegree.android.popularmoviesapp.adapters.interfaces.ItemClickListener;
import com.abani.nanodegree.android.popularmoviesapp.constants.Constants;
import com.abani.nanodegree.android.popularmoviesapp.models.Review;
import com.abani.nanodegree.android.popularmoviesapp.utils.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviewReviewAdapter extends RecyclerView.Adapter<MoviewReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews;
    private ItemClickListener itemClickListener;
    Context context;

    public MoviewReviewAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        holder.tvAuthorName.setText(reviews.get(position).getAuthor());
        CommonUtils.makeTextViewResizable(holder.tvReview, 3, reviews.get(position).getContent(), Constants.VIEW_MORE, true);
        holder.tvReview.setText(reviews.get(position).getContent());
        holder.ivTextCirle.setColorFilter(CommonUtils.getRandomMaterialColor(context));
        holder.tvUserFirstLetter.setText(reviews.get(position).getAuthor().substring(0, 1));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_author_name)
        TextView tvAuthorName;
        @BindView(R.id.tv_review)
        TextView tvReview;
        @BindView(R.id.iv_text_circle)
        ImageView ivTextCirle;
        @BindView(R.id.tv_user_first_letter)
        TextView tvUserFirstLetter;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
