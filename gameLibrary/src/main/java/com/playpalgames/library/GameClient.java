package com.playpalgames.library;

/**
 * Created by javi on 01/08/2014.
 */
public interface GameClient {
public void incomingChallenge(final String challengerName, final String matchId);
public void challengeAccepted();
}
