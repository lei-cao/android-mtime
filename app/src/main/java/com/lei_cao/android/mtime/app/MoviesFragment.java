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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.lei_cao.android.mtime.app.models.Movie;

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

    // The param value for fetching movies by popularity desc
    final String sortByPopularityDesc = "popularity.desc";

    // The param value for fetching movies by vote average desc
    final String sortByVoteAverageDesc = "vote_average.desc";

    // The grid view
    GridView grid;

    // The movies adapter for the grid view
    MovieGridAdapter adapter;

    // Current page loaded in the adapter
    int currentPage = 1;

    // The task is loading
    boolean loadingMore = true;

    // If all data fetched, stop loading data from server
    boolean stopLoadingData = false;

    // A MovieTask to fetch movies from server
    private class MovieTask {

        // The sorting option
        String sort = sortByPopularityDesc;

        // The page to fetch
        int page = 1;

        // Clear the adapter or not
        boolean clearAdapter = false;

        // The movies fetched by task
        ArrayList<Movie> movies = new ArrayList<Movie>();

        public MovieTask(String sort, int page, boolean clearAdapter) {
            this.sort = sort;
            this.page = page;
            this.clearAdapter = clearAdapter;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        grid = (GridView) rootView.findViewById(R.id.grid_movie);

        adapter = new MovieGridAdapter(getActivity(), new ArrayList<Movie>());
        grid.setAdapter(adapter);

        // Click on the grid's item, will navigate to the detail view
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String extraName = getResources().getString(R.string.intent_movie_name);
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra(extraName, movie);
                startActivity(intent);
            }
        });

        // While scrolling the grid, loading more data from the server
        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !loadingMore) {
                    if (!stopLoadingData) {
                        new FetchMovieTask().execute(new MovieTask(sortByPopularityDesc, currentPage++, false));
                    }
                }
            }
        });

        Button sortByPopularity = (Button) rootView.findViewById(R.id.sort_by_popularity);
        sortByPopularity.setOnClickListener(listenerSortByPopularity);

        Button sortByVoting = (Button) rootView.findViewById(R.id.sort_by_voting);
        sortByVoting.setOnClickListener(listenerSortByVoting);

        new FetchMovieTask().execute(new MovieTask(sortByPopularityDesc, currentPage, true));

        return rootView;
    }

    public View.OnClickListener listenerSortByPopularity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentPage = 1;
            stopLoadingData = false;
            new FetchMovieTask().execute(new MovieTask(sortByPopularityDesc, currentPage, true));
        }
    };

    public View.OnClickListener listenerSortByVoting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentPage = 1;
            stopLoadingData = false;
            new FetchMovieTask().execute(new MovieTask(sortByVoteAverageDesc, currentPage, true));
        }
    };

    private class FetchMovieTask extends AsyncTask<MovieTask, Integer, MovieTask> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private ArrayList<Movie> parseMovieJson(String movieJsonStr) throws JSONException {
            final String PAGE = "page";
            final String RESULTS = "results";
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
                movie.posterPath = movieObj.getString(POSTER_PATH);
                movie.title = movieObj.getString(TITLE);
                movie.overview = movieObj.getString(OVERVIEW);
                movie.voteAverage = movieObj.getString(VOTE_AVERAGE);
                movie.releaseDate = movieObj.getString(RELEASE_DATE);
                movies.add(movie);
            }
            return movies;
        }

        protected MovieTask doInBackground(MovieTask... tasks) {
            loadingMore = true;
            tasks[0].movies = fetchData(tasks[0]);
            return tasks[0];
        }

        protected void onPostExecute(MovieTask task) {
            if (task.movies.size() != 0) {
                if (task.clearAdapter) {
                    adapter.clear();
                }
                for (Movie movie : task.movies) {
                    adapter.add(movie);
                }
                currentPage++;
            } else {
                // Seems no more data from the server anymore, stop loading.
                // It's better to have explicit check
                stopLoadingData = true;
            }
            loadingMore = false;
        }

        /**
         * Fetch data from movie db
         *
         * @return
         */
        public ArrayList<Movie> fetchData(MovieTask task) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=" + getResources().getString(R.string.themoviedb_api_key);
                final String PAGE_PARAM = "page";
                final String SORT_BY_PARAM = "sort_by";

                Uri movieUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, String.valueOf(task.page))
                        .appendQueryParameter(SORT_BY_PARAM, task.sort)
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
