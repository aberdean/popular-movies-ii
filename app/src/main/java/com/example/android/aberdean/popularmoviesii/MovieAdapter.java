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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Renders the poster images of the movies retrieved from IMDb
 * and passes them to the RecyclerView.
 * When a user clicks on a movie poster, sets up a click handler that
 * passes the position of the movie in the ArrayList to the MainActivity.
 */
class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    /* holds the urls for the posters */
    private ArrayList mPosterData;

    private final MovieAdapterOnClickHandler mClickHandler;

    interface MovieAdapterOnClickHandler {
        void onClick(int adapterPosition);
    }

    /* Constructor */
    MovieAdapter(MovieAdapterOnClickHandler clickHandler) {

        mClickHandler = clickHandler;
    }

    /**
     * Sets up a listener to react when a user clicks on a movie
     * poster.
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        @BindView(R.id.iv_movie_poster) ImageView mPosterImageView;

        MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * When a user clicks on a movie poster,
         * pass the movie position to the handler.
         * @param v the view that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);

        }
    }

    /**
     * When the holder is created, sets up the view
     * and passes it to the holder.
     * @param viewGroup the view group
     * @param viewType the view type
     * @return the view holder
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForPosters = R.layout.poster_item_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForPosters, viewGroup,
                false);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * When binding the view holder, renders the posters.
     * @param holder the view holder
     * @param position the position of the movie
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        final String posterUrl = mPosterData.get(position).toString();
        Context mContext = holder.mPosterImageView.getContext();
        Picasso.with(mContext)
                .load(posterUrl)
                .into(holder.mPosterImageView);
    }

    /**
     * Provides the amount of posters.
     * @return the number of movie posters
     */
    @Override
    public int getItemCount() {
        if (mPosterData == null) {
            return 0;
        }
        return mPosterData.size();
    }

    /**
     * Sets the poster urls and notifies when they change.
     * @param posterData the array of posters
     */
    void setPosterData(ArrayList posterData) {
        mPosterData = posterData;
        notifyDataSetChanged();
    }

    /**
     * Provides the urls of the movie posters.
     * @return the urls of the posters
     */
    ArrayList getPosterUris() {
        return mPosterData;
    }
}
