package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.models.Review;
import com.lei_cao.android.mtime.app.services.MovieResponses;
import com.lei_cao.android.mtime.app.services.MovieService;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ReviewFragment extends Fragment {

    // The reviews adapter
    ReviewsAdapter adapter;

    // The movieDb service
    MovieService service;

    // The apiKey
    String apiKey;

    Movie movie;
    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_review, container, false);

        service = new MovieService();
        apiKey = getResources().getString(R.string.themoviedb_api_key);

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
        } else {
            Intent intent = getActivity().getIntent();
            if (intent == null || !intent.hasExtra(DetailFragment.DETAIL_MOVIE)) {
                return rootView;
            }
            movie = (Movie) intent.getParcelableExtra(DetailFragment.DETAIL_MOVIE);
        }
        if (movie == null) {
            return rootView;
        }

        ListView reviews = (ListView) rootView.findViewById(R.id.detail_reviews);
        adapter = new ReviewsAdapter(getActivity(), new ArrayList<Review>());
        reviews.setAdapter(adapter);
        Call<MovieResponses.ReviewsResponse> reviewsCall = service.service.movieReviews(String.valueOf(movie.id), apiKey, 1);
        reviewsCall.enqueue(new Callback<MovieResponses.ReviewsResponse>() {
            @Override
            public void onResponse(Response<MovieResponses.ReviewsResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().results.size() != 0) {
                    adapter.clear();
                    for (Review r : response.body().results) {
                        adapter.add(r);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        return rootView;
    }
}
