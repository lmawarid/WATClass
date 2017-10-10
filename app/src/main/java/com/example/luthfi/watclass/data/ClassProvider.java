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
 * This class provides a Content Provider for the UI layer
 * that retrieves data from the main database.
 */
public final class ClassProvider extends ContentProvider {

    /***
     * Constants
     */
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final int CLASS = 100;
    private static final int CLASS_ID = 101;
    private static final SQLiteQueryBuilder queryBuilder;
    static {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(ClassContract.ClassEntry.TABLE_NAME);
    }

    /***
     * Private
     */
    private ClassDBHelper openHelper;

    /***
     * Creates the URI matcher for the Content Provider.
     * @return  A URI matcher.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ClassContract.AUTHORITY;

        matcher.addURI(authority, ClassContract.PATH_CLASS, CLASS);
        matcher.addURI(authority, ClassContract.PATH_CLASS + "/#", CLASS_ID);
        return matcher;
    }

    /***
     * Creates the Content Provider.
     */
    @Override
    public boolean onCreate() {
        openHelper = new ClassDBHelper(getContext());
        return true;
    }

    /***
     * Queries the database.
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        // Get the database. For queries, we only want read access.
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

    /***
     * Get the type of the matched URI.
     * @param uri   The URI.
     * @return      Type of matched URI as String.
     */
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

    /***
     * Inserts a new tuple into the database.
     * @param uri               The database URI.
     * @param contentValues     The values to be inserted.
     * @return
     */
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

    /***
     * Deletes a tuple from the database.
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
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
                        ClassContract.ClassEntry._ID + "='" + ContentUris.parseId(uri) + "'",
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

    /***
     * Updates a tuple (in most cases, a class record) in the database.
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
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