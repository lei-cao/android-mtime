package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    final String sortByPopularityDesc = "popularity.desc";
    final String sortByVoteAverageDesc = "vote_average.desc";

    GridView grid;
    MovieGridAdapter adapter;
    ArrayList<String> images = new ArrayList<String>();

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        grid = (GridView) rootView.findViewById(R.id.grid_movie);

        adapter = new MovieGridAdapter(getActivity(), images);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String image = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(Intent.EXTRA_TEXT, image);
                startActivity(intent);
            }
        });

        Button sortByPopularity = (Button) rootView.findViewById(R.id.sort_by_popularity);
        sortByPopularity.setOnClickListener(listenerSortByPopularity);

        Button sortByVoting = (Button) rootView.findViewById(R.id.sort_by_voting);
        sortByVoting.setOnClickListener(listenerSortByVoting);

        new FetchMovieTask().execute(sortByPopularityDesc);

        return rootView;
    }

    public View.OnClickListener listenerSortByPopularity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new FetchMovieTask().execute(sortByPopularityDesc);
        }
    };

    public View.OnClickListener listenerSortByVoting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new FetchMovieTask().execute(sortByVoteAverageDesc);
        }
    };

    private class FetchMovieTask extends AsyncTask<String, Integer, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] parseMovieJson(String movieJsonStr) throws JSONException {
            final String PAGE = "page";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            ArrayList<String> result = new ArrayList<String>();
            JSONObject movieJson = new JSONObject(movieJsonStr);

            JSONArray movieArray = movieJson.getJSONArray(RESULTS);
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                result.add(movie.getString(POSTER_PATH));
                Log.v(LOG_TAG, result.get(i));
            }
            return result.toArray(new String[result.size()]);
        }

        private String getImageUrl(String path) {
            final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
            final String WIDTH = "w185";

            return IMAGE_BASE_URL + WIDTH + path;
        }

        protected String[] doInBackground(String... params) {
            return fetchData(params[0]);
        }

        protected void onPostExecute(String[] result) {
            if (result != null) {
                adapter.clear();
                for (String posterPath : result) {
                    adapter.add(getImageUrl(posterPath));
                }
            }
            Log.v(LOG_TAG, result.toString());
        }

        /**
         * Fetch data from movie db
         *
         * @return
         */
        public String[] fetchData(String sort) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            String page = "1";

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=" + getResources().getString(R.string.themoviedb_api_key);
                final String PAGE_PARAM = "page";
                final String SORT_BY_PARAM = "sort_by";

                Uri movieUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, page)
                        .appendQueryParameter(SORT_BY_PARAM, sort)
                        .build();

                URL url = new URL(movieUri.toString());

                Log.v(LOG_TAG, "Movie URI " + movieUri.toString());

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

            try {
                return parseMovieJson(movieJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }
}
