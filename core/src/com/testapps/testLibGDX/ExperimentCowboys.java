package com.testapps.testLibGDX;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.playpalgames.library.GameController;


public class ExperimentCowboys implements ApplicationListener {
    private SpriteBatch batch;
    private float elapsedTime = 0;
    private BattleFieldController battleField;
    private GameController gameController;



    @Override
    public void create () {
        batch = new SpriteBatch();
        this.battleField = new BattleFieldController();
        battleField.create();
        gameController=GameController.getInstance();

        //TODO: set up game(cowboys position, first player to move...) according to player's role: host or guest
        //boolean amIHost= gameController.isHost();

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
}
