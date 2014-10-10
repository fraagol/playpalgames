package com.playpalgames.app.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.playpalgames.app.R;

import com.playpalgames.backend.gameEndpoint.model.Match;

import java.util.List;

/**
 * Created by javi on 07/10/2014.
 */
public class PendingGamesAdapter extends ArrayAdapter<Match> {
    public PendingGamesAdapter(Context context, int resource, List<Match> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Match match = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pendinggameitemlayout, parent, false);
        }
        // Lookup view for data population
        TextView opponentTextView = (TextView) convertView.findViewById(R.id.gameOpponent);

        // Populate the data into the template view using the data object
       opponentTextView.setText(match.getHostName());

        // Return the completed view to render on screen
        return convertView;
    }
}
