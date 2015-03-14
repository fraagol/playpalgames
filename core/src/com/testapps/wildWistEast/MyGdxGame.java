package com.testapps.wildWistEast;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.playpalgames.library.GameClient;
import com.playpalgames.library.GameController;
import com.testapps.wildWistEast.turn.TurnAction;


public class MyGdxGame implements ApplicationListener, GameClient {
    private SpriteBatch batch;
    private float elapsedTime = 0;
    private BattleFieldController battleField;
    private GameController gameController;
    private MessageController messageController;



	@Override
	public void create () {
        batch = new SpriteBatch();
        gameController= GameController.getInstance();
        gameController.initMatch();
        gameController.addGameClientListener(this);
        messageController = new MessageController(gameController);
        this.battleField = new BattleFieldController(messageController, gameController.isHost());
	}

    @Override
    public void resize(int width, int height) {

    }

    @Override
	public void render () {
        Gdx.gl.glClearColor(255f/255.0f, 205f/255.0f, 124f/255.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();

        batch.begin();
        this.battleField.render(batch);
        batch.end();
	}


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

        batch.dispose();
        this.battleField.dispose();
    }

    @Override
    /**
     * Called by the framework when a new Turn is available
     */
    public void turnAvailable() {
        TurnAction action = new TurnAction();
        action = gameController.<TurnAction>getNextTurn(action);
        messageController.setEnemyTurn(action);
    }
}
