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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.aberdean.popularmoviesii.models.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;

/**
 * Renders the thumbnails of the trailers for a movie retrieved from IMDb
 * and passes them to the RecyclerView.
 * When a user clicks on a trailer thumbnail, it sets up a click handler that
 * passes the position of the trailer in the ArrayList to the MovieDetails activity.
 */
class TrailerAdapter
        extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mTrailerData;

    private final TrailerAdapterOnClickHandler mClickHandler;

    interface TrailerAdapterOnClickHandler {
        void onClick(int adapterPosition);
    }

    TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {

        mClickHandler = clickHandler;
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_trailer_title) TextView mTrailerTitleTextView;
        @BindView(R.id.iv_trailer) ImageView mTrailerImageView;

        TrailerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForTrailers = R.layout.trailer_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForTrailers, viewGroup,
                false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        Context mContext = holder.mTrailerImageView.getContext();
        holder.mTrailerTitleTextView.setText(mTrailerData.get(position).getName());
        Log.v(TAG, "Trailer URL " + mTrailerData.get(position).getTrailerThumbUrl(mContext));
        final String trailerUrl = mTrailerData.get(position).getTrailerThumbUrl(mContext);
        Picasso.with(mContext)
                .load(trailerUrl)
                .into(holder.mTrailerImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null) {
            return 0;
        }
        return mTrailerData.size();
    }

    void setTrailerData(List<Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }
}
