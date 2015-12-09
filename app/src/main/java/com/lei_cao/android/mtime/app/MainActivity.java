package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.lei_cao.android.mtime.app.models.Movie;

public class MainActivity extends AppCompatActivity implements MoviesFragment.MovieCallback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        MoviesFragment moviesFragment =  ((MoviesFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movies));
        moviesFragment.setTwoPanel(mTwoPane);

    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            fragment.mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class).putExtra(DetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }


    @Override
    public void onShowReviews(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            ReviewFragment fragment = new ReviewFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ReviewActivity.class).putExtra(DetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }

}
