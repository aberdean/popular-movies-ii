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
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.aberdean.popularmoviesii.models.Movie;
import com.example.android.aberdean.popularmoviesii.models.Review;
import com.example.android.aberdean.popularmoviesii.models.Trailer;
import com.example.android.aberdean.popularmoviesii.utilities.MoviesDbService;
import com.example.android.aberdean.popularmoviesii.utilities.ReviewsResponse;
import com.example.android.aberdean.popularmoviesii.utilities.NetworkClient;
import com.example.android.aberdean.popularmoviesii.utilities.TrailersResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private Integer mId;

    private List<Trailer> trailers;

    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

    @BindView(R.id.toolbar) Toolbar toolbar;

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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("movie")) {

            Movie mChosenMovie = intent.getParcelableExtra("movie");

            Resources res = getResources();

            String posterUri = mChosenMovie.getPosterUri(this);
            Picasso.with(this)
                    .load(posterUri)
                    .into(mPosterThumb);
            String backdropUri = mChosenMovie.getBackdropUri(this);
            Picasso.with(this)
                    .load(backdropUri)
                    .into(mBackdrop);

            String synopsis = mChosenMovie.getDescription();
            mSynopsis.setText(synopsis);

            String releaseDate = mChosenMovie.getReleaseDate();
            String release = String.format(
                    res.getString(R.string.released), releaseDate);
            mReleaseDate.setText(release);

            String title = mChosenMovie.getTitle();
            mOriginalTitle.setText(title);
            getSupportActionBar().setTitle(title);

            Double rating = mChosenMovie.getRating();
            String rate = String.format(
                    res.getString(R.string.rating), rating.toString());
            mRating.setText(rate);

            mId = mChosenMovie.getId();
        }

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mReviews.setLayoutManager(reviewLayoutManager);
        mReviews.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mReviews.setAdapter(mReviewAdapter);

        GridLayoutManager trailerLayoutManager =
                new GridLayoutManager(this, getResources()
                        .getInteger(R.integer.num_of_columns));

        mTrailers.setLayoutManager(trailerLayoutManager);
        mTrailers.setHasFixedSize(true);

        mTrailerAdapter = new TrailerAdapter(this);
        mTrailers.setAdapter(mTrailerAdapter);

        MoviesDbService service = NetworkClient.getClient().create(MoviesDbService.class);
        Call<ReviewsResponse> reviewCall = service.getReviews(mId, BuildConfig.MOVIE_API_KEY);

        if (reviewCall != null) {
            reviewCall.enqueue(new Callback<ReviewsResponse>() {
                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    List<Review> reviewData = response.body().getResults();
                    mReviewAdapter.setReviewData(reviewData);
                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }

        Call<TrailersResponse> trailerCall = service.getTrailers(mId, BuildConfig.MOVIE_API_KEY);

        if (trailerCall != null) {
            trailerCall.enqueue(new Callback<TrailersResponse>() {
                @Override
                public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                    trailers = response.body().getResults();
                    mTrailerAdapter.setTrailerData(trailers);
                }

                @Override
                public void onFailure(Call<TrailersResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    /**
     * When a trailer thumbnail is clicked, starts an implicit intent to launch
     * the trailer either on a YouTube app or on a browser.
     * @param trailerPosition the position of the trailer in the ArrayList
     */
    @Override
    public void onClick(int trailerPosition) {
        Trailer trailer = trailers.get(trailerPosition);
        Intent intentToStartTrailer = new Intent(ACTION_VIEW, Uri.parse(trailer.getTrailerUrl(this)));
        startActivity(intentToStartTrailer);
    }

}
