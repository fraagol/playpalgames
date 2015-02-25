package com.testapps.wildWistEast;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.playpalgames.library.GameController;
import com.testapps.wildWistEast.buttons.ActionMoveButton;
import com.testapps.wildWistEast.buttons.ActionRechargeButton;
import com.testapps.wildWistEast.buttons.ActionShootButton;
import com.testapps.wildWistEast.buttons.GameButtons;
import com.testapps.wildWistEast.buttons.IActionButton;
import com.testapps.wildWistEast.buttons.IButtonsSubscribed;
import com.testapps.wildWistEast.characters.cowboy.Cowboy;
import com.testapps.wildWistEast.characters.cowboy.CowboyFactory;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameGUI.Lives;
import com.testapps.wildWistEast.gameStates.IGameStates;
import com.testapps.wildWistEast.gameStates.InitGameState;
import com.testapps.wildWistEast.gameStates.MainState;
import com.testapps.wildWistEast.gameStates.selectPositionState.SelectPositionState;
import com.testapps.wildWistEast.gameStates.selectPositionState.SelectorButtonMovePlayer;
import com.testapps.wildWistEast.gameStates.selectRechargeState.SelectRechargeState;
import com.testapps.wildWistEast.gameStates.selectShootState.SelectShootState;
import com.testapps.wildWistEast.turn.TurnAction;

import java.io.IOException;

public class BattleFieldController {
    CowboyFactory cowboyFactory;
    CowboysBand cowboysBand;
    GameButtons gameButtons;

    private IGameStates state;
    private GameController gameController;
    private InitGameState initGameState;
    private MainState mainState;
    private SelectPositionState selectPositionState;
    private SelectShootState selectShootState;
    private SelectRechargeState selectRechargeState;
    private Lives lives;
    private Bullets bullets;
    private BackGround backGround;

    public BattleFieldController(GameController gameController) {
        this.cowboyFactory = new CowboyFactory(gameController.isHost());
        this.cowboysBand = new CowboysBand();
        this.gameButtons = new GameButtons(this);
        this.lives = new Lives();
        this.bullets = new Bullets();
        this.gameController = gameController;
        this.backGround = new BackGround();

        createCowboys();
        GameBoard.initBoard(this.cowboysBand);

        initGameState = new InitGameState(cowboysBand);
        mainState = new MainState(this.gameButtons);
        selectPositionState = new SelectPositionState(this, gameButtons, cowboysBand);
        selectShootState = new SelectShootState(gameButtons, cowboysBand, bullets);
        selectRechargeState = new SelectRechargeState(cowboysBand, bullets);

        initGameState.init();
        state = initGameState;
    }

    private void createCowboys() {
        Cowboy cowboyI = this.cowboyFactory.createMyPlayer();
        Cowboy cowboyOther = this.cowboyFactory.createEnemy();
        this.cowboysBand.addCowboyToBand(cowboyI);
        this.cowboysBand.addCowboyToBand(cowboyOther);
        this.cowboysBand.setMyPlayerID(cowboyI.getID());
    }

    public void render(SpriteBatch batch, float elapsedTime) {
        backGround.render(batch);
        state.render(batch);
        cowboysBand.render(batch);
        gameButtons.render(batch);
        lives.render(batch);
        bullets.render(batch);
    }

    public void buttonPressed(IActionButton actionBttn) {
        if(actionBttn instanceof ActionMoveButton)
        {
            state = this.selectPositionState;
        }
        else if(actionBttn instanceof ActionShootButton)
        {
            state = this.selectShootState;
        }
        else if(actionBttn instanceof ActionRechargeButton)
        {
            state = this.selectRechargeState;
        }
        state.init();
    }

    public void buttonPressed(IButtonsSubscribed buttonSubscribed) {

        if(buttonSubscribed instanceof SelectorButtonMovePlayer)
        {
            state = this.mainState;
        }
        state.init();

        try {
            gameController.sendTurn(new TurnAction(buttonSubscribed.getAction(),
                    cowboysBand.getMyCowboy().getID(),
                    buttonSubscribed.getBoardPos()), null, false);
            gameController.setMyTurn(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleNewTurn(TurnAction turnAction) {
        if(turnAction.getAction() == TurnAction.Action.MOVE) {
            cowboysBand.getCowboy(turnAction.getPlayer()).moveTo(turnAction.getTarget());
        }
        else if(turnAction.getAction() == TurnAction.Action.SHOOT) {
            cowboysBand.getCowboy(turnAction.getPlayer()).shootTo(turnAction.getTarget());
        }
        else if(turnAction.getAction() == TurnAction.Action.RELOAD) {
            cowboysBand.getCowboy(turnAction.getPlayer()).rechargeGun();
        }

        state = this.mainState;
        state.init();
    }

    public void dispose() {
        initGameState.dispose();
        mainState.dispose();
        selectPositionState.dispose();

        cowboyFactory.dispose();
        cowboysBand.dispose();
        gameButtons.dispose();
    }
}
