package com.example.luthfi.watclass.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Luthfi on 14/8/2016.
 */
public final class ClassContract {
    // This class provides a contract for the ClassProvider.

    // Empty constructor.
    private ClassContract() {

    }

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

        // Table details.
        public static final String TABLE_NAME = "class";

        public static final String COLUMN_TERM = "term";
        public static final String COLUMN_SUBJECT = "subject";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_INSTRUCTOR = "instructor";
        public static final String COLUMN_LECTURE = "LEC";
        public static final String COLUMN_TUTORIAL = "TUT";
        // COLUMN_TEST yet to be added.

        public static final String COLUMN_DATE_MODIFIED = "date modified";
    }
}
