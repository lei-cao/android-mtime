package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    public String id;
    public String author;
    public String content;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(author);
        out.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public Review(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
    }

    public Review() {

    }
}
