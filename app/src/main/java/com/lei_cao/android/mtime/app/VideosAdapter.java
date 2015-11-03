package com.lei_cao.android.mtime.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lei_cao.android.mtime.app.models.Video;

import java.util.ArrayList;

public class VideosAdapter extends ArrayAdapter<Video> {
    private Activity activity;
    private ArrayList<Video> items;

    public VideosAdapter(Activity a, ArrayList<Video> i) {
        super(a, 0, i);
        activity = a;
        items = i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater()
                    .inflate(R.layout.list_item_video, parent, false);
        }

        TextView video = (TextView) convertView.findViewById(R.id.play_video_textview);
        video.setText(getItem(position).name);

        return convertView;
    }
}
