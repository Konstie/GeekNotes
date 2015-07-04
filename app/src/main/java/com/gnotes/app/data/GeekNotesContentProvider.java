package com.gnotes.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.sql.SQLException;

public class GeekNotesContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GeekNotesDbHelper mGNdbHelper;
    private GeekNotesDbHelper.DBHelper openHelper;

    static final int GEEKNOTE = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GeekNotesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, GeekNotesContract.PATH_NOTE, GEEKNOTE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mGNdbHelper = new GeekNotesDbHelper(getContext());
        openHelper = mGNdbHelper.getCoreDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case GEEKNOTE:
                retCursor = openHelper.getReadableDatabase().query(
                        GeekNotesContract.GeekEntry.TABLE_NAME,
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
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case GEEKNOTE:
                return GeekNotesContract.GeekEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown operation");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case GEEKNOTE:
                long _id = db.insert(GeekNotesContract.GeekEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = GeekNotesContract.GeekEntry.buildGeekNotesURI(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri + ", id = " + _id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection)
            selection = "1";

        switch (match) {
            case GEEKNOTE:
                rowsDeleted = db.delete(
                        GeekNotesContract.GeekEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown operation!");
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case GEEKNOTE:
                rowsUpdated = db.update(GeekNotesContract.GeekEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown operation!");
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        openHelper.close();
        super.shutdown();
    }
}
