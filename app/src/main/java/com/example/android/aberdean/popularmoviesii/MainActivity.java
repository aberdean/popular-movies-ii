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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.aberdean.popularmoviesii.models.Movie;
import com.example.android.aberdean.popularmoviesii.utilities.MoviesDbService;
import com.example.android.aberdean.popularmoviesii.utilities.MoviesResponse;
import com.example.android.aberdean.popularmoviesii.utilities.NetworkClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Shows a list of popular movies' posters organized in a grid view.
 * Allows the user to select sorting by popularity or by rating.
 * Start activity MovieDetails when the user clicks on one
 * of the movies' poster.
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler {

    /**
     * Suppressing "unused" warnings, because TAG is not used at the moment,
     * but it's still useful to leave it here.
     */
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    // The API key must be set in a variable called ApiKey
    // within the gradle.properties file
    private static final String API_KEY = BuildConfig.MOVIE_API_KEY;

    @BindView(R.id.recyclerview_posters) RecyclerView mRecyclerView;

    private MovieAdapter mMovieAdapter;

    private List<Movie> movies;
    private String sortBy = "popular";

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.popular_title);

        GridLayoutManager layoutManager =
                new GridLayoutManager(this, getResources()
                        .getInteger(R.integer.num_of_columns));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getString("sort_by");
            if (savedInstanceState.containsKey("poster_uris")) {
                ArrayList posterUris = savedInstanceState.getParcelableArrayList("poster_uris");
                mMovieAdapter.setPosterData(posterUris);
            }
        } else {
            fetchPosters(sortBy);
        }

        Toast.makeText(getApplicationContext(),
                getString(R.string.attrib), Toast.LENGTH_SHORT).show();

    }

    private void fetchPosters(String sortBy) {
        MoviesDbService service = NetworkClient.getClient().create(MoviesDbService.class);
        Call<MoviesResponse> call = null;
        if (sortBy.equals("popular")) {
            call = service.getPopularMovies(BuildConfig.MOVIE_API_KEY);
        } else if (sortBy.equals("top_rated")) {
            call = service.getTopRatedMovies(BuildConfig.MOVIE_API_KEY);
        }
        if (call != null) {
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    movies = response.body().getResults();
                    ArrayList<String> mPosterUris = new ArrayList<>(movies.size());
                    for (Movie movie : movies) {
                        mPosterUris.add(movie.getPosterUri(getApplicationContext()));
                    }
                    mMovieAdapter.setPosterData(mPosterUris);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    /**
     * When a movie poster is clicked, starts the MovieDetails activity.
     * @param position the position of the movie
     */
    public void onClick(int position) {
        Class destinationClass = MovieDetails.class;
        Intent intentToStartMovieDetails = new Intent(this, destinationClass);
        intentToStartMovieDetails.putExtra("movie", movies.get(position));
        startActivity(intentToStartMovieDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sorting_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_by_top_rated:
                getSupportActionBar().setTitle(R.string.top_rated_title);
                sortBy = "top_rated";
                fetchPosters(sortBy);
                return true;
            case R.id.sort_by_most_popular:
                getSupportActionBar().setTitle(R.string.popular_title);
                sortBy = "popular";
                fetchPosters(sortBy);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        ArrayList posterUris = mMovieAdapter.getPosterUris();
        if (posterUris != null && !posterUris.isEmpty()) {
            outstate.putParcelableArrayList("poster_uris", posterUris);
        }
        outstate.putString("sort_by", sortBy);
    }

}
