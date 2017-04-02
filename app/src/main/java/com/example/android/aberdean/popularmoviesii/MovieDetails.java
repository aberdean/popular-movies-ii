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

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.aberdean.popularmoviesii.utilities.MovieJsonUtils;
import com.example.android.aberdean.popularmoviesii.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.BindView;

import static android.content.Intent.ACTION_VIEW;

/**
 * Presents the user with a detailed view of the chosen movie,
 * including a backdrop picture, a thumbnail poster, the original title,
 * release date and rating, and a synopsis.
 * Allows the user to choose between a dark and light theme.
 */
public class MovieDetails extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private static final String TAG = MovieDetails.class.getSimpleName();

    private String mId;
    private ArrayList<String> mTrailerUris;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    @BindView(R.id.recyclerview_reviews) RecyclerView mReviews;
    @BindView(R.id.recyclerview_trailers) RecyclerView mTrailers;

    @BindView(R.id.iv_backdrop) ImageView mBackdrop;
    @BindView(R.id.iv_poster_thumb) ImageView mPosterThumb;

    @BindView(R.id.tv_title) TextView mOriginalTitle;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    @BindView(R.id.tv_release_date) TextView mReleaseDate;
    @BindView(R.id.tv_rating) TextView mRating;

    /**
     * Assigns the appropriate values for the chosen movie.
     * @param savedInstanceState the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("movieDetails")) {

                Resources res = getResources();

                ArrayList mChosenMovie = (ArrayList<?>)
                        intent.getSerializableExtra("movieDetails");

                String posterUri = mChosenMovie.get(0).toString();
                String backdropUri = mChosenMovie.get(1).toString();
                setImage(posterUri, backdropUri);

                String synopsis = mChosenMovie.get(2).toString();
                mSynopsis.setText(synopsis);

                String releaseDate = mChosenMovie.get(3).toString();
                String release = String.format(
                        res.getString(R.string.released), releaseDate);
                mReleaseDate.setText(release);

                String title = mChosenMovie.get(4).toString();
                mOriginalTitle.setText(title);

                String rating = mChosenMovie.get(5).toString();
                String rate = String.format(
                        res.getString(R.string.rating), rating);
                mRating.setText(rate);

                mId = mChosenMovie.get(6).toString();
        }

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mReviews.setLayoutManager(reviewLayoutManager);
        mReviews.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mReviews.setAdapter(mReviewAdapter);

        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mTrailers.setLayoutManager(trailerLayoutManager);
        mTrailers.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailers.setAdapter(mTrailerAdapter);

        fetchReviews();
        fetchTrailers();

    }

    private void fetchReviews() {
        new ReviewQueryTask().execute(mId);
    }

    private class ReviewQueryTask extends
            AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String id = params[0];
            URL reviewRequestUrl = NetworkUtils
                    .buildAdditionalDetailsUrl(id, "/reviews");

            try {
                String jsonReviewResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                String[] jsonReviewData = MovieJsonUtils
                        .getReviewStringsFromJson(MovieDetails.this,
                                jsonReviewResponse);

                return jsonReviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] reviewData) {
            if (reviewData != null) {
                mReviewAdapter.setReviewData(reviewData);
            }
        }
    }

    /**
     * When a trailer thumbnail is clicked, starts an implicit intent to launch
     * the trailer either on a YouTube app or on a browser.
     * @param trailerPosition the position of the trailer in the ArrayList
     */
    @Override
    public void onClick(int trailerPosition) {
        String trailer = mTrailerUris.get(trailerPosition);
        Intent intentToStartTrailer = new Intent(ACTION_VIEW, Uri.parse(trailer));
        startActivity(intentToStartTrailer);
    }

    private void fetchTrailers() {
        new TrailerQueryTask().execute(mId);
    }

    private class TrailerQueryTask extends
            AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String id = params[0];
            URL trailerRequestUrl = NetworkUtils
                    .buildAdditionalDetailsUrl(id, "/videos");

            try {
                String jsonTrailerResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                String[] jsonTrailerData = MovieJsonUtils
                        .getTrailerStringsFromJson(MovieDetails.this,
                                jsonTrailerResponse);
                Log.v(TAG, "Trailer Codes: " + jsonTrailerData);
                return jsonTrailerData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailerData) {
            if (trailerData != null) {

                String[] trailerCodes = trailerData;
                ArrayList<String> mTrailerThumbUris =
                        new ArrayList<>(trailerCodes.length);
                mTrailerUris =
                        new ArrayList<>(trailerCodes.length);
                for (String trailerCode : trailerCodes) {
                    String baseTrailerThumbUri =
                            "https://img.youtube.com/vi/";
                    String endTrailerThumbUri = "/0.jpg";
                    mTrailerThumbUris.add(baseTrailerThumbUri + trailerCode + endTrailerThumbUri);

                    String baseTrailerUri = "https://www.youtube.com/watch?v=";
                    mTrailerUris.add(baseTrailerUri + trailerCode);
                }
                mTrailerAdapter.setTrailerData(mTrailerThumbUris);

            }
        }
    }

    /**
     * Builds the url for the appropriate movie's poster and backdrop,
     * and loads them into their views.
     * @param posterUri the uri to fetch the movie's poster
     * @param backdropUri the uri to fetch the movie's backdrop
     */
    private void setImage(String posterUri, String backdropUri) {
        String baseUri = "https://image.tmdb.org/t/p/w500";
        String posterThumb = baseUri + posterUri;
        Picasso.with(this)
                .load(posterThumb)
                .into(mPosterThumb);
        String backdrop = baseUri + backdropUri;
        Picasso.with(this)
                .load(backdrop)
                .into(mBackdrop);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.color_scheme, menu);
        return true;
    }

    //TODO: Move color scheme choice to preferences
    /**
     * Allows the user to toggle between a light and a dark theme.
     * @param item the selected menu item
     * @return true or inherit from parent
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FrameLayout background =
                (FrameLayout) findViewById(R.id.background_color);
        switch (item.getItemId()) {
            case R.id.dark_scheme:
                background.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.colorBackground));
                mReleaseDate.setTextColor(ContextCompat.getColor(this,
                        R.color.colorText));
                mRating.setTextColor(ContextCompat.getColor(this,
                        R.color.colorText));
                mSynopsis.setTextColor(ContextCompat.getColor(this,
                        R.color.colorText));
                return true;
            case R.id.light_scheme:
                background.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.colorText));
                mReleaseDate.setTextColor(ContextCompat.getColor(this,
                        R.color.colorBackground));
                mRating.setTextColor(ContextCompat.getColor(this,
                        R.color.colorBackground));
                mSynopsis.setTextColor(ContextCompat.getColor(this,
                        R.color.colorBackground));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
