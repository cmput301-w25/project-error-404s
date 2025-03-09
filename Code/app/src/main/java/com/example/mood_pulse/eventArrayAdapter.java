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
            convertView = inflater.inflate(R.layout.item_mood_event, parent, false);
        }

        MoodEvent event = getItem(position);
        if (event != null) {
            TextView emotionText = convertView.findViewById(R.id.emotionText);
            TextView dateText = convertView.findViewById(R.id.dateText);
            TextView noteText = convertView.findViewById(R.id.noteText);

            emotionText.setText(event.getEmotionalState());
            dateText.setText(event.getDate().toString());
            noteText.setText(event.getNote());
        }

        return convertView;
    }

    public void updateData(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        notifyDataSetChanged();
    }
}