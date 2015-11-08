package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Represent the movie fetched from the API and passed between intents
 */
public class Movie implements Parcelable {
    public Long id;

    @SerializedName("poster_path")
    public String posterPath;

    public String title;
    public String overview;

    @SerializedName("vote_average")
    public String voteAverage;

    @SerializedName("release_date")
    public String releaseDate;

    public boolean favorited;

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
        out.writeByte((byte) (favorited ? 1 : 0));
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
        favorited = in.readByte() != 0;
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
}
