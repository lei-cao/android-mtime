package com.lei_cao.android.mtime.app.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MTimeSqliteHelper extends SQLiteOpenHelper {

    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_POStER_PATH = "poster_path";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
    public static final String COLUMN_RELEASE_DATE = "releaseDate";

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " +
            TABLE_MOVIES +
            "(" +
            COLUMN_ID + " integer primary key, " +
            COLUMN_POStER_PATH + " text, " +
            COLUMN_TITLE + " text, " +
            COLUMN_OVERVIEW + " text, " +
            COLUMN_VOTE_AVERAGE + " text, " +
            COLUMN_RELEASE_DATE + " text " +
            ");";

    public MTimeSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MTimeSqliteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }
}
