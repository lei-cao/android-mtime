package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lei_cao.android.mtime.app.models.Movie;

public class DetailActivity extends AppCompatActivity implements DetailFragment.DetailCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public void onShowReviews(Movie movie) {
        Intent intent = new Intent(this, ReviewActivity.class).putExtra(DetailFragment.DETAIL_MOVIE, movie);
        startActivity(intent);
    }
}
