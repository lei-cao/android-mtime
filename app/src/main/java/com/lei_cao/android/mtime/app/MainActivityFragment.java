package com.lei_cao.android.mtime.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.lei_cao.android.lazylist.LazyAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    GridView grid;
    LazyAdapter adapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] imagesArray = {
                "http://ia.media-imdb.com/images/M/MV5BMTg3NDIzNTc4OF5BMl5BanBnXkFtZTgwNDc4MDM5NjE@._V1_SY201_CR50,0,201,201_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMTEyMjM1OTQ2NzReQTJeQWpwZ15BbWU4MDkwODIxOTYx._V1_SY201_CR50,0,201,201_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMTQxNzM4MzY0N15BMl5BanBnXkFtZTgwNjYzMzQ0MzE@._V1_SY201_CR33,0,201,201_AL_.jpg",
                "http://ia.media-imdb.com/images/M/MV5BMjE4MDI3NDI2Nl5BMl5BanBnXkFtZTcwNjE5OTQwOA@@._V1_SY172_CR2,0,116,172_AL_.jpg",
        };


        grid = (GridView) rootView.findViewById(R.id.grid_movie);

        adapter = new LazyAdapter(getActivity(), imagesArray);
        grid.setAdapter(adapter);

        Button b = (Button) rootView.findViewById(R.id.button1);
        b.setOnClickListener(listener);

        return rootView;
    }

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }

    };
}
