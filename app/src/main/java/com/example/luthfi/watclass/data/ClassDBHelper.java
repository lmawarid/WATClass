package com.example.luthfi.watclass.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.DropBoxManager;

/**
 * Created by Luthfi on 14/8/2016.
 * This class manages the creation and upgrade of the
 * database holding the main data table.
 */
public class ClassDBHelper extends SQLiteOpenHelper {

    /***
     * Constants
     */
    // Database name and version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "classes.db";

    /***
     * Constructor
     */
    public ClassDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /***
     * Creates the Open Helper. This creates the main table for the application
     * and inserts a test value (a tuple).
     * @param sqLiteDatabase    The database to hold the table.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a unique ID for the table.
        // SQL command to create table of classes.
        final String SQL_CREATE_CLASS_TABLE = "CREATE TABLE "
                + ClassContract.ClassEntry.TABLE_NAME + "("
                + ClassContract.ClassEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ClassContract.ClassEntry.COLUMN_TERM + " INTEGER NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_SUBJECT + " TEXT NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_ID + " INTEGER NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_INSTRUCTOR + " TEXT NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_LECTURE + " INTEGER,"
                + ClassContract.ClassEntry.COLUMN_TUTORIAL + " INTEGER,"
                + ClassContract.ClassEntry.COLUMN_GRADING + " TEXT NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_WEIGHTS + " TEXT NOT NULL,"
                + ClassContract.ClassEntry.COLUMN_MARKS + " TEXT NOT NULL"
                + ");";

        // Execute command to create the table.
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);

        // Insert a test value into the database.
        sqLiteDatabase.execSQL("INSERT INTO " + ClassContract.ClassEntry.TABLE_NAME + "("
                + ClassContract.ClassEntry.COLUMN_TERM + ","
                + ClassContract.ClassEntry.COLUMN_SUBJECT + ","
                + ClassContract.ClassEntry.COLUMN_ID + ","
                + ClassContract.ClassEntry.COLUMN_TITLE + ","
                + ClassContract.ClassEntry.COLUMN_INSTRUCTOR + ","
                + ClassContract.ClassEntry.COLUMN_LECTURE + ","
                + ClassContract.ClassEntry.COLUMN_TUTORIAL + ","
                + ClassContract.ClassEntry.COLUMN_GRADING + ","
                + ClassContract.ClassEntry.COLUMN_WEIGHTS + ","
                + ClassContract.ClassEntry.COLUMN_MARKS + ") "
                + "VALUES(1165,'PD',2,'Critical Reflection & Report Writing',"
                + "'Jay Dolmage',81,NULL,'[]','[]', '{}');");
    }

    /***
     * Handles the upgrading of the underlying database.
     * @param sqLiteDatabase    The database holding the main table.
     * @param oldVersion        Old database version number.
     * @param newVersion        New database version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Update the database.
        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE " + ClassContract.ClassEntry.TABLE_NAME
                    + " ADD COLUMN " + ClassContract.ClassEntry.COLUMN_DATE_MODIFIED
                    + " TEXT");
        }
    }
}
