/*
 * Copyright (C) 2017 Antonella Bernobich Dean
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.aberdean.popularmoviesii;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.aberdean.popularmoviesii.models.Review;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

import static java.security.AccessController.getContext;

/**
 * Passes the reviews for a movie retrieved from IMDb
 * into the RecyclerView.
 */
class ReviewAdapter
        extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private List<Review> mReviewData;

    public ReviewAdapter() {

    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_author) TextView mAuthorTextView;
        @BindView(R.id.tv_review) TextView mReviewTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForReviews = R.layout.review_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForReviews, viewGroup,
                false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {

        Review review = mReviewData.get(position);
        Context context = holder.mAuthorTextView.getContext();
        holder.mAuthorTextView.setText(String.format(context
                .getResources().getString(R.string.review_author), review.getAuthor()));
        holder.mReviewTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) {
            return 0;
        }
        return mReviewData.size();
    }

    void setReviewData(List<Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }
}
