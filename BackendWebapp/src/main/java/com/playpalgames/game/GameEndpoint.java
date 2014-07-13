package com.playpalgames.game;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.playpalgames.backend.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(name = "gameEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "game.playpalgames.com", ownerName = "game.playpalgames.com", packagePath = ""))
public class GameEndpoint {

    // Make sure to add this endpoint to your web.xml file if this is a web application.

    private static final Logger LOG = Logger.getLogger(GameEndpoint.class.getName());

    /**
     * This inserts a new <code>Turn</code> object.
     *
     * @param turn The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertTurn")
    public Turn insertTurn(Turn turn) {
        Turn lastTurn = ofy().load().type(Turn.class).filter("matchId", turn.getMatchId()).order("-turnNumber").first().now();

        turn.setTurnNumber(lastTurn != null ? lastTurn.getTurnNumber() + 1 : 0);
        ofy().save().entity(turn).now();
        LOG.info("Calling insertTurn method");

        return turn;
    }

    /**
     * This creates a new Match
     *
     * @return The match created
     */
    @ApiMethod(name = "createMatch", httpMethod = ApiMethod.HttpMethod.GET)
    public Match createMatch() {
        LOG.info("Calling createMatch method");

        Match match = new Match();
        ofy().save().entity(match).now();

        LOG.info("Match created with id "+match.getId());


        return match;
    }


    @ApiMethod(name = "listTurns")
    public Turn[] listTurns() {
        LOG.info("ListTurns called");
        List<Turn> turns = ofy().load().type(Turn.class).order("-turnNumber").list();
        //List<Turn> turns= ofy().load().type(Turn.class).list();

        return turns.toArray(new Turn[turns.size()]);
    }

    @ApiMethod (name = "listTurnsFrom")
    public Turn[] listTurnsFrom(@Named("from") Long from) {
        LOG.info("ListTurns from "+from+" called");
        List<Turn> turns = ofy().load().type(Turn.class).filter("turnNumber >",from).order("turnNumber").list();
        //List<Turn> turns= ofy().load().type(Turn.class).list();

        return turns.toArray(new Turn[turns.size()]);
    }
}