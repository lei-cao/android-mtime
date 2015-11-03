package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represent the movie fetched from the API and passed between intents
 */
public class Movie implements Parcelable {
    public Long id;
    public String posterPath;
    public String title;
    public String overview;
    public String voteAverage;
    public String releaseDate;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(posterPath);
        out.writeString(title);
        out.writeString(overview);
        out.writeString(voteAverage);
        out.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(Parcel in) {
        id = in.readLong();
        posterPath = in.readString();
        title = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public Movie() {
    }

    public String getListUrl() {
        final String width = "w185";
        return getImageUrl(width);
    }

    public String getDetailUrl() {
        final String width = "w342";
        return getImageUrl(width);
    }

    private String getImageUrl(String width) {
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        return IMAGE_BASE_URL + width + posterPath;
    }

    public static ArrayList<Movie> parseMovieJson(String movieJsonStr) throws JSONException {
        final String RESULTS = "results";
        final String ID = "id";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";

        ArrayList<Movie> movies = new ArrayList<Movie>();
        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray movieArray = movieJson.getJSONArray(RESULTS);
        for (int i = 0; i < movieArray.length(); i++) {
            Movie movie = new Movie();
            JSONObject movieObj = movieArray.getJSONObject(i);
            movie.id = movieObj.getLong(ID);
            movie.posterPath = movieObj.getString(POSTER_PATH);
            movie.title = movieObj.getString(TITLE);
            movie.overview = movieObj.getString(OVERVIEW);
            movie.voteAverage = movieObj.getString(VOTE_AVERAGE);
            movie.releaseDate = movieObj.getString(RELEASE_DATE);
            movies.add(movie);
        }
        return movies;
    }
}
