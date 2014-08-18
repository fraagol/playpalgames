package com.testapps.testLibGDX;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.testapps.testLibGDX.buttons.ActionMoveButton;
import com.testapps.testLibGDX.buttons.ActionShootButton;
import com.testapps.testLibGDX.buttons.GameButtons;
import com.testapps.testLibGDX.buttons.IActionButton;
import com.testapps.testLibGDX.buttons.IButtonsSubscribed;
import com.testapps.testLibGDX.characters.cowboy.Cowboy;
import com.testapps.testLibGDX.characters.cowboy.CowboyFactory;
import com.testapps.testLibGDX.characters.cowboy.CowboysBand;
import com.testapps.testLibGDX.gameGUI.Lifes;
import com.testapps.testLibGDX.gameStates.InitGameState;
import com.testapps.testLibGDX.gameStates.IGameStates;
import com.testapps.testLibGDX.gameStates.MainState;
import com.testapps.testLibGDX.gameStates.selectPositionState.SelectPositionState;
import com.testapps.testLibGDX.gameStates.selectPositionState.SelectorButtonMovePlayer;
import com.testapps.testLibGDX.gameStates.selectShootState.SelectShootState;

public class BattleFieldController {
    CowboyFactory cowboyFactory;
    CowboysBand cowboysBand;
    GameButtons gameButtons;

    private IGameStates state;
    private InitGameState initGameState;
    private MainState mainState;
    private SelectPositionState selectPositionState;
    private SelectShootState selectShootState;
    private Lifes lifes;

    public BattleFieldController() {
        cowboyFactory = new CowboyFactory();
        cowboysBand = new CowboysBand();
        gameButtons = new GameButtons(this);
        lifes = new Lifes();
    }

    public void create() {
        createCowboys();

        GameBoard.initBoard(this.cowboysBand);

        initGameState = new InitGameState(cowboysBand);
        mainState = new MainState(this.gameButtons);
        selectPositionState = new SelectPositionState(this, gameButtons, cowboysBand);
        selectShootState = new SelectShootState(gameButtons, cowboysBand);


        initGameState.init();
        state = initGameState;
    }

    private void createCowboys() {
        Cowboy cowboyI = this.cowboyFactory.createMyPlayer();
        Cowboy cowboyOther = this.cowboyFactory.createEnemy();
        this.cowboysBand.addCowboyToBand(cowboyI);
        this.cowboysBand.addCowboyToBand(cowboyOther);
    }

    public void render(SpriteBatch batch, float elapsedTime) {
        state.render(batch);
        cowboysBand.render(batch);
        gameButtons.render(batch);
        lifes.render(batch);
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
        state.init();
    }

    public void buttonPressed(IButtonsSubscribed buttonSubscribed) {
        if(buttonSubscribed instanceof SelectorButtonMovePlayer)
        {
            state = this.mainState;
        }

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