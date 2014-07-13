package com.testapps.testLibGDX.characters.cowboy.views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.testapps.testLibGDX.characters.cowboy.CowboyOrientation;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyAnimation;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyShootingDown;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyShootingLeft;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyShootingRight;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyShootingUp;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyWalkingLeft;
import com.testapps.testLibGDX.characters.cowboy.animations.CowboyWalkingRight;

public class CowboyView {

    private final float FRAME_DURATION = 1/15f;

    private Texture texture;
    private CowboyAnimation animation;
    private TextureRegion stoppedFrame;
    protected Vector2 pos;
    protected int width;
    protected int height;
    protected Texture textureDead;


    public CowboyView(Texture texture) {
        this.texture = texture;
        pos = new Vector2(0, 0);
        this.stop(new CowboyOrientation(CowboyOrientation.STOP_N));
        textureDead = new Texture(Gdx.files.internal("gravestone.png"));
    }

    public void render(SpriteBatch batch, float elapsedTime) {
        renderBehindMainPlayer(batch, elapsedTime);
        renderMainPlayer(batch, elapsedTime);
        renderFrontMainPlayer(batch, elapsedTime);


    }
    protected void renderBehindMainPlayer(SpriteBatch batch, float elapsedTime)
    {

    }

    private void renderMainPlayer(SpriteBatch batch, float elapsedTime)
    {
        TextureRegion currentFrame = null;
        if(this.animation != null) {
            currentFrame = this.animation.getFrame(elapsedTime, true);
        }
        else if(this.stoppedFrame != null){
            currentFrame = this.stoppedFrame;
        }

        if(currentFrame != null) {
            batch.draw(currentFrame, pos.x, pos.y);
        }
    }

    protected void renderFrontMainPlayer(SpriteBatch batch, float elapsedTime)
    {

    }

    protected void updateWidthAndHeight(TextureRegion currentFrame) {
        this.width = currentFrame.getRegionWidth();
        this.height = currentFrame.getRegionHeight();
    }

    public void showAnimWalkRight(){
        if(this.animation instanceof CowboyWalkingRight)
          return;
        this.stoppedFrame = null;
        this.animation = new CowboyWalkingRight(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void showAnimWalkLeft(){
        if(this.animation instanceof CowboyWalkingLeft)
            return;
        this.stoppedFrame = null;
        this.animation = new CowboyWalkingLeft(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void showAnimShootUp(){
        if(this.animation instanceof CowboyShootingUp)
            return;
        this.stoppedFrame = null;
        this.animation = new CowboyShootingUp(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void showAnimShootDown(){
        if(this.animation instanceof CowboyShootingDown)
            return;
        this.stoppedFrame = null;
        this.animation = new CowboyShootingDown(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void showAnimShootLeft(){
        if(this.animation instanceof CowboyShootingLeft)
            return;
        this.stoppedFrame = null;
        this.animation = new CowboyShootingLeft(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void showAnimShootRight(){
        if(this.animation instanceof CowboyShootingRight)
            return;
        this.stoppedFrame = null;
        this.animation = new CowboyShootingRight(this.texture, FRAME_DURATION);
        updateWidthAndHeight(this.animation.getFrame(0, false));
    }

    public void stop(CowboyOrientation orientation){
        orientation.setTexture(this.texture);
        this.animation = null;
        this.stoppedFrame = orientation.obtainFrame();
        updateWidthAndHeight(this.stoppedFrame);
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void updateWalkingAnimation(Vector2 currentDirection) {
        if (currentDirection.x < 0)
        {
            this.showAnimWalkLeft();
        }
        else
        {
            this.showAnimWalkRight();
        }
    }

    public void die() {
        this.animation = null;
        this.stoppedFrame = new TextureRegion(textureDead);
        updateWidthAndHeight(this.stoppedFrame);
    }

    public void dispose(){
        this.textureDead.dispose();
    }
}
