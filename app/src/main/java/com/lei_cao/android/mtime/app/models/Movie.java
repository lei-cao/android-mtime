package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represent the movie fetched from the API and passed between intents
 */
public class Movie implements Parcelable {
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
        posterPath = in.readString();
        title = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public Movie() {
    }

    public String getImageUrl() {
        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String WIDTH = "w185";
        return IMAGE_BASE_URL + WIDTH + posterPath;
    }
}
