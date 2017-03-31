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

package com.example.android.aberdean.popularmoviesi;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.aberdean.popularmoviesi.utilities.MovieJsonUtils;
import com.example.android.aberdean.popularmoviesi.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Presents the user with a detailed view of the chosen movie,
 * including a backdrop picture, a thumbnail poster, the original title,
 * release date and rating, and a synopsis.
 * Allows the user to choose between a dark and light theme.
 */
public class MovieDetails extends AppCompatActivity {

    private String mId;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    private ImageView mBackdrop;
    private ImageView mPosterThumb;

    private TextView mSynopsis;
    private TextView mReleaseDate;
    private TextView mRating;

    /**
     * Assigns the appropriate values for the chosen movie.
     * @param savedInstanceState the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        RecyclerView mReviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        RecyclerView mTrailers = (RecyclerView) findViewById(R.id.recyclerview_trailers);

        mBackdrop = (ImageView) findViewById(R.id.iv_backdrop);
        mPosterThumb = (ImageView) findViewById(R.id.iv_poster_thumb);

        mSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        TextView mOriginalTitle = (TextView) findViewById(R.id.tv_title);
        mRating = (TextView) findViewById(R.id.tv_rating);

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

        fetchReviews();

    }

    private void fetchReviews() {
        new ReviewQueryTask().execute(mId);
    }

    public class ReviewQueryTask extends
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
