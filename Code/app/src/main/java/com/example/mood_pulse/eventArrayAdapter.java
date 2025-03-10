package com.example.mood_pulse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * This adapter is responsible for displaying mood events in a list view.
 * -----------------------------------------------------------------------------
 * Handled in this class:
 * - Populating the list with mood event data
 * - Formatting and displaying mood event details
 * - Updating the list dynamically when new events are fetched
 */
public class eventArrayAdapter extends ArrayAdapter<MoodEvent> {
    private final LayoutInflater inflater;
    private List<MoodEvent> events;

    /**
     * Initializes the adapter with context and list of mood events.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Stores the context and event list
     * - Initializes the layout inflater for creating item views
     *
     * @param context The application context.
     * @param events  The list of mood events to display.
     */
    public eventArrayAdapter(Context context, List<MoodEvent> events) {
        super(context, 0, events);
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    /**
     * Creates or updates a view for a mood event at a given position.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Reuses views when possible for performance optimization
     * - Binds mood event details to the UI components
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view group.
     * @return The updated view displaying the mood event.
     */
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

    /**
     * Updates the mood event list with new data and refreshes the UI.
     * -----------------------------------------------------------------------------
     * Handled in this method:
     * - Clears the old list of events
     * - Adds new mood events to the list
     * - Notifies the adapter to refresh the list view
     *
     * @param newEvents The updated list of mood events.
     */
    public void updateData(List<MoodEvent> newEvents) {
        this.events.clear();
        this.events.addAll(newEvents);
        notifyDataSetChanged();
    }
}
