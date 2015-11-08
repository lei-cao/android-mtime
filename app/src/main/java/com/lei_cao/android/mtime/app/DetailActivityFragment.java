package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lei_cao.android.mtime.app.DAO.MoviesDAO;
import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.models.Video;
import com.lei_cao.android.mtime.app.services.MovieResponses;
import com.lei_cao.android.mtime.app.services.MovieService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    // The videos adapter
    VideosAdapter adapter;

    // The movieDb service
    MovieService service;

    // The apiKey
    String apiKey;

    MoviesDAO dao;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        service = new MovieService();
        apiKey = getResources().getString(R.string.themoviedb_api_key);

        Intent intent = getActivity().getIntent();
        String extraName = getResources().getString(R.string.intent_movie_name);
        if (intent == null || !intent.hasExtra(extraName)) {
            return rootView;
        }
        final Movie movie = (Movie) intent.getParcelableExtra(extraName);
        ImageView image = (ImageView) rootView.findViewById(R.id.detail_movie_image);
        Picasso.with(getActivity()).load(movie.getDetailUrl()).noFade().into(image);

        // set title, overview, rating, release date
        TextView title = (TextView) rootView.findViewById(R.id.detail_movie_title);
        TextView overview = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        TextView vote = (TextView) rootView.findViewById(R.id.detail_movie_vote);
        final TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_movie_release_date);

        title.setText(movie.title);
        overview.setText(movie.overview);
        vote.setText(movie.voteAverage);
        releaseDate.setText(movie.releaseDate);

        ListView videos = (ListView) rootView.findViewById(R.id.detail_movie_videos);
        adapter = new VideosAdapter(getActivity(), new ArrayList<Video>());
        videos.setAdapter(adapter);

        videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = adapter.getItem(position);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.GetVideoUrl())));
            }
        });

        Call<MovieResponses.VideosResponse> call = service.service.movieVideos(String.valueOf(movie.id), apiKey);
        call.enqueue(new Callback<MovieResponses.VideosResponse>() {
            @Override
            public void onResponse(Response<MovieResponses.VideosResponse> response, Retrofit retrofit) {
                if (response.body() != null && response.body().results.size() != 0) {
                    adapter.clear();
                    for (Video v : response.body().results) {
                        adapter.add(v);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });


        dao = new MoviesDAO(this.getActivity());
        dao.open();

        final ImageButton favorite = (ImageButton) rootView.findViewById(R.id.detail_movie_favorite_imagebutton);
        Movie m = dao.getMovie(movie.id);
        if (m != null) {
            movie.favorited = true;
            favorite.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (movie.favorited) {
                    dao.deleteMovie(movie);
                    favorite.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    movie.favorited = false;
                } else {
                    Movie m = dao.createMovie(movie);
                    if (m != null) {
                        favorite.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                        movie.favorited = true;
                    }
                }
                v.setEnabled(true);
            }
        });
        return rootView;
    }

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
