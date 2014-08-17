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
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.playpalgames.backend.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(name = "gameEndpoint", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.playpalgames.com", ownerName = "backend.playpalgames.com", packagePath = ""))
public class GameEndpoint {
private static String LOCK="LOCK";
    private static final Logger LOG = Logger.getLogger(GameEndpoint.class.getName());

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = "AIzaSyDqfPILfQJFKacnom3NVqNsd_WaVjbMALM";

    /** This inserts a new <code>Turn</code> object.
     *
     * @param turn The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertTurn")
    public Turn insertTurn(Turn turn) {
        Turn lastTurn;
       synchronized (LOCK) {
           LOG.info(this.toString()+" "+Thread.currentThread().getId());
           lastTurn = ofy().load().type(Turn.class).filter("matchId", turn.getMatchId()).order("-turnNumber").first().now();
           turn.setTurnNumber(lastTurn != null ? lastTurn.getTurnNumber() + 1 : 0);
           ofy().save().entity(turn).now();

//           try {
//               if(turn.getTurnNumber()%2==0)
//                   Thread.sleep(3000);
//           } catch (InterruptedException e) {
//               e.printStackTrace();
//           }
       }
        if (lastTurn!=null) {
            LOG.info("Calling insertTurn method. Last turn number: " + lastTurn.getTurnNumber() + " id " + lastTurn.getId());
        }else{
            LOG.info("Calling insertTurn method first time.");
        }
        return turn;
    }

    /**This creates a new Match
     *
     * @return The match created
     */
    @ApiMethod(name = "createMatch", httpMethod = ApiMethod.HttpMethod.GET)
    public Match createMatch(@Named("userId") Long userId) {
        LOG.info("Calling createMatch method");
        Match match = new Match();
        match.setUserId(userId);
        match.getPlayers().add(findUserById(userId));
        saveMatch(match);
        LOG.info("Match created with id "+match.getId());
        return match;
    }

    @ApiMethod(name = "joinMatch", httpMethod = ApiMethod.HttpMethod.GET)
    public Match joinMatch(@Named("userId") Long userId, @Named("matchId") Long matchId ) throws IOException {
        LOG.info("Calling joinMatch ");
        Match match = getById(matchId,Match.class);
        User user= getById(userId,User.class);
        match.getPlayers().add(user);
        saveMatch(match);
        //Send accepted to Match Creator
        sendMessage("A "+matchId,findUserById(match.getUserId()).getRegId());
        return match;
    }

    @ApiMethod(name = "listTurns")
    public Turn[] listTurns(@Named("matchId") Long matchId) {
        LOG.info("ListTurns called");
        List<Turn> turns = ofy().load().type(Turn.class).filter("matchId", matchId).order("-turnNumber").list();
        return turns.toArray(new Turn[turns.size()]);
    }

    @ApiMethod (name = "listTurnsFrom")
    public Turn[] listTurnsFrom(@Named("matchId") Long matchId, @Named("from") Long from) {
        LOG.info("ListTurns from "+from+" called");
        List<Turn> turns = ofy().load().type(Turn.class).filter("matchId", matchId).filter("turnNumber >",from).order("turnNumber").list();
        return turns.toArray(new Turn[turns.size()]);
    }

    /**
     * Register a device to the backend
     *
     *
     */
    @ApiMethod(name = "register")
    public User registerDevice(User user) {
        String regId=user.getRegId();
        User existingUser= findUserByRegId(regId);
        if(existingUser != null) {
            LOG.info("Device " + regId + " already registered, skipping register");
            return existingUser;
        }
        //PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();


       // deleteByPhoneNumber(user.getPhoneNumber());
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
        if(record == null) {
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
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    @ApiMethod(name = "sendMessage")
    public void sendMessage(@Named("message") String message,@Named("regId") String regId) throws IOException {
        if(message == null || message.trim().length() == 0) {
            LOG.warning("Not sending message because it is empty");
            return;
        }

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();


            Result result = sender.send(msg, regId, 5);
            if (result.getMessageId() != null) {
                LOG.info("Message<<< "+msg+ " >>>sent to " + regId);
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    LOG.info("Registration Id changed for " + regId + " updating to " + canonicalRegId);
                    User userForUpdate= findUserByRegId(regId);
                    userForUpdate.setRegId(canonicalRegId);
                    ofy().save().entity(userForUpdate).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    LOG.warning("Registration Id " + regId + " no longer registered with GCM, removing from datastore");
                    //TODO: if the device is no longer registered with Gcm, remove it from the datastore
                   // ofy().delete().entity(record).now();
                }
                else {
                    LOG.warning("Error when sending message : " + error);
                }
            }

    }

    @ApiMethod(name = "sendPing")
    public void sendPing(@Named("number") String number, @Named("senderNumber") String senderNumber) throws IOException {

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", "ping from "+ senderNumber).build();
        List<User> records = ofy().load().type(User.class).list();
        List<User> records2 = ofy().load().type(User.class).filter("phoneNumber",number).list();
        for(User record : records) {
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
                }
                else {
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
     * @param number
     */
    private void deleteByPhoneNumber(String number) {
        List<User> users = ofy().load().type(User.class).filter("phoneNumber", number).list();
        if(users.size()>0)
            ofy().delete().entities(users);

    }

    private User findUserById(Long id){
        return ofy().load().type(User.class).id(id).now();
    }

    private <T> T getById(Long id, Class<T> type) {
        return ofy().load().type(type).id(id).now();
    }

    private void saveMatch(Match match){
        ofy().save().entity(match).now();
    }

}