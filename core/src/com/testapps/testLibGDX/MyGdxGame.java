package com.testapps.testLibGDX;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.playpalgames.library.GameClient;
import com.playpalgames.library.GameController;


public class MyGdxGame implements ApplicationListener, GameClient {
    private SpriteBatch batch;
    private float elapsedTime = 0;
    private BattleFieldController battleField;
    private GameController gameController;
    private TurnAction turnAction = new TurnAction();
    private boolean newTurnAvailable = false;



	@Override
	public void create () {
        batch = new SpriteBatch();
        gameController= GameController.getInstance();
        this.battleField = new BattleFieldController(gameController);
        battleField.create();

        //TODO: set up game(cowboys position, first player to move...) according to player's role: host or guest
        boolean amIHost= gameController.isHost();

	}

    @Override
    public void resize(int width, int height) {

    }

    @Override
	public void render () {
        if(!gameController.isMyTurn()) {
            //TODO:DISABLE BUTTONS!!!

            //Not my turn, check if a turn is available
            if (newTurnAvailable) {
                this.battleField.handleNewTurn(turnAction);
                newTurnAvailable = false;
                gameController.setMyTurn(true);
            }
        }
        Gdx.gl.glClearColor(255f/255.0f, 205f/255.0f, 124f/255.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();

        batch.begin();
        this.battleField.render(batch, elapsedTime);
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
        turnAction = gameController.<TurnAction>getNextTurn(turnAction);
        newTurnAvailable = true;
    }
}
