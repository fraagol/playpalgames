package com.playpalgames.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.playpalgames.backend.gameEndpoint.model.Match;
import com.playpalgames.library.GameController;

import java.util.List;

/**
 * Created by javi on 07/10/2014.
 */
public class PendingGamesAdapter extends ArrayAdapter<Match> {
    Long userId;

    public PendingGamesAdapter(Context context, int resource, List<Match> objects, Long id) {
        super(context, resource, objects);
        this.userId = id;
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
        opponentTextView.setText(match.getHostUserId().equals(userId) ? match.getGuestName() : match.getHostName());

        TextView turnUserTextView = (TextView) convertView.findViewById(R.id.turnUser);
        switch (match.getStatus()) {
            case GameController.STATUS_IN_GAME:
                turnUserTextView.setText(match.getNextTurnPlayerId().equals(userId) ? "Tu turno" : "Esperando a tu oponente");
                break;
            case GameController.STATUS_INVITATION_SENT:
                turnUserTextView.setText(match.getHostUserId().equals(userId) ? "Invitación enviada" : "Invitación recibida");
                break;
            case GameController.STATUS_INVITATION_ACCEPTED:
                turnUserTextView.setText(match.getHostUserId().equals(userId) ? "Desafío aceptado" : "Esperando inicio");
                break;
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivUserIcon);
        imageView.setImageResource(StartActivity.GAME_IMAGES_ID[match.getGameType()]);


        // Return the completed view to render on screen
        return convertView;
    }
}
