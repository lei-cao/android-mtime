package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lei_cao.android.mtime.app.models.Movie;

public class DetailActivity extends AppCompatActivity implements DetailFragment.DetailCallback {

    Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailFragment())
                    .commit();
        }

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(DetailFragment.DETAIL_MOVIE)) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
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
