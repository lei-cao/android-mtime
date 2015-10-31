package com.lei_cao.android.mtime.app;

import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MoviesFragment extends Fragment {
    GridView grid;
    MovieGridAdapter adapter;
    ArrayList<String> images;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        String[] imagesArray = {
                "http://ia.media-imdb.com/images/M/MV5BMjE4MDI3NDI2Nl5BMl5BanBnXkFtZTcwNjE5OTQwOA@@._V1_SY172_CR2,0,116,172_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMTg3NDIzNTc4OF5BMl5BanBnXkFtZTgwNDc4MDM5NjE@._V1_SY201_CR50,0,201,201_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMTQxNzM4MzY0N15BMl5BanBnXkFtZTgwNjYzMzQ0MzE@._V1_SY201_CR33,0,201,201_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMTEyMjM1OTQ2NzReQTJeQWpwZ15BbWU4MDkwODIxOTYx._V1_SY201_CR50,0,201,201_AL_.jpg",
        };

        images = new ArrayList<String>(
                Arrays.asList(imagesArray)
        );

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

        Button b = (Button) rootView.findViewById(R.id.button1);
        b.setOnClickListener(listener);

        new FetchMovieTask().execute();

        return rootView;
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Collections.sort(images);
            adapter.notifyDataSetChanged();
        }

    };

    private class FetchMovieTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strings) {
            return fetchData();
        }

        protected void onPostExecute(String result) {
            Log.v("Fetch Movie Task", result);
        }

        /**
         * Fetch data from movie db
         *
         * @return
         */
        public String fetchData() {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.themoviedb.org/3/movie/latest?api_key=" + getResources().getString(R.string.themoviedb_api_key));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
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
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.v("Fetch Movie Task", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.v("Fetch Movie Task", "Error closing stream", e);
                    }
                }
            }
            return forecastJsonStr;
        }
    }
}
