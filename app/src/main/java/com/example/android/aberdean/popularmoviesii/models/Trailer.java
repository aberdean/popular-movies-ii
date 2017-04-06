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

public class Trailer implements Parcelable {
    @SerializedName("key")
    private String mCode;
    @SerializedName("name")
    private String mName;

    private Trailer() {
    }

    public Trailer(String key, String name) {
        mCode = key;
        mName = name;
    }

    public String getTrailerThumbUrl(Context context) {
        return context.getResources().getString(R.string.base_trailer_thumb_uri)
                + mCode + context.getResources().getString(R.string.end_trailer_thumb_uri);
    }

    public String getTrailerUrl(Context context) {
        return context.getResources().getString(R.string.base_trailer_uri) + mCode;
    }

    public String getName() {
        return mName;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel source) {
            Trailer trailer = new Trailer();
            trailer.mCode = source.readString();
            trailer.mName = source.readString();
            return trailer;
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mCode);
    }
}
