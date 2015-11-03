package com.lei_cao.android.mtime.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by leicao on 3/11/15.
 */
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

    public static ArrayList<Video> parseVideoJson(String json) throws JSONException {
        final String RESULTS = "results";

        ArrayList<Video> videos = new ArrayList<Video>();
        JSONObject videoJson = new JSONObject(json);

        JSONArray videoArray = videoJson.getJSONArray(RESULTS);
        for (int i = 0; i < videoArray.length(); i++) {
            Video video = new Video();
            JSONObject videoObj = videoArray.getJSONObject(i);
            video.id = videoObj.getString("id");
            video.key = videoObj.getString("key");
            video.name = videoObj.getString("name");
            video.site = videoObj.getString("site");
            video.size = videoObj.getString("size");
            video.type = videoObj.getString("type");

            videos.add(video);
        }
        return videos;
    }
}
