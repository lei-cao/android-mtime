package com.lei_cao.android.mtime.app.services;

import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.models.Review;
import com.lei_cao.android.mtime.app.models.Video;

import java.util.List;

public class MovieResponses {

    public class MoviesResponse {
        public String page;
        public List<Movie> results;
    }

    public class VideosResponse {
        public String id;
        public List<Video> results;
    }

    public class ReviewsResponse {
        public String id;
        public List<Review> results;
    }
}
