package com.lei_cao.android.mtime.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieGridAdapter extends ArrayAdapter<Movie> {

    private Activity activity;
    private ArrayList<Movie> items;


    public MovieGridAdapter(Activity a, ArrayList<Movie> i) {
        super(a, 0, i);
        activity = a;
        items = i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater()
                    .inflate(R.layout.grid_item_movie, parent, false);
        }

        String url = getItem(position).getListUrl();
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.grid_item_movie_image);

        imageView.setImageResource(R.mipmap.ic_launcher);
        Picasso.with(activity)
                .load(url)
                .noFade()
                .into(imageView);

        return convertView;
    }
}
