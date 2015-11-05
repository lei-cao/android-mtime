package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    public String id;
    public String key;
    public String name;
    public String site;
    public String size;
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(key);
        out.writeString(name);
        out.writeString(site);
        out.writeString(size);
        out.writeString(type);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Video(Parcel in) {
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readString();
        type = in.readString();
    }

    public Video() {

    }

    public String GetVideoUrl() {
        return "http://www.youtube.com/watch?v=" + this.key;
    }
}
