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

/**
 * Passes the reviews for a movie retrieved from IMDb
 * into the RecyclerView.
 */
class ReviewAdapter
        extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private String[] mReviewData;

    public ReviewAdapter() {

    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_reviews);
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
        final String review = mReviewData[position];
        holder.mReviewTextView.setText(review);
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) {
            return 0;
        }
        return mReviewData.length;
    }

    void setReviewData(String[] reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }
}
