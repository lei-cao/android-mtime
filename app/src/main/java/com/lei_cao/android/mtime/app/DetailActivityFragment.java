package com.lei_cao.android.mtime.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lei_cao.android.mtime.app.models.Movie;
import com.lei_cao.android.mtime.app.models.TheMovieDb;
import com.lei_cao.android.mtime.app.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    // TheMovieDb for getting TheMovieDb info
    TheMovieDb theMovieDb = null;

    VideosAdapter adapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        theMovieDb = new TheMovieDb(getResources().getString(R.string.themoviedb_api_key));

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

        Log.v(LOG_TAG, movie.id.toString());
        Log.v(LOG_TAG, theMovieDb.MovieVideosUri(movie.id).toString());

        ListView videos = (ListView) rootView.findViewById(R.id.detail_movie_videos);
        adapter = new VideosAdapter(getActivity(), new ArrayList<Video>());
        videos.setAdapter(adapter);

        videos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = adapter.getItem(position);

                Log.i("Video", "Video Playing....");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.GetVideoUrl())));
                Log.i("Video", "Video Playing....");
            }
        });

        new FetchVideosTask().execute(movie.id);
        return rootView;
    }

    private class FetchVideosTask extends AsyncTask<Long, Integer, ArrayList<Video>> {

        @Override
        protected ArrayList<Video> doInBackground(Long... tasks) {
            ArrayList<Video> videos = theMovieDb.FetchMovieVideos(tasks[0]);
            return videos;
        }

        protected void onPostExecute(ArrayList<Video> videos) {
            Log.v(LOG_TAG, String.valueOf(videos.size()));
            if (videos.size() != 0) {
                adapter.clear();
                for (Video video : videos) {
                    adapter.add(video);
                    Log.v(LOG_TAG, video.name);
                }
            }
        }
    }
}
