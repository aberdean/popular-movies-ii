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

package com.example.android.aberdean.popularmoviesii.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.aberdean.popularmoviesii.R;
import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("overview")
    private String mDescription;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("id")
    private Integer mId;
    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("vote_average")
    private Double mRating;

    private Movie() {
    }

    public Movie(String posterPath, String description, String releaseDate, Integer id,
                 String title, String backdropPath, Double rating) {
        mPosterPath = posterPath;
        mDescription = description;
        mReleaseDate = releaseDate;
        mId = id;
        mTitle = title;
        mBackdropPath = backdropPath;
        mRating = rating;
    }

    public String getPosterUri(Context context) {
        return context.getResources().getString(R.string.poster_base_uri) + mPosterPath;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public Integer getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBackdropUri(Context context) {
        return context.getResources().getString(R.string.poster_base_uri) + mBackdropPath;
    }

    public Double getRating() {
        return mRating;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            Movie movie = new Movie();
            movie.mPosterPath = source.readString();
            movie.mDescription = source.readString();
            movie.mReleaseDate = source.readString();
            movie.mId = source.readInt();
            movie.mTitle = source.readString();
            movie.mBackdropPath = source.readString();
            movie.mRating = source.readDouble();
            return movie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPosterPath);
        dest.writeString(mDescription);
        dest.writeString(mReleaseDate);
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mBackdropPath);
        dest.writeDouble(mRating);
    }
}
