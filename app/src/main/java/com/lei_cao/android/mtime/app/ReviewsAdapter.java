package com.lei_cao.android.mtime.app;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lei_cao.android.mtime.app.models.Review;

import java.util.List;

public class ReviewsAdapter extends ArrayAdapter<Review> {
    private Activity activity;
    private List<Review> items;

    public ReviewsAdapter(Activity a, List<Review> i) {
        super(a, 0, i);
        activity = a;
        items = i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater()
                    .inflate(R.layout.list_item_review, parent, false);
        }

        TextView author = (TextView) convertView.findViewById(R.id.play_review_author_textview);
        author.setText(getItem(position).author);

        TextView content = (TextView) convertView.findViewById(R.id.play_review_content_textview);
        content.setText(getItem(position).content);

        return convertView;
    }
}
