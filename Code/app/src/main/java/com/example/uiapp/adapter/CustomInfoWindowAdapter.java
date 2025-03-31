package com.example.uiapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uiapp.R;
import com.example.uiapp.model.EmojiHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View window;
    private final Context context;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void renderWindowText(Marker marker, View view) {
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        ImageView moodIconImageView = view.findViewById(R.id.moodIconImageView);

        // Retrieve username from marker
        usernameTextView.setText(marker.getTitle());

        // Retrieve moodIcon from marker's tag
        if (marker.getTag() instanceof String) {
            String moodIconResId = (String) marker.getTag();
            moodIconImageView.setImageDrawable(EmojiHelper.getMoodDrawable(moodIconImageView.getContext(), moodIconResId));
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

