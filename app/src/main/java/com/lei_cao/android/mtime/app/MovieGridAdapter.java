package com.lei_cao.android.mtime.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieGridAdapter extends ArrayAdapter<Movie> {

    Context context;

    public MovieGridAdapter(Context c, List<Movie> i) {
        super(c, 0, i);
        context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        }

        String url = getItem(position).getListUrl();
        ImageView imageView = (ImageView) convertView
                .findViewById(R.id.grid_item_movie_image);

        imageView.setImageResource(R.mipmap.ic_launcher);
        Picasso.with(context)
                .load(url)
                .noFade()
                .into(imageView);

        return convertView;
    }
}
