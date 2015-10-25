package com.lei_cao.android.mtime.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] moviesArray = {
                "Inside out",
                "Star Trek",
                "Terminator",
                "Interstellar",
                "Inception",
                "Beautiful Mind"
        };

        List<String> movies = new ArrayList<String>(
                Arrays.asList(moviesArray)
        );

        ArrayAdapter<String> moviesAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_textview, movies
        );

        ListView moviesList = (ListView) rootView.findViewById(R.id.list_movie);

        moviesList.setAdapter(moviesAdapter);

        return rootView;
    }
}
