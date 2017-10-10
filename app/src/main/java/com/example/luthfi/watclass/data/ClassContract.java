package com.example.luthfi.watclass.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Luthfi on 14/8/2016.
 * This class provides a Data Contract for main table.
 */
public final class ClassContract {

    // Empty constructor.
    private ClassContract() {}

    public static final String AUTHORITY = "com.example.luthfi.watclass.data.ClassProvider";
    // content://com.example.luthfi.watclass.data.ClassProvider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_CLASS = "classes";

    public static final class ClassEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_CLASS).build();
        // vnd.android.cursor.dir/com.example.luthfi.watclass.data.ClassProvider/classes
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                AUTHORITY + "/" + PATH_CLASS;
        // vnd.android.cursor.item/com.example.luthfi.watclass.data.ClassProvider/item
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                AUTHORITY + "/" + PATH_CLASS;

        // Table name.
        public static final String TABLE_NAME = "CLASS";

        // Name of data fields/attributes in the table.
        public static final String COLUMN_TERM = "TERM";
        public static final String COLUMN_SUBJECT = "SUBJECT";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_TITLE = "TITLE";
        public static final String COLUMN_INSTRUCTOR = "INSTRUCTOR";
        public static final String COLUMN_LECTURE = "LEC";
        public static final String COLUMN_TUTORIAL = "TUT";

        // TODO: The storage of grading components may need to be redesigned.
        // Currently it is easier to store everything as JSON due to the
        // variable number of grading components. For personal use, this is
        // manageable.
        public static final String COLUMN_GRADING = "GRADING";
        public static final String COLUMN_WEIGHTS = "WEIGHTS";
        public static final String COLUMN_MARKS = "MARKS";

        public static final String COLUMN_DATE_MODIFIED = "date modified";
    }
}
