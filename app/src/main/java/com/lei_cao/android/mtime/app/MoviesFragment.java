package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.lei_cao.android.mtime.app.DAO.MoviesDAO;
import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.services.MovieResponses;
import com.lei_cao.android.mtime.app.services.MovieService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MoviesFragment extends Fragment {

    // The param value for fetching movies by popularity desc
    final String sortByPopularityDesc = "popularity.desc";

    // The param value for fetching movies by vote average desc
    final String sortByVoteAverageDesc = "vote_average.desc";

    String sort = sortByPopularityDesc;

    MovieService service;

    String apiKey;

    // The grid view
    GridView grid;

    // The movies adapter for the grid view
    MovieGridAdapter adapter;

    // The movies collection of sortByPopularityDesc
    List<Movie> movies = new ArrayList<>();

    // Current page loaded in the adapter
    int currentPage = 1;

    // The task is loading
    boolean loadingMore = false;

    // If all data fetched, stop loading data from server
    boolean stopLoadingData = false;

    MoviesDAO dao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        service = new MovieService();
        apiKey = getResources().getString(R.string.themoviedb_api_key);

        grid = (GridView) rootView.findViewById(R.id.grid_movie);

        adapter = new MovieGridAdapter(getActivity(), movies);
        grid.setAdapter(adapter);

        // Click on the grid's item, will navigate to the detail view
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String extraName = getResources().getString(R.string.intent_movie_name);
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailsActivity.class).putExtra(extraName, movie);
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
                    if (!stopLoadingData && !loadingMore) {
//                        loadingMore = true;
                        Call<MovieResponses.MoviesResponse> call = service.service.discoverMovies(apiKey, currentPage, sort);
                        call.enqueue(callbackSortByPopularityDesc);
                    }
                }
            }
        });

        Button sortByPopularity = (Button) rootView.findViewById(R.id.sort_by_popularity);
        sortByPopularity.setOnClickListener(listenerSortByPopularity);

        Button sortByVoting = (Button) rootView.findViewById(R.id.sort_by_voting);
        sortByVoting.setOnClickListener(listenerSortByVoting);

        Button myFavorites = (Button) rootView.findViewById(R.id.sort_by_favorite);
        myFavorites.setOnClickListener(listenerMyFavorites);

        if (!stopLoadingData && !loadingMore) {
            loadingMore = true;

            Call<MovieResponses.MoviesResponse> call = service.service.discoverMovies(apiKey, currentPage, sort);
            call.enqueue(callbackSortByPopularityDesc);
        }

        dao = new MoviesDAO(this.getActivity());
        dao.open();

        return rootView;
    }

    Callback<MovieResponses.MoviesResponse> callbackSortByPopularityDesc = new Callback<MovieResponses.MoviesResponse>() {
        @Override
        public void onResponse(Response<MovieResponses.MoviesResponse> response, Retrofit retrofit) {

            if (response.body().results.size() != 0) {
                for (Movie m : response.body().results) {
                    adapter.add(m);
                }
                currentPage++;
            } else {
                // Seems no more data from the server anymore, stop loading.
                // It's better to have explicit check
                stopLoadingData = true;
            }
            loadingMore = false;
        }

        @Override
        public void onFailure(Throwable t) {
            loadingMore = false;
        }
    };

    Callback<MovieResponses.MoviesResponse> callbackSortByVoteAverageDesc = new Callback<MovieResponses.MoviesResponse>() {
        @Override
        public void onResponse(Response<MovieResponses.MoviesResponse> response, Retrofit retrofit) {

            if (response.body().results.size() != 0) {
                for (Movie m : response.body().results) {
                    adapter.add(m);
                }
                currentPage++;
            } else {
                // Seems no more data from the server anymore, stop loading.
                // It's better to have explicit check
                stopLoadingData = true;
            }
            loadingMore = false;
        }

        @Override
        public void onFailure(Throwable t) {
            loadingMore = false;
        }
    };

    public View.OnClickListener listenerSortByPopularity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentPage = 1;
            stopLoadingData = false;
            loadingMore = true;
            adapter.clear();
            sort = sortByPopularityDesc;
            Call<MovieResponses.MoviesResponse> call = service.service.discoverMovies(apiKey, currentPage, sort);
            call.enqueue(callbackSortByPopularityDesc);
        }
    };

    public View.OnClickListener listenerSortByVoting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            currentPage = 1;
            stopLoadingData = false;
            loadingMore = true;
            adapter.clear();
            sort = sortByVoteAverageDesc;
            Call<MovieResponses.MoviesResponse> call = service.service.discoverMovies(apiKey, currentPage, sort);
            call.enqueue(callbackSortByVoteAverageDesc);
        }
    };

    public View.OnClickListener listenerMyFavorites = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopLoadingData = true;
            List<Movie> myMovies = dao.getAllMovies();
            adapter.clear();
            for (Movie m : myMovies) {
                adapter.add(m);
            }
        }
    };

    @Override
    public void onResume() {
        dao.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dao.close();
        super.onPause();
    }
}
