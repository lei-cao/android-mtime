package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.models.MovieTask;
import com.lei_cao.android.mtime.app.models.TheMovieDb;

import java.util.ArrayList;

public class MoviesFragment extends Fragment {

    // The param value for fetching movies by popularity desc
    final String sortByPopularityDesc = "popularity.desc";

    // The param value for fetching movies by vote average desc
    final String sortByVoteAverageDesc = "vote_average.desc";

    // TheMovieDb for getting TheMovieDb info
    TheMovieDb theMovieDb = null;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        theMovieDb = new TheMovieDb(getResources().getString(R.string.themoviedb_api_key));

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
        protected MovieTask doInBackground(MovieTask... tasks) {
            loadingMore = true;
            tasks[0].movies = theMovieDb.FetchDiscoverMovies(tasks[0]);
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
    }
}
