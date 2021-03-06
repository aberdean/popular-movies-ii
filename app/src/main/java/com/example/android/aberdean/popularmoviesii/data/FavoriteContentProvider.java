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

package com.example.android.aberdean.popularmoviesii.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.aberdean.popularmoviesii.data.FavoriteContract.FavoriteEntry.TABLE_NAME;

@SuppressWarnings("ConstantConditions")
public class FavoriteContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private FavoriteDbHelper mFavoriteDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavoriteDbHelper = new FavoriteDbHelper(context);

        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri = null;

        switch(match) {
            case FAVORITES:
                try {
                    long id = db.insert(TABLE_NAME, null,  values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(FavoriteContract
                                .FavoriteEntry.CONTENT_URI, id);
                    }
                } catch (SQLiteConstraintException e) {
                    /*
                     * It's fine, it means the user wanted to delete it from the favorites,
                     * since the database entries have a UNIQUE constraint to avoid adding
                     * the same movie twice.
                     * Reset returnUri, just to make sure not to return garbage (or better,
                     * just not to leave the catch clause empty!).
                     */
                    returnUri = null;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mFavoriteDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor cursor;

        switch(match) {
            case FAVORITES:
                cursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mFavoriteDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch(match) {

            case FAVORITE_WITH_ID:

                String dbId = uri.getPathSegments().get(1);

                moviesDeleted = db.delete(TABLE_NAME, "id=?", new String[]{dbId});

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (moviesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
