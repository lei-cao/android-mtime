package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        String extraName = getResources().getString(R.string.intent_movie_name);
        if (intent == null || !intent.hasExtra(extraName)) {
            return rootView;
        }
        Movie movie = (Movie) intent.getParcelableExtra(extraName);
        ImageView image = (ImageView) rootView.findViewById(R.id.detail_movie_image);
        Picasso.with(getActivity()).load(movie.getDetailUrl()).noFade().into(image);

        // set title, overview, rating, release date
        TextView title = (TextView) rootView.findViewById(R.id.detail_movie_title);
        TextView overview = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        TextView vote = (TextView) rootView.findViewById(R.id.detail_movie_vote);
        TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_movie_release_date);

        title.setText(movie.title);
        overview.setText(movie.overview);
        vote.setText(movie.voteAverage);
        releaseDate.setText(movie.releaseDate);

        return rootView;
    }

}
