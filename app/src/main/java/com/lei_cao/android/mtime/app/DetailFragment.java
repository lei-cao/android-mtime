package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

public class DetailFragment extends Fragment {

    static final String DETAIL_MOVIE = "MOVIE";

    // The videos adapter
    VideosAdapter adapter;

    // The movieDb service
    MovieService service;

    // The apiKey
    String apiKey;

    MoviesDAO dao;

    Movie movie;

    Boolean mTwoPane = false;

    private ShareActionProvider mShareActionProvider;

    private String mVideo;

    private static final String MOVIE_SHARE_HASHTAG = " #MTime";

    public interface DetailCallback {
        public void onShowReviews(Movie movie);
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

        service = new MovieService();
        apiKey = getResources().getString(R.string.themoviedb_api_key);


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
                    int i = 0;
                    for (Video v : response.body().results) {
                        if (i == 0) {
                            mVideo = v.GetVideoUrl();
                            if (mShareActionProvider != null) {
                                mShareActionProvider.setShareIntent(createShareMovie());
                            }
                        }
                        adapter.add(v);
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        Button reviews = (Button) rootView.findViewById(R.id.detail_movie_reviews_button);
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    ((MoviesFragment.MovieCallback) getActivity()).onShowReviews(movie);
                } else {
                    ((DetailCallback) getActivity()).onShowReviews(movie);
                }
            }
        });

        dao = new MoviesDAO(this.getActivity());
        dao.open();

        final ImageButton favorite = (ImageButton) rootView.findViewById(R.id.detail_movie_favorite_imagebutton);
        Movie m = dao.getMovie(movie.id);
        if (m != null) {
            movie.favorited = true;
            favorite.setImageResource(android.R.drawable.btn_star_big_on);
        }
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (movie.favorited) {
                    dao.deleteMovie(movie);
                    favorite.setImageResource(android.R.drawable.btn_star_big_off);
                    movie.favorited = false;
                } else {
                    Movie m = dao.createMovie(movie);
                    if (m != null) {
                        favorite.setImageResource(android.R.drawable.btn_star_big_on);
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
        if (dao != null) {
            dao.open();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (dao != null) {
            dao.close();
        }
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mVideo != null) {
            mShareActionProvider.setShareIntent(createShareMovie());
        }
    }

    private Intent createShareMovie() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mVideo + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }


}
