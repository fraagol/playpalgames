package com.playpalgames.app.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.playpalgames.app.E;
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
import java.util.ArrayList;
import java.util.Date;

import static com.playpalgames.app.game.PenaltyTurn.LEFT;
import static com.playpalgames.app.game.PenaltyTurn.RIGHT;

@EActivity
public class KickGameActivity extends Activity implements GameClient {
    GameController gameController = null;

    private static final int STATE_SHOOTER_LOOK = 1;
    private static final int STATE_SHOOTER_SHOOT = 2;
    private static final int STATE_SHOOTER_WAIT = 3;
    private static final int STATE_SHOOTER_END = 4;
    private static final int STATE_KEEPER_WAIT = 5;
    private static final int STATE_KEEPER_SAVE = 6;
    private static final int STATE_KEEPER_END = 7;

    private static final int HOST_SHOOTS = 0;
    private static final int GUEST_SAVES = 1;
    private static final int GUEST_SHOOTS = 2;
    private static final int HOST_SAVES = 3;

    private static final int SAVE = 0;
    private static final int GOAL = 1;


    SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
    @ViewById(R.id.logTextView)
    TextView logTextView;
    String auxLog = "";

    @ViewById
    Button leftButton;

    @ViewById
    Button rightButton;

    @ViewById
    Button endButton;

    @ViewById
    TextView scoreP1Text;

    @ViewById
    TextView scoreP2Text;

    @ViewById
    TableRow scoreP1Row;

    @ViewById
    TextView opponentNameText;

    CountDownTimer timer;


    @ViewById
    TableRow scoreP2Row;

    @ViewById(R.id.questionText)
    TextView questionText;

    String questionAux = null;

    private PenaltyTurn localTurn, remoteTurn;
    private int state = 0;
    private boolean iShoot;
    private int round = 1;
    private int shoot = 0;
    private int localScore = 0;
    private int remoteScore = 0;
    private boolean end;
    boolean waitingTurn = false;
    private ArrayList<Integer> localGoals = new ArrayList<Integer>();
    private ArrayList<Integer> remoteGoals = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

    }


    @AfterViews
    void afterViews() {
        logTextView.setMovementMethod(new ScrollingMovementMethod());
        try {
            initGame();
        } catch (IOException e) {
            E.manage(e);
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void initGame() throws IOException {
        gameController = GameController.getInstance();
        gameController.addGameClientListener(this);
        gameController.initMatch();
        localTurn = new PenaltyTurn();
        remoteTurn = new PenaltyTurn();
        timer = new CountDownTimer(300000, 20000) {
            public void onTick(long millisUntilFinished) {
                checkForTurns();
            }

            public void onFinish() {
            }
        };

        opponentNameText.setText(gameController.getOpponentName());
        if (gameController.isNewGame()) {
            log("Comienza la tanda de penalties");
            iShoot = amIHost();
            waitingTurn = !iShoot;
            turn();
        } else {
            //get last turn and load state
            loadGame();
        }

    }

    @Background
    void loadGame() {
        String state = null;
        try {
            state = gameController.getStateAndLastTurn();
        } catch (IOException e) {
            E.manage(e);
            e.printStackTrace();
            log(e.getMessage());
        }
        loadState(state);
    }

    private void loadState(String state) {
        String[] fields = state.split("\\|");
        int i = 0;
        this.round = Integer.valueOf(fields[i++]);
        this.shoot = Integer.valueOf(fields[i++]);
        setHostScore(Integer.valueOf(fields[i++]));
        setGuestScore(Integer.valueOf(fields[i++]));
        String hGoals = fields[i++];
        String gGoals = fields[i++];
        setGoals(hGoals, gGoals);
        setTurn(Integer.valueOf(fields[i++]));
    }


    private String produceStateString() {
        StringBuilder state = new StringBuilder();
        state.append(round).append("|")
                .append(shoot).append("|")
                .append(hostScore()).append("|")
                .append(guestScore()).append("|")
                .append(hostGoals()).append("|")
                .append(guestGoals()).append("|")
                .append(nextTurn());

        return state.toString();
    }


    void setGoals(String hostGoals, String guestGoals) {
        drawScore(scoreP1Row, amIHost() ? hostGoals : guestGoals, localGoals);
        drawScore(scoreP2Row, amIHost() ? guestGoals : hostGoals, remoteGoals);
    }

    @UiThread
    void drawScore(TableRow row, String string, ArrayList<Integer> goalsList) {
        String[] goals = string.split("-");
        if (goals != null) {
            for (int i = 0; i < goals.length; i++) {
                if (goals[i].equals(String.valueOf(GOAL))) {
                    row.addView(getGoalImageView());
                    goalsList.add(GOAL);
                } else if (goals[i].equals(String.valueOf(SAVE))) {
                    row.addView(getSaveImageView());
                    goalsList.add(SAVE);
                }
            }
        }
        scoreP1Text.setText(String.valueOf(localScore));
        scoreP2Text.setText(String.valueOf(remoteScore));
    }

    void setHostScore(int score) {
        if (amIHost()) {
            localScore = score;

        } else {
            remoteScore = score;
        }
    }

    void setGuestScore(int score) {
        if (amIHost()) {
            remoteScore = score;
        } else {
            localScore = score;
        }
    }

    private int hostScore() {
        return amIHost() ? localScore : remoteScore;
    }

    private int guestScore() {
        return amIHost() ? remoteScore : localScore;
    }

    private String hostGoals() {
        return goalsArraylistToString(amIHost() ? localGoals : remoteGoals);
    }

    private String guestGoals() {
        return goalsArraylistToString(amIHost() ? remoteGoals : localGoals);
    }

    private int nextTurn() {
        if (iShoot) {
            return amIHost() ? GUEST_SAVES : HOST_SAVES;
        } else {
            return amIHost() ? HOST_SHOOTS : GUEST_SHOOTS;
        }

    }

    private void setTurn(int t) {
        switch (t) {
            case HOST_SHOOTS:
                if (amIHost()) {
                    iShoot = false;
                    state = STATE_KEEPER_END;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    end = processResult();
                    if (end) {
                        finishMatch();
                    } else {
                        turn();
                    }
                } else {
                    state = STATE_SHOOTER_WAIT;
                    iShoot = true;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    processTurn(localTurn);
                }
                break;
            case GUEST_SHOOTS:
                if (amIHost()) {
                    state = STATE_SHOOTER_WAIT;
                    iShoot = true;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    processTurn(localTurn);

                } else {
                    iShoot = false;
                    state = STATE_KEEPER_END;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    end = processResult();
                    if (end) {
                        finishMatch();
                    } else {
                        turn();
                    }
                }
                break;

            case HOST_SAVES:
                if (amIHost()) {
                    state = STATE_KEEPER_WAIT;
                    iShoot = false;
                    turnAvailable();
                } else {
                    state = STATE_SHOOTER_WAIT;
                    timer.start();
                    iShoot = true;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    enableButtons(false);
                }
                break;

            case GUEST_SAVES:
                if (amIHost()) {
                    state = STATE_SHOOTER_WAIT;
                    timer.start();
                    iShoot = true;
                    localTurn = gameController.<PenaltyTurn>getNextTurn(localTurn);
                    enableButtons(false);

                } else {
                    state = STATE_KEEPER_WAIT;
                    iShoot = false;
                    turnAvailable();
                }
                break;
        }
    }


    @Background
    void setAction(int direction) {
        try {
            switch (state) {
                case STATE_SHOOTER_LOOK:
                    log("Miras a la " + translate(direction) + " ¿y hacia donde tiras?");
                    localTurn.setLook(direction);
                    state = STATE_SHOOTER_SHOOT;

                    break;
                case STATE_SHOOTER_SHOOT:
                    enableButtons(false);
                    log("Tiras a la " + translate(direction) + ", esperando al portero...");
                    localTurn.setShoot(direction);

                    sendTurn(localTurn, true);
                    state = STATE_SHOOTER_WAIT;
                    timer.start();
                    break;
                case STATE_KEEPER_SAVE:
                    enableButtons(false);
                    log("Te tiras a la " + translate(direction) + " y...");
                    localTurn.setSave(direction);
                    sendTurn(localTurn, false);
                    state = STATE_KEEPER_END;
                    end = processResult();
                    if (end) {
                        finishMatch();
                    } else {
                        turn();
                    }
                    break;
                default:
                    log("Error, acción en estado incorrecto " + state);
            }
        } catch (IOException e) {
            E.manage(e);
            log(e.getMessage());
        }

    }

    void sendTurn(PenaltyTurn t, boolean opponentIsNext) throws IOException {
        gameController.sendTurn(localTurn, produceStateString(), opponentIsNext);
    }

    void finishMatch() {
        log("Has " + (localScore > remoteScore ? "GANADO!" : "PERDIDO!"), true);
        timer.cancel();
        enableButtons(false);
        enableEndButton();

    }

    void turn() {
        timer.cancel();
        shoot++;
        if (iShoot) {
            log("Te toca tirar, ¿hacia donde miras?", true);
            state = STATE_SHOOTER_LOOK;
            enableButtons(true);
        } else {

            log("Eres el portero, esperando al lanzador", true);
            timer.start();
            state = STATE_KEEPER_WAIT;
            enableButtons(false);
        }
    }

    @Override
    public void turnAvailable() {
        timer.cancel();
        remoteTurn = gameController.<PenaltyTurn>getNextTurn(remoteTurn);
        processTurn(remoteTurn);

    }

    void processTurn(PenaltyTurn turn) {
        switch (state) {
            case STATE_KEEPER_WAIT:
                log("El delantero mira a la " + translate(turn.getLook()) + " ¿Hacia donde te lanzas?");
                localTurn.setShoot(turn.getShoot());
                enableButtons(true);
                state = STATE_KEEPER_SAVE;
                break;
            case STATE_SHOOTER_WAIT:
                localTurn.setSave(turn.getSave());
                state = STATE_SHOOTER_END;
                end = processResult();
                if (end) {
                    finishMatch();
                } else {
                    turn();
                }
                break;
            default:
                log("Error, turno" + turn.dataToString() + " en estado " + state + " inválido");
        }
    }


    boolean processResult() {
        switch (state) {
            case STATE_KEEPER_END:
                //I'm de goalkeeper
                if (goal(localTurn)) {
                    log("GOL!!!");
                    remoteScore++;
                    remoteGoal();
                } else {
                    log("PARADA!!!");
                    remoteFail();
                }

                break;
            case STATE_SHOOTER_END:
                //I'm de shooter
                if (goal(localTurn)) {
                    log("GOL!!!");
                    localScore++;
                    localGoal();
                } else {
                    log("PARADA!!!");
                    localFail();
                }
                break;
        }
        if (endOfRound()) {
            round++;
        }
        //Change shooter/keeper for next round

        iShoot = !iShoot;
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
        if (amIHost()) {
            return localScore - remoteScore > 6 - round || remoteScore - localScore > 5 - round;
        } else {
            return localScore - remoteScore > 5 - round || remoteScore - localScore > 6 - round;
        }
    }

    private boolean endOfRound() {
        return (shoot % 2) == 0;
    }

    private boolean goal(PenaltyTurn keeper) {
        return keeper.getShoot() != keeper.getSave();
    }

    private String translate(int direction) {
        return direction == LEFT ? "izquierda" : "derecha";
    }


    private String goalsArraylistToString(ArrayList<Integer> arrayList) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : arrayList) {
            sb.append(i).append("-");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    private boolean amIHost() {
        return gameController.isHost();
    }


    private ImageView getGoalImageView() {
        return getImageView(R.drawable.ball);
    }

    private ImageView getSaveImageView() {
        return getImageView(R.drawable.x);
    }

    private ImageView getImageView(int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(id);
        return imageView;
    }

    @UiThread
    void toast(String message) {
        Toast.makeText(KickGameActivity.this, message, Toast.LENGTH_SHORT).show();

    }

    @Background
    void checkForTurns() {
        try {
            if (!gameController.getTurnsFromServer()) {
                toast("Esperando...");
            }
        } catch (IOException e) {
            E.manage(e);
            toast(e.getMessage());
        }

    }

    @UiThread
    void remoteGoal() {
        scoreP2Text.setText(String.valueOf(remoteScore));
        scoreP2Row.addView(getGoalImageView());
        remoteGoals.add(GOAL);
    }

    @UiThread
    void localGoal() {
        scoreP1Text.setText(String.valueOf(localScore));
        scoreP1Row.addView(getGoalImageView());
        localGoals.add(GOAL);

    }

    @UiThread
    void localFail() {
        scoreP1Text.setText(String.valueOf(localScore));
        scoreP1Row.addView(getSaveImageView());
        localGoals.add(SAVE);
    }

    @UiThread
    void remoteFail() {
        scoreP1Text.setText(String.valueOf(localScore));
        scoreP2Row.addView(getSaveImageView());
        remoteGoals.add(SAVE);
    }

    @UiThread
    void enableButtons(boolean enable) {
        waitingTurn = !enable;
        leftButton.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        rightButton.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);

    }

    @UiThread
    void log(String msg, boolean keep) {

        if (keep) {
            questionAux += msg + "\n";
        } else {
            questionAux = msg + "\n";
        }
        questionText.setText(questionAux);

        Date now = new Date();
        String strDate = sdfDate.format(now);
        msg = strDate + " " + msg;
        Log.i("####@", msg);
        auxLog += msg + "\n";
        if (logTextView != null) {
            logTextView.setText(auxLog);

        }


    }

    void log(String msg) {
        log(msg, false);

    }

    @Click
    void endButton() {
        endMatch();
    }

    @Background
    void endMatch() {
        try {
            gameController.endMatch();
            finish();
        } catch (IOException e) {
            E.manage(e);
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    @UiThread
    void enableEndButton() {
        endButton.setVisibility(View.VISIBLE);
    }

    @Click
    void leftButton() {
        setAction(LEFT);
    }

    @Click
    void rightButton() {
        setAction(RIGHT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameController.exitMatch();
        timer.cancel();
    }
}
