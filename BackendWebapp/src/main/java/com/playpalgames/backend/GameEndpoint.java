package com.playpalgames.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.playpalgames.backend.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(name = "gameEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.playpalgames.com", ownerName = "backend.playpalgames.com", packagePath = ""))
public class GameEndpoint {
    private static final String LOCK = "LOCK";
    private static final Logger LOG = Logger.getLogger(GameEndpoint.class.getName());

    public static final String COMMAND_ACCEPT_CHALLENGE = "A";
    public static final String COMMAND_TURN = "T";
    public static final String COMMAND_CHALLENGE = "C";

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = "AIzaSyDqfPILfQJFKacnom3NVqNsd_WaVjbMALM";

    /**
     * This inserts a new <code>Turn</code> object.
     *
     * @param turn The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertTurn")
    public Turn insertTurn(Turn turn) throws IOException {
        Turn lastTurn;
        synchronized (LOCK) {
            LOG.info("turnData: " + turn.getTurnData());
            lastTurn = ofy().load().type(Turn.class).filter("matchId", turn.getMatchId()).order("-turnNumber").first().now();
            turn.setTurnNumber(lastTurn != null ? lastTurn.getTurnNumber() + 1 : 0);
            save(turn);
        }

        Match match = getById(turn.getMatchId(), Match.class);
        LOG.info("match: " + match);
        Long opponentId = match.getHostUserId().equals(turn.getPlayerId()) ? match.getGuestUserId() : match.getHostUserId();
        if (turn.isOpponentIsNext()) {
            LOG.info("opponent: " + opponentId);
            //Store nextTurnPlayerId to show on Pending games
            match.setNextTurnPlayerId(opponentId);
        } else {
            match.setNextTurnPlayerId(turn.getPlayerId());
        }
        //if first turn change match state
        if (lastTurn == null) {
            match.setStatus(Match.STATUS_IN_GAME);
        }
        LOG.info("match2: " + match);
        save(match);
        LOG.info("match3: " + match);
        //Notify opponent
        LOG.info("Notifiying turn to user: " + opponentId);
        sendMessage(COMMAND_TURN, findRegIdByUserId(opponentId));
        return turn;
    }

    /**
     * This creates a new Match and sends an invitation
     *
     * @return The match created
     */
    @ApiMethod(name = "createMatch", httpMethod = ApiMethod.HttpMethod.GET)
    public Match createMatch(@Named("userId") Long userId, @Named("invitedUserId") Long invitedUserId, @Named("invitedUserName") String invitedUserName, @Named("username") String username, @Named("gameType") int gameType) throws IOException {
        LOG.info("Calling createMatch method");
        Match match = new Match();
        match.setHostUserId(userId);
        match.setHostName(username);
        match.setGuestUserId(invitedUserId);
        match.setGuestName(invitedUserName);
        match.setStatus(Match.STATUS_INVITATION_SENT);
        match.setGameType(gameType);
        match.setNextTurnPlayerId(userId);
        saveMatch(match);
        LOG.info("Match created with id " + match.getId());
        sendMessage(COMMAND_CHALLENGE + " " + username + " " + match.getId() + " " + match.gameType, findRegIdByUserId(invitedUserId));

        return match;
    }

    @ApiMethod(name = "joinMatch", httpMethod = ApiMethod.HttpMethod.GET)
    public Match joinMatch(@Named("userId") Long userId, @Named("matchId") Long matchId, @Named("username") String username) throws IOException {
        LOG.info("Calling joinMatch ");
        Match match = getById(matchId, Match.class);
        match.setGuestUserId(userId);
        match.setGuestName(username);
        match.setStatus(Match.STATUS_INVITATION_ACCEPTED);
        saveMatch(match);
        //Send accepted to Match Creator
        try {
            sendMessage(COMMAND_ACCEPT_CHALLENGE + " " + matchId + " " + userId, findRegIdByUserId(match.getHostUserId()));
        } catch (Exception e) {
            LOG.severe("Error sending GCM message, Testing? " + e.getMessage());
        }
        return match;
    }

    @ApiMethod(name = "listTurns")
    public Turn[] listTurns(@Named("matchId") Long matchId) {
        LOG.info("ListTurns called");
        List<Turn> turns = ofy().load().type(Turn.class).filter("matchId", matchId).order("-turnNumber").list();
        return turns.toArray(new Turn[turns.size()]);
    }

    @ApiMethod(name = "listTurnsFrom")
    public Turn[] listTurnsFrom(@Named("matchId") Long matchId, @Named("from") Long from) {
        LOG.info("ListTurns from " + from + " called");
        List<Turn> turns = ofy().load().type(Turn.class).filter("matchId", matchId).filter("turnNumber >", from).order("turnNumber").list();
        return turns.toArray(new Turn[turns.size()]);
    }

    @ApiMethod(name = "getLastTurn")
    public Turn getLastTurn(@Named("matchId") Long matchId) {
        LOG.info("Last turn called");
        return ofy().load().type(Turn.class).filter("matchId", matchId).order("-turnNumber").first().now();

    }


    /**
     * Register a device to the backend
     */
    @ApiMethod(name = "endMatch")
    public void endMatch(@Named("matchId") Long matchId, @Named("isHost") boolean isHost) {
        Match match = getById(matchId, Match.class);
        switch (match.getStatus()) {
            case Match.STATUS_IN_GAME:
                match.setStatus(isHost ? Match.STATUS_HOST_FINISHED : Match.STATUS_GUEST_FINISHED);
                break;
            case Match.STATUS_GUEST_FINISHED:
                match.setStatus(isHost ? Match.STATUS_FINISHED : Match.STATUS_GUEST_FINISHED);
                break;
            case Match.STATUS_HOST_FINISHED:
                match.setStatus(isHost ? Match.STATUS_HOST_FINISHED : Match.STATUS_FINISHED);
                break;
            default:
                LOG.severe("Finishing match in incorrect state: " + match.getStatus());
        }
        saveMatch(match);
    }

    /**
     * Register a device to the backend
     */
    @ApiMethod(name = "register")
    public User registerDevice(User user) {
        String regId = user.getRegId();
        User existingUser = findUserByRegId(regId);
        if (existingUser != null) {
            LOG.info("Device " + regId + " already registered, skipping register");
            return existingUser;
        }
        ofy().save().entity(user).now();
        LOG.info(user.toString());
        return user;
    }

    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(@Named("regId") String regId) {
        User record = findUserByRegId(regId);
        if (record == null) {
            LOG.info("Device " + regId + " not registered, skipping unregister");
            return;
        }
        ofy().delete().entity(record).now();
    }

    /**
     * Return a collection of registered devices
     *
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<User> listDevices(/*@Named("count") int count*/) {
        List<User> records = ofy().load().type(User.class)/*.limit(count)*/.list();

        return CollectionResponse.<User>builder().setItems(records).build();

    }


    /**
     * Return a collection of games where the players is playing
     *
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listGamesByPlayer")
    public CollectionResponse<Match> listGamesByPlayer(@Named("playerId") long playerId) {
        int[] statusHost = new int[]{
                Match.STATUS_CREATED,
                Match.STATUS_INVITATION_SENT,
                Match.STATUS_INVITATION_ACCEPTED,
                Match.STATUS_IN_GAME,
                Match.STATUS_GUEST_FINISHED,
        };


        int[] statusGuest = new int[]{
                Match.STATUS_INVITATION_SENT,
                Match.STATUS_INVITATION_ACCEPTED,
                Match.STATUS_IN_GAME,
                Match.STATUS_HOST_FINISHED,
        };
        List<Match> matches = ofy().cache(false).load().type(Match.class).filter("hostUserId", playerId).filter("status IN", statusHost).list();
        List<Match> matchesGuest = ofy().cache(false).load().type(Match.class).filter("guestUserId", playerId).filter("status IN", statusGuest).list();
        matches.addAll(matchesGuest);
        return CollectionResponse.<Match>builder().setItems(matches).build();

    }

    /**
     * Return a collection of registered devices
     *
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listOthersDevices")
    public CollectionResponse<User> listOthersDevices(@Named("id") Long id) {
        List<User> records = ofy().load().type(User.class)/*.limit(count)*/.list();
        LOG.info("records size: " + records.size());
        Iterator<User> it = records.iterator();
        while (it.hasNext()) {
            Long remoteId = it.next().getId();
            LOG.info("Comparing user id " + id + " and " + remoteId);

            if (remoteId.equals(id)) {
                it.remove();
                LOG.info("Removing " + remoteId);
            }
        }
        LOG.info("records size: " + records.size());

        return CollectionResponse.<User>builder().setItems(records).build();
    }


    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message, @Named("regId") String regId) throws IOException {
        if (message == null || message.trim().length() == 0) {
            LOG.warning("Not sending message because it is empty");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();

        Result result;
        int numTries = 3;
        while (true) {
            try {
                result = sender.send(msg, regId, 5);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                LOG.severe("Try " + numTries + ":" + e.getMessage());
                if (--numTries == 0) throw e;
            }
        }

        if (result.getMessageId() != null) {
            LOG.info("Message<<< " + msg + " >>>sent to " + regId);
            String canonicalRegId = result.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                LOG.info("Registration Id changed for " + regId + " updating to " + canonicalRegId);
                User userForUpdate = findUserByRegId(regId);
                userForUpdate.setRegId(canonicalRegId);
                ofy().save().entity(userForUpdate).now();
            }
        } else {
            String error = result.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                LOG.warning("Registration Id " + regId + " no longer registered with GCM, removing from datastore");
                //TODO: if the device is no longer registered with Gcm, remove it from the datastore
                // ofy().delete().entity(record).now();
            } else {
                LOG.warning("Error when sending message : " + error);
            }
        }

    }

    @ApiMethod(name = "sendPing")
    public void sendPing(@Named("number") String number, @Named("senderNumber") String senderNumber) throws IOException {

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", "ping from " + senderNumber).build();
        List<User> records = ofy().load().type(User.class).list();
        for (User record : records) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                LOG.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    LOG.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    LOG.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                } else {
                    LOG.warning("Error when sending message : " + error);
                }
            }
        }
    }


    private User findUserByRegId(String regId) {
        return ofy().load().type(User.class).filter("regId", regId).first().now();
    }

    /**
     * Delete existing users with same phone number
     *
     * @param number
     */
    private void deleteByPhoneNumber(String number) {
        List<User> users = ofy().load().type(User.class).filter("phoneNumber", number).list();
        if (users.size() > 0)
            ofy().delete().entities(users);

    }

    private void save(Object entity) {
        ofy().save().entity(entity).now();
    }

    private User findUserById(Long id) {
        LOG.info("findUserById: " + id);
        return ofy().load().type(User.class).id(id).now();
    }

    private String findRegIdByUserId(Long id) {
        LOG.info("findRegIdByUserId: " + id);
        User user = findUserById(id);
        return user != null ? user.getRegId() : null;
    }

    private <T> T getById(Long id, Class<T> type) {

        return ofy().load().type(type).id(id).now();
    }

    private void saveMatch(Match match) {
        ofy().save().entity(match).now();
    }

}