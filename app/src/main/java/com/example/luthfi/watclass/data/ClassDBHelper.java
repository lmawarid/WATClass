package com.example.luthfi.watclass.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Luthfi on 14/8/2016.
 */
public class ClassDBHelper extends SQLiteOpenHelper {

    // Database name and version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "classes.db";

    // Database constructor.
    public ClassDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Initializes the database.
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
                + ClassContract.ClassEntry.COLUMN_TUTORIAL + " INTEGER"
                + ");";

        // Execute command.
        sqLiteDatabase.execSQL(SQL_CREATE_CLASS_TABLE);

        // Test class.
        // TODO // Is there a way to insert data to all columns except the auto-incremented
        // TODO // primary key?
        sqLiteDatabase.execSQL("INSERT INTO " + ClassContract.ClassEntry.TABLE_NAME + "("
                + ClassContract.ClassEntry.COLUMN_TERM + ","
                + ClassContract.ClassEntry.COLUMN_SUBJECT + ","
                + ClassContract.ClassEntry.COLUMN_ID + ","
                + ClassContract.ClassEntry.COLUMN_TITLE + ","
                + ClassContract.ClassEntry.COLUMN_INSTRUCTOR + ","
                + ClassContract.ClassEntry.COLUMN_LECTURE + ","
                + ClassContract.ClassEntry.COLUMN_TUTORIAL + ")"
                + "VALUES(1165,'PD',2,'Critical Reflection & Report Writing',"
                + "'Jay Dolmage',81,NULL);");
    }

    // Handles database upgrades.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO: Log database upgrade.

        // Update the database.
        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE " + ClassContract.ClassEntry.TABLE_NAME
                    + " ADD COLUMN " + ClassContract.ClassEntry.COLUMN_DATE_MODIFIED
                    + " TEXT");
        }
    }
}
