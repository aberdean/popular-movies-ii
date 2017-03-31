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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.aberdean.popularmoviesi.utilities.MovieJsonUtils;
import com.example.android.aberdean.popularmoviesi.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

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

    private MovieAdapter mMovieAdapter;

    private String[][] mJsonMovieData;
    private String sortBy = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView)
                findViewById(R.id.recyclerview_posters);

        GridLayoutManager layoutManager =
                new GridLayoutManager(this, getResources()
                        .getInteger(R.integer.num_of_columns));

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        Toast.makeText(getApplicationContext(),
                getString(R.string.attrib), Toast.LENGTH_SHORT).show();

        fetchPosters();

    }

    private void fetchPosters() {
        new MovieQueryTask().execute(sortBy);
    }

    /**
     * When a movie poster is clicked, starts the MovieDetails activity.
     * @param moviePosition the position of the movie in the ArrayList
     */
    public void onClick(int moviePosition) {
        Class destinationClass = MovieDetails.class;
        Intent intentToStartMovieDetails = new Intent(this, destinationClass);
        ArrayList movieDetails = getDetails(moviePosition);
        intentToStartMovieDetails.putExtra("movieDetails", movieDetails);
        startActivity(intentToStartMovieDetails);
    }

    private ArrayList getDetails(int position) {
        ArrayList<String> chosenMovie =
                new ArrayList<>(mJsonMovieData.length);

        for (String[] movies : mJsonMovieData) {
            chosenMovie.add(movies[position]);
        }
        return chosenMovie;
    }

    public class MovieQueryTask extends
            AsyncTask<String, String[], String[][]> {

        @Override
        protected String[][] doInBackground(String... params) {
            String sortOrder = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(sortOrder);
            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                mJsonMovieData = MovieJsonUtils
                        .getMovieStringsFromJson(MainActivity.this,
                                jsonMovieResponse);

                return mJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[][] movieData) {
            if (movieData != null) {
                String[] posterData = movieData[0];
                ArrayList<String> mPosterUris =
                        new ArrayList<>(posterData.length);
                for (String posterUri : posterData) {
                    String basePosterUri =
                            "https://image.tmdb.org/t/p/w500";
                    mPosterUris.add(basePosterUri + posterUri);
                }
                mMovieAdapter.setPosterData(mPosterUris);
            }
        }
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
                sortBy = "top_rated";
                fetchPosters();
                return true;
            case R.id.sort_by_most_popular:
                sortBy = "popular";
                fetchPosters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
