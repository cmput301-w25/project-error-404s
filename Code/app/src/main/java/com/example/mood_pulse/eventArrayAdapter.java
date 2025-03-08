package com.example.mood_pulse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class eventArrayAdapter extends ArrayAdapter<MoodEvent> {
    private final LayoutInflater inflater;
    private List<MoodEvent> events;

    public eventArrayAdapter(Context context, List<MoodEvent> events) {
        super(context, 0, events);
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.add_mood, parent, false);
        }

        MoodEvent event = getItem(position);
        if (event != null) {
            TextView emotionalState = convertView.findViewById(R.id.dateTv);
            TextView date = convertView.findViewById(R.id.timeTv);

            emotionalState.setText(event.getEmotionalState());
            date.setText(event.getDate().toString());
        }

        return convertView;
    }

    public void updateData(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        notifyDataSetChanged();
    }
}