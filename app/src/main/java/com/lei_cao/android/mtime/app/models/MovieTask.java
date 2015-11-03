package com.lei_cao.android.mtime.app.models;

import java.util.ArrayList;

// A MovieTask to fetch movies from server
public class MovieTask {

    // The sorting option
    String sort;

    // The page to fetch
    int page = 1;

    // Clear the adapter or not
    public boolean clearAdapter = false;

    // The movies fetched by task
    public ArrayList<Movie> movies = new ArrayList<Movie>();

    public MovieTask(String sort, int page, boolean clearAdapter) {
        this.sort = sort;
        this.page = page;
        this.clearAdapter = clearAdapter;
    }
}
