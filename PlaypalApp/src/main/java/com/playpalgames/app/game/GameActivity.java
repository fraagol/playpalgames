package com.playpalgames.app.game;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.playpalgames.app.R;
import com.playpalgames.library.GameClient;
import com.playpalgames.library.GameController;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.playpalgames.app.game.PenaltyTurn.LEFT;
import static com.playpalgames.app.game.PenaltyTurn.RIGHT;

@EActivity
public class GameActivity extends Activity implements GameClient {
    GameController gameController = null;

    private static final int STATE_SHOOTER_LOOK = 1;
    private static final int STATE_SHOOTER_SHOOT = 2;
    private static final int STATE_SHOOTER_WAIT = 3;
    private static final int STATE_SHOOTER_END = 4;
    private static final int STATE_KEEPER_WAIT = 5;
    private static final int STATE_KEEPER_SAVE = 6;
    private static final int STATE_KEEPER_END = 7;


    private PenaltyTurn localTurn, remoteTurn;
    private int state = 0;
    private boolean iShoot;
    private int round = 1;
    private int shoot = 0;
    private int localScore = 0;
    private int remoteScore = 0;
    private boolean end;


    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    @ViewById
    Button leftButton;

    @ViewById
    Button rightButton;

    @ViewById
    TextView scoreP1Text;

    @ViewById
    TextView scoreP2Text;

    @ViewById
    TableRow scoreP1Row;


    @ViewById
    TableRow scoreP2Row;



    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


    }

    @AfterViews
    void afterViews() {
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        initGame();
    }

    private void initGame() {
        gameController = GameController.getInstance();
        gameController.addGameClientListener(this);
        log("Comienza la tanda de penalties");
        iShoot = gameController.isHost();
        localTurn = new PenaltyTurn();
        remoteTurn= new PenaltyTurn();
        turn();
    }

    @Background
    void setAction(int direction) {
        try {
            switch (state) {
                case STATE_SHOOTER_LOOK:
                    log("Miras a la "+translate(direction)+ " ¿y hacia donde tiras?");
                    localTurn.setLook(direction);
                    state = STATE_SHOOTER_SHOOT;

                    break;
                case STATE_SHOOTER_SHOOT:
                    enableButtons(false);
                    log("Tiras a la "+translate(direction)+", esperando al portero...");
                    localTurn.setShoot(direction);
                    gameController.sendTurn(localTurn);
                    state = STATE_SHOOTER_WAIT;
                    break;
                case STATE_KEEPER_SAVE:
                    enableButtons(false);
                    log("Te tiras a la "+translate(direction)+" y...");
                    localTurn.setSave(direction);
                    gameController.sendTurn(localTurn);
                    state = STATE_KEEPER_END;
                    end = processResult();
                    if (end) {
                        finishMatch();
                    }else{
                        turn();
                    }
                    break;
                default:
                    log("Error, acción en estado incorrecto " + state);
            }
        } catch (IOException e) {
            log(e.getMessage());
        }

    }

    void finishMatch() {
        log("Has " + (localScore > remoteScore ? "GANADO!" : "PERDIDO!"));
    }

    void turn() {

        shoot++;
        if (iShoot) {
            enableButtons(true);
            log("Te toca tirar, ¿hacia donde miras?");
            state = STATE_SHOOTER_LOOK;
            enableButtons(true);
        } else {

            log("Eres el portero, esperando al lanzador");
            state = STATE_KEEPER_WAIT;
            enableButtons(false);
        }
    }

    @Override
    public void turnAvailable() {
        remoteTurn = gameController.<PenaltyTurn>getNextTurn(remoteTurn);
        switch (state) {
            case STATE_KEEPER_WAIT:
                log("El delantero mira a la " + translate(remoteTurn.getLook())+" ¿Hacia donde te lanzas?");

                enableButtons(true);
                state = STATE_KEEPER_SAVE;
                break;
            case STATE_SHOOTER_WAIT:
                state = STATE_SHOOTER_END;
                end = processResult();
                if (end) {
                    finishMatch();
                }else{

                    turn();
                }
                break;
            default:
                log("Error, turno recibido en estado " + state + " inválido");
        }

    }

    @UiThread
    void remoteGoal() {
        scoreP2Text.setText(String.valueOf(remoteScore));
        ImageView picture = new ImageView(this);
        picture.setImageResource(R.drawable.ball);
        scoreP2Row.addView(picture);
    }

    @UiThread
    void localGoal() {
        scoreP1Text.setText(String.valueOf(localScore));
        ImageView picture = new ImageView(this);
       picture.setImageResource(R.drawable.ball);
      scoreP1Row.addView(picture);

    }

@UiThread
    void localFail() {
        scoreP1Text.setText(String.valueOf(localScore));
        ImageView picture = new ImageView(this);
        picture.setImageResource(R.drawable.x);
        scoreP1Row.addView(picture);
    }

    @UiThread
    void remoteFail() {
        scoreP1Text.setText(String.valueOf(localScore));
        ImageView picture = new ImageView(this);
        picture.setImageResource(R.drawable.x);
        scoreP2Row.addView(picture);
    }

    boolean processResult() {
        switch (state) {
            case STATE_KEEPER_END:
                //I'm de goalkeeper
                if (goal(remoteTurn, localTurn)) {
                    log("GOL!!! El tiro iba por la "+ translate(remoteTurn.getShoot()));
                    remoteScore++;
                    remoteGoal();
                } else{
                    log("PARADA!!! El tiro iba por la "+ translate(remoteTurn.getShoot()));
                    remoteFail();
                }

                break;
            case STATE_SHOOTER_END:
                //I'm de shooter
                if (goal(localTurn, remoteTurn)) {
                    log("GOL!!! El portero se ha tirado a la "+ translate(remoteTurn.getSave()));
                    localScore++;
                    localGoal();
                }else{
                    log("PARADA!!! El portero se ha tirado por la "+ translate(remoteTurn.getSave()));
                    localFail();
                }
                break;
        }
        if (endOfRound()) {
            round++;
        }
        //Change shooter/keeper for next round

        iShoot=!iShoot;
        return checkVictory();
    }

    private boolean checkVictory() {
        if (localScore == remoteScore)
            return false;

        //different score
        if (round > 5)
            return endOfRound();

        //before 5th round
        if (endOfRound())
            return Math.abs(localScore - remoteScore) > 6 - round;
        if (gameController.isHost()) {
            return localScore - remoteScore > 6 - round || remoteScore - localScore > 5 - round;
        } else {
            return localScore - remoteScore > 5 - round || remoteScore - localScore > 6 - round;
        }
    }

    private boolean endOfRound() {
        return (shoot % 2) == 0;
    }

    private boolean goal(PenaltyTurn shooter, PenaltyTurn keeper) {
        return shooter.getShoot() != keeper.getSave();
    }

    private String translate(int direction) {
        return direction == LEFT ? "izquierda" : "derecha";
    }

    @UiThread
    void enableButtons(boolean enable) {
        leftButton.setVisibility(enable ? View.VISIBLE:View.INVISIBLE);
        rightButton.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);

    }

    @UiThread
    void log(String msg) {

        Date now = new Date();
        String strDate = sdfDate.format(now);
        msg = strDate + " " + msg;
        Log.e("####@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }
    }

    @Click
    void leftButton() {
        setAction(LEFT);
    }

    @Click
    void rightButton() {
        setAction(RIGHT);
    }

    @Click
    void testButton() {
        localGoal();
    }

}
