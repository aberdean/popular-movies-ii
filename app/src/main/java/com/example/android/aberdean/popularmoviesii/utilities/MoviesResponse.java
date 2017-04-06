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

package com.example.android.aberdean.popularmoviesii.utilities;

import com.example.android.aberdean.popularmoviesii.models.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoviesResponse {
    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public List<Movie> getResults() {
        return movies;
    }
}
