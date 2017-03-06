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

package com.example.android.aberdean.popularmoviesi.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Utility functions to handle TMDb JSON data.
 */
public final class MovieJsonUtils {

    /**
     * Parse JSON from a TMDb response and return an array of Strings
     * describing a movie.
     *
     * @param context the context from which this method is called
     * @param movieJsonString JSON response from server
     *
     * @return Array of Strings describing a movie
     *
     * @throws JSONException If JSON data cannot be parsed
     */
    public static String[][] getMovieStringsFromJson(
            Context context, String movieJsonString) throws JSONException {

        /*
         * Movie information.
         * All info for a movie are children of the "results" array.
         */
        final String MJ_RESULTS = "results";

        /* Path to the poster image for the movie */
        final String MJ_POSTER = "poster_path";
        /* Path to backdrop image for the movie */
        final String MJ_BACKDROP = "backdrop_path";
        /* Movie description */
        final String MJ_OVERVIEW = "overview";
        final String MJ_RELEASE_DATE = "release_date";
        final String MJ_ORIGINAL_TITLE = "original_title";
        final String MJ_VOTE_AVERAGE = "vote_average";

        /* HTTP status codes */
        final String MJ_STATUS_CODE = "status_code";

        /* String arrays holding each type of data for all the movies */
        String[] parsedPosterUri;
        String[] parsedBackdropUri;
        String[] parsedDescription;
        String[] parsedReleaseDate;
        String[] parsedTitle;
        String[] parsedRating;

        JSONObject movieJson = new JSONObject(movieJsonString);

        /* Handle connectivity errors */
        if (movieJson.has(MJ_STATUS_CODE)) {
            int errorCode = movieJson.getInt(MJ_STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(MJ_RESULTS);

        parsedPosterUri = new String[movieArray.length()];
        parsedBackdropUri = new String[movieArray.length()];
        parsedDescription = new String[movieArray.length()];
        parsedReleaseDate = new String[movieArray.length()];
        parsedTitle = new String[movieArray.length()];
        parsedRating = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            /* Values to be collected */
            String posterPath;
            String backdropPath;
            String description;
            String releaseDate;
            String title;
            String rating;

            /* Get the JSON object representing the movie */
            JSONObject movieData = movieArray.getJSONObject(i);

            posterPath = movieData.getString(MJ_POSTER);
            backdropPath = movieData.getString(MJ_BACKDROP);
            description = movieData.getString(MJ_OVERVIEW);
            releaseDate = movieData.getString(MJ_RELEASE_DATE);
            title = movieData.getString(MJ_ORIGINAL_TITLE);
            rating = movieData.getString(MJ_VOTE_AVERAGE);

            parsedPosterUri[i] = posterPath;
            parsedBackdropUri[i] = backdropPath;
            parsedDescription[i] = description;
            parsedReleaseDate[i] = releaseDate;
            parsedTitle[i] = title;
            parsedRating[i] = rating;

        }

        // variable parsedMovieData is needed to format the data
        String[][] parsedMovieData = {parsedPosterUri, parsedBackdropUri,
                parsedDescription, parsedReleaseDate,
                parsedTitle, parsedRating};

        return parsedMovieData;
    }
}
