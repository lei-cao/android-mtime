package com.lei_cao.android.mtime.app.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.lei_cao.android.mtime.app.helpers.MTimeSqliteHelper;
import com.lei_cao.android.mtime.app.models.Movie;

import java.util.ArrayList;

public class MoviesDAO {
    private SQLiteDatabase db;
    private MTimeSqliteHelper dbHelper;

    private String[] allColumns = {
            MTimeSqliteHelper.COLUMN_ID,
            MTimeSqliteHelper.COLUMN_POStER_PATH,
            MTimeSqliteHelper.COLUMN_TITLE,
            MTimeSqliteHelper.COLUMN_OVERVIEW,
            MTimeSqliteHelper.COLUMN_VOTE_AVERAGE,
            MTimeSqliteHelper.COLUMN_RELEASE_DATE
    };

    public MoviesDAO(Context context) {
        dbHelper = new MTimeSqliteHelper(context);
    }

    public boolean open() {
        db = dbHelper.getWritableDatabase();
        return true;
    }

    public void close() {
        dbHelper.close();
    }

    public Movie createMovie(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MTimeSqliteHelper.COLUMN_ID, movie.id);
        values.put(MTimeSqliteHelper.COLUMN_POStER_PATH, movie.posterPath);
        values.put(MTimeSqliteHelper.COLUMN_TITLE, movie.title);
        values.put(MTimeSqliteHelper.COLUMN_OVERVIEW, movie.overview);
        values.put(MTimeSqliteHelper.COLUMN_VOTE_AVERAGE, movie.voteAverage);
        values.put(MTimeSqliteHelper.COLUMN_RELEASE_DATE, movie.releaseDate);
        try {
            db.insertOrThrow(MTimeSqliteHelper.TABLE_MOVIES, null, values);
        } catch (SQLiteConstraintException e) {
        } catch (Exception e) {
            return null;
        }
        movie.favorited = true;
        return movie;
    }

    public void deleteMovie(Movie movie) {
        long id = movie.id;
        db.delete(MTimeSqliteHelper.TABLE_MOVIES, MTimeSqliteHelper.COLUMN_ID + " = " + id, null
        );
    }

    public Movie getMovie(long id) {
        Cursor c = db.query(MTimeSqliteHelper.TABLE_MOVIES, allColumns, MTimeSqliteHelper.COLUMN_ID + " = " + id, null, null
                , null, null);
        if (c.moveToFirst()) {
            Movie m = cursorToMovie(c);
            m.favorited = true;
            c.close();
            return m;
        }
        c.close();
        return null;
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movies = new ArrayList<>();

        Cursor c = db.query(MTimeSqliteHelper.TABLE_MOVIES, allColumns, null, null, null, null, null);

        c.moveToFirst();
        while (!c.isAfterLast()) {
            Movie m = cursorToMovie(c);
            movies.add(m);
            c.moveToNext();
            m.favorited = true;
        }

        c.close();
        return movies;
    }

    private Movie cursorToMovie(Cursor c) {
        Movie m = new Movie();
        m.id = c.getLong(0);
        m.posterPath = c.getString(1);
        m.title = c.getString(2);
        m.overview = c.getString(3);
        m.voteAverage = c.getString(4);
        m.releaseDate = c.getString(5);
        return m;
    }
}
