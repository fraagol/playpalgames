package com.testapps.wildWistEast;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.playpalgames.library.GameController;
import com.testapps.wildWistEast.buttons.ActionMoveButton;
import com.testapps.wildWistEast.buttons.ActionRechargeButton;
import com.testapps.wildWistEast.buttons.ActionShootButton;
import com.testapps.wildWistEast.buttons.GameButtons;
import com.testapps.wildWistEast.buttons.IActionButton;
import com.testapps.wildWistEast.characters.cowboy.Cowboy;
import com.testapps.wildWistEast.characters.cowboy.CowboyFactory;
import com.testapps.wildWistEast.characters.cowboy.CowboysBand;
import com.testapps.wildWistEast.gameGUI.Bullets;
import com.testapps.wildWistEast.gameGUI.Lives;
import com.testapps.wildWistEast.gameStates.ActionMessage;
import com.testapps.wildWistEast.gameStates.IGameStates;
import com.testapps.wildWistEast.gameStates.InitGameState;
import com.testapps.wildWistEast.gameStates.MainState;
import com.testapps.wildWistEast.gameStates.selectPositionState.SelectPositionState;
import com.testapps.wildWistEast.gameStates.selectReloadState.SelectReloadState;
import com.testapps.wildWistEast.gameStates.selectShootState.SelectShootState;
import com.testapps.wildWistEast.messageHandler.MssgHandler;
import com.testapps.wildWistEast.turn.TurnAction;

import java.io.IOException;

public class BattleFieldController {
    CowboyFactory cowboyFactory;
    CowboysBand cowboysBand;
    GameButtons gameButtons;

    private IGameStates state;
    private MessageController messageController;
    private InitGameState initGameState;
    private MainState mainState;
    private SelectPositionState selectPositionState;
    private SelectShootState selectShootState;
    private SelectReloadState selectReloadState;
    private Lives lives;
    private Bullets bullets;
    private BackGround backGround;
    private MssgHandler mssgHandler;

    public BattleFieldController(MessageController messageController, boolean host) {
        this.cowboyFactory = new CowboyFactory(host);
        this.cowboysBand = new CowboysBand();
        this.gameButtons = new GameButtons(this);
        this.lives = new Lives();
        this.bullets = new Bullets();
        this.messageController = messageController;
        this.backGround = new BackGround();

        createCowboys();
        GameBoard.initBoard(this.cowboysBand);

        initGameState = new InitGameState(cowboysBand);
        mainState = new MainState(this.gameButtons);
        selectPositionState = new SelectPositionState(this, gameButtons, cowboysBand);
        selectShootState = new SelectShootState(this, gameButtons, cowboysBand, bullets);
        selectReloadState = new SelectReloadState(this, cowboysBand, bullets);
        mssgHandler = new MssgHandler(cowboysBand, lives);

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

    public void render(SpriteBatch batch) {
        backGround.render(batch);
        state.render(batch);
        cowboysBand.render(batch);

        if(messageController.getMyTurn() != null) {
            gameButtons.hideMenuButtons();
        } else {
            gameButtons.showMenuButtons();
            gameButtons.render(batch);
        }

        if(messageController.everybodyReady()) {
            mssgHandler.handle(messageController.getMyTurn());
            mssgHandler.handle(messageController.getEnemyTurn());
            messageController.reset();
            state = this.mainState;
            state.init();
        }

        lives.render(batch);
        bullets.render(batch);
    }

    public void buttonPressed(IActionButton actionBttn) {
        if (actionBttn instanceof ActionMoveButton) {
            state = this.selectPositionState;
        } else if (actionBttn instanceof ActionShootButton) {
            state = this.selectShootState;
        } else if (actionBttn instanceof ActionRechargeButton) {
            state = this.selectReloadState;
        }
        state.init();
    }

    public void buttonPressed(ActionMessage actionMessage) {
        state = this.mainState;
        state.init();

        messageController.iDoAction(new TurnAction(actionMessage.getTurnAction(),
                cowboysBand.getMyCowboy().getID(),
                actionMessage.getBoardPos()));
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
