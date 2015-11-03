package com.lei_cao.android.mtime.app.models;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TheMovieDb {

    private final String LOG_TAG = TheMovieDb.class.getSimpleName();

    private final String PAGE_PARAM = "page";
    private final String SORT_BY_PARAM = "sort_by";

    private final String DISCOVER_MOVIE_PATH = "discover/movie";
    private final String MOVIE_VIDEOS_PATH = "movie/%d/videos";
    private String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/%s?api_key=";

    private String apiKey;

    public TheMovieDb(String apiKey) {
        this.apiKey = apiKey;
        MOVIE_DB_BASE_URL += apiKey;
    }

    public Uri DiscoverMovieUri(int page, String sort) {
        return Uri.parse(String.format(MOVIE_DB_BASE_URL, DISCOVER_MOVIE_PATH)).buildUpon()
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .appendQueryParameter(SORT_BY_PARAM, sort)
                .build();
    }

    public Uri MovieVideosUri(Long id) {
        return Uri.parse(String.format(MOVIE_DB_BASE_URL, String.format(MOVIE_VIDEOS_PATH, id))).buildUpon()
                .build();
    }

    /**
     * Fetch Discover Movies
     *
     * @return
     */
    public ArrayList<Movie> FetchDiscoverMovies(MovieTask task) {

        String urlStr = DiscoverMovieUri(task.page, task.sort).toString();
        String movieJsonStr = FetchJSONData(urlStr);

        Log.v(LOG_TAG, movieJsonStr);

        try {
            return Movie.parseMovieJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch movie videos
     *
     * @return
     */
    public ArrayList<Video> FetchMovieVideos(Long id) {

        String urlStr = MovieVideosUri(id).toString();
        String videosJsonStr = FetchJSONData(urlStr);

        Log.v(LOG_TAG, videosJsonStr);

        try {
            return Video.parseVideoJson(videosJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Fetch JSON data from movie db
     *
     * @param urlStr
     * @return
     */
    private String FetchJSONData(String urlStr) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            URL url = new URL(urlStr);

            Log.v(LOG_TAG, "Movie URI " + urlStr);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                movieJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                movieJsonStr = null;
            }
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.v(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            movieJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.v(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.v(LOG_TAG, movieJsonStr);

        return movieJsonStr;
    }

}
