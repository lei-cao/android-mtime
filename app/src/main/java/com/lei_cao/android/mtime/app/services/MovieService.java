package com.lei_cao.android.mtime.app.services;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public class MovieService {
    final private String baseUrl = "http://api.themoviedb.org/3/";

    public interface Interface {
        @GET("discover/movie")
        Call<MovieResponses.MoviesResponse> discoverMovies(
                @Query("api_key") String apiKey,
                @Query("page") int page,
                @Query("sort_by") String sort
        );

        @GET("movie/{id}/videos")
        Call<MovieResponses.VideosResponse> movieVideos(
                @Path("id") String id,
                @Query("api_key") String apiKey
        );

        @GET("movie/{id}/reviews")
        Call<MovieResponses.ReviewsResponse> movieReviews(
                @Path("id") String id,
                @Query("api_key") String apiKey,
                @Query("page") int page
        );
    }

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public Interface service = retrofit.create(Interface.class);
}