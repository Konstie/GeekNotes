package com.gnotes.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class GeekNotesContract {
    public static final String CONTENT_AUTHORITY = "com.gnotes.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_NOTE = "geeknote";

    public static final class GeekEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOTE)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        public static final String TABLE_NAME = "notes_table";

        public static final int VERSION = 1;
        public static final String COLUMN_NOTE_ID = "note_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_INFOTYPE = "infotype";
        public static final String COLUMN_INFO = "fieldinfo";

        public static final String COLUMN_TRANSLATION = "translation";

        public static final String COLUMN_ARTICLE_INFO = "article_info";
        public static final String COLUMN_ARTICLE_RANK = "rank";
        public static final String COLUMN_ARTICLE_IMDB_INFO = "imdb_info";

        public static final String COLUMN_ARTICLE_POSTERLINK = "poster";

        public static Uri buildGeekNotesURI(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
