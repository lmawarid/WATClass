package com.example.luthfi.watclass.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Luthfi on 14/8/2016.
 */
public final class ClassProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int CLASS = 100;
    private static final int CLASS_ID = 101;

    private ClassDBHelper openHelper;

    private static final SQLiteQueryBuilder queryBuilder;

    static {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ClassContract.ClassEntry.TABLE_NAME);
    }

    // Builds the URI matcher for the database.
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ClassContract.AUTHORITY;

        matcher.addURI(authority, ClassContract.PATH_CLASS, CLASS);
        matcher.addURI(authority, ClassContract.PATH_CLASS + "/#", CLASS_ID);
        return matcher;
    }

    // On create, initialize the database helper.
    @Override
    public boolean onCreate() {
        openHelper = new ClassDBHelper(getContext());
        return true;
    }

    // Query the database.
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        // Get the database. For queries, we only want it to be readable.
        SQLiteDatabase database = openHelper.getReadableDatabase();

        // Switch on the match code and execute the appropriate query.
        switch (uriMatcher.match(uri)) {
            case CLASS:
                returnCursor = database.query(
                        ClassContract.ClassEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CLASS_ID:
                returnCursor = database.query(
                        ClassContract.ClassEntry.TABLE_NAME,
                        projection,
                        ClassContract.ClassEntry._ID + "=" + ContentUris.parseId(uri),
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI to the return cursor and return it.
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    // Get the type of the matched URI.
    @Nullable
    @Override
    public String getType(Uri uri) {
        // Switch on the match code and return the appropriate type.
        switch (uriMatcher.match(uri)) {
            case CLASS:
                return ClassContract.ClassEntry.CONTENT_TYPE;
            case CLASS_ID:
                return ClassContract.ClassEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    // Insert a new class record into the database.
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Get database with write access for insertion.
        final SQLiteDatabase database = openHelper.getWritableDatabase();
        Uri returnUri;

        // Switch on the match code and do the appropriate action.
        // (Right now only adding a new class is valid).
        switch (uriMatcher.match(uri)) {
            case CLASS:
                long _id = database.insert(
                        ClassContract.ClassEntry.TABLE_NAME,
                        null,
                        contentValues
                        );
                if (_id > 0) returnUri = ContentUris.withAppendedId(
                        ClassContract.ClassEntry.CONTENT_URI,
                        _id
                        );
                else throw new SQLiteException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify change to the content resolver.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    // Delete a class record from the database.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get database with write access for deletion.
        final SQLiteDatabase database = openHelper.getWritableDatabase();
        int rowsDeleted;

        // Switch on the match code and execute the appropriate action.
        switch (uriMatcher.match(uri)) {
            case CLASS:
                rowsDeleted = database.delete(
                        ClassContract.ClassEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CLASS_ID:
                rowsDeleted = database.delete(
                        ClassContract.ClassEntry.TABLE_NAME,
                        ClassContract.ClassEntry._ID + "='"
                                + ContentUris.parseId(uri) + "'",
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If at least one class was deleted, update the content resolver.
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    // Update a class record in the database.
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        // Get database with write access for update.
        final SQLiteDatabase database = openHelper.getWritableDatabase();
        int rowsUpdated;

        // Switch on the match code and execute the appropriate action.
        switch (uriMatcher.match(uri)) {
            case CLASS:
                rowsUpdated = database.update(
                        ClassContract.ClassEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case CLASS_ID:
                rowsUpdated = database.update(
                        ClassContract.ClassEntry.TABLE_NAME,
                        contentValues,
                        ClassContract.ClassEntry._ID + "='"
                                + ContentUris.parseId(uri) + "'",
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If at least one class was updated, update the content resolver.
        if (selection == null || rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
