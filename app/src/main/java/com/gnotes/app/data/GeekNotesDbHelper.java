package com.gnotes.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.gnotes.app.Item;

import java.util.ArrayList;
import java.util.Arrays;

public class GeekNotesDbHelper {
    private static final int DB_VERSION = 1;

    private static final ArrayList<String> filterCats = new ArrayList<>(
            Arrays.asList("Все", "Книга", "Фильм", "Сериал",
                    "Мультсериал", "Мультфильм", "Муз. исполнитель",
                    "Игра", "Комикс", "Аниме")
    );

    static final String DB_NAME = "geeknotes.db";

    private SQLiteDatabase database;

    private DBHelper dbHelper;

    public GeekNotesDbHelper(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public DBHelper getCoreDatabase() {
        return dbHelper;
    }

    public void insertData(String title, String category, String extraField, String extraInfo) {
        ContentValues cv = new ContentValues();
        cv.put(GeekNotesContract.GeekEntry.COLUMN_TITLE, title);
        cv.put(GeekNotesContract.GeekEntry.COLUMN_CATEGORY, category);
        cv.put(GeekNotesContract.GeekEntry.COLUMN_INFOTYPE, extraField);
        cv.put(GeekNotesContract.GeekEntry.COLUMN_INFO, extraInfo);
        database.insert(GeekNotesContract.GeekEntry.TABLE_NAME, null, cv);
    }

    public void insertData(String table, String nullColumnHack, ContentValues values) {
        database.insert(table, nullColumnHack, values);
    }

    public void updateData(String sourceTitle, String newTitle, String newExtra) {
        ContentValues cv = new ContentValues();
        cv.put(GeekNotesContract.GeekEntry.COLUMN_TITLE, newTitle);
        cv.put(GeekNotesContract.GeekEntry.COLUMN_INFO, newExtra);
        database.update(GeekNotesContract.GeekEntry.TABLE_NAME, cv, GeekNotesContract.GeekEntry.COLUMN_TITLE + " = ?",
                new String[] {sourceTitle});
    }

    public Cursor getAllData() {
        String buildSQL = "SELECT * FROM " + GeekNotesContract.GeekEntry.TABLE_NAME + " ORDER BY "
                + GeekNotesContract.GeekEntry._ID + " DESC";
        return database.rawQuery(buildSQL, null);
    }

    public Cursor getSpecificNote(String title) {
        String buildSQL = "SELECT * FROM " + GeekNotesContract.GeekEntry.TABLE_NAME + " WHERE " +
                GeekNotesContract.GeekEntry.COLUMN_TITLE + " = ?";
        return database.rawQuery(buildSQL, new String[] {title});
    }

    public Cursor getItemsByCategory(String categoryName) {
        String buildSQL = "SELECT * FROM " + GeekNotesContract.GeekEntry.TABLE_NAME + " WHERE TRIM("
                + GeekNotesContract.GeekEntry.COLUMN_CATEGORY + ") = '" + categoryName.trim() + "' ORDER BY " + GeekNotesContract.GeekEntry._ID + " DESC";
        return database.rawQuery(buildSQL, null);
    }

    public void deleteByID(int position) {
        database.delete(GeekNotesContract.GeekEntry.TABLE_NAME, GeekNotesContract.GeekEntry._ID + " = " + (position), null);
    }

    public void deleteByTitle(String title) {
        String[] whereArgs = new String[]{title};
        database.delete(GeekNotesContract.GeekEntry.TABLE_NAME, GeekNotesContract.GeekEntry.COLUMN_TITLE + " = ?", whereArgs);
    }

    public class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + GeekNotesContract.GeekEntry.TABLE_NAME + " (" +
                    GeekNotesContract.GeekEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                    GeekNotesContract.GeekEntry.COLUMN_TITLE + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_CATEGORY + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_INFOTYPE + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_INFO + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_ARTICLE_INFO + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_ARTICLE_RANK + " REAL, " +
                    GeekNotesContract.GeekEntry.COLUMN_ARTICLE_IMDB_INFO + " TEXT, " +
                    GeekNotesContract.GeekEntry.COLUMN_ARTICLE_POSTERLINK + " TEXT);";

            database.execSQL(SQL_CREATE_NOTES_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + GeekNotesContract.GeekEntry.TABLE_NAME);
            onCreate(database);
        }
    }
}
