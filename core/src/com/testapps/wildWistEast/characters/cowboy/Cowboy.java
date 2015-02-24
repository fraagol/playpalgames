package com.testapps.wildWistEast.characters.cowboy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.testapps.wildWistEast.GameBoard;
import com.testapps.wildWistEast.GameParams;
import com.testapps.wildWistEast.characters.cowboy.views.CowboyView;

public class Cowboy {
    private int id;
    private CowboyView view;
    private int numLives = GameParams.numLives;
    private int numBullets = GameParams.numBullets;

    private float time = 0;

    final private float speed = 100f;
    private boolean moving = false;
    private Vector2 moveTo;
    private Integer moveToBoardPosition;
    private Vector2 currentDirection;
    private boolean shooting = false;

    public Cowboy(CowboyView view, int id) {

        this.view = view;
        this.id = id;
    }

    public void render(SpriteBatch batch) {
        if(this.moving)
        {
            time += Gdx.graphics.getDeltaTime();
            updatePos(Gdx.graphics.getDeltaTime());
            if(this.moving) {
                this.view.updateWalkingAnimation(this.currentDirection);
            }
        }
        if(this.shooting)
        {

        }
        this.view.render(batch, time);
    }

    public void stop(CowboyOrientation orientation){
        this.view.stop(orientation);
    }

    public int getWidth() {
        return this.view.getWidth();
    }

    public int getHeight(){
        return this.view.getHeight();
    }

    public int getID() {return this.id;}

    public void setPos(int boardPos) {
        this.view.setPos(GameBoard.getScreenPos(boardPos));
    }

    public void moveTo(Integer boardPos) {
        moveToBoardPosition = boardPos;
        moveTo = GameBoard.getScreenPos(boardPos);
        moving = true;
        time = 0;
    }

    private void updatePos(float elapsedTime)
    {
        if(this.moving == false)
            return;

        Vector2 newPos = this.view.getPos().cpy();

        float realDistanceToEnd = Vector2.dst(newPos.x, newPos.y, moveTo.x, moveTo.y);
        currentDirection = this.moveTo.cpy();
        currentDirection.sub(newPos).nor();
        newPos.x += this.speed * currentDirection.x * elapsedTime;
        newPos.y += this.speed * currentDirection.y * elapsedTime;

        float distanceMoved = Vector2.dst(newPos.x, newPos.y, this.view.getPos().x, this.view.getPos().y);
        if(distanceMoved >= realDistanceToEnd)
        {
            this.moving = false;
            this.view.stop(new CowboyOrientation(CowboyOrientation.STOP_N));
            GameBoard.setCowboyPosition(this, moveToBoardPosition);
            this.moveToBoardPosition = null;
        }

        this.view.setPos(newPos);
    }

    public void shootTo(Integer boardPos) {
        if(canShoot()) {
            numBullets--;
            setShootingAnimation(boardPos);
            this.shooting = true;
        }
    }

    public void shooted(){
        this.numLives--;
        if(this.numLives == 0) {
            this.view.die();
        }
    }

    public void dispose(){
        this.view.dispose();
    }

    private void setShootingAnimation(Integer boardPos) {
        Vector2 myPos = GameBoard.getScreenPos(this);
        Vector2 objectivePos = GameBoard.getScreenPos(boardPos);
        float distX = objectivePos.x - myPos.x;
        float distY = objectivePos.y - myPos.y;
        if(Math.abs(distX) < Math.abs(distY))
        {//Up//Down
            if(distY > 0)
            {
                this.view.showAnimShootUp();
            }
            else
            {
                this.view.showAnimShootDown();
            }
        }
        else
        {//Right//Left
            if(distX > 0)
            {
                this.view.showAnimShootRight();
            }
            else
            {
                this.view.showAnimShootLeft();

            }
        }
    }


    public boolean canShoot() {
        return this.numBullets > 0;
    }

    public void rechargeGun() {
        this.numBullets = GameParams.numBullets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cowboy cowboy = (Cowboy) o;

        if (id != cowboy.id) return false;
        if (moving != cowboy.moving) return false;
        if (numBullets != cowboy.numBullets) return false;
        if (numLives != cowboy.numLives) return false;
        if (shooting != cowboy.shooting) return false;
        if (Float.compare(cowboy.speed, speed) != 0) return false;
        if (Float.compare(cowboy.time, time) != 0) return false;
        if (currentDirection != null ? !currentDirection.equals(cowboy.currentDirection) : cowboy.currentDirection != null)
            return false;
        if (moveTo != null ? !moveTo.equals(cowboy.moveTo) : cowboy.moveTo != null) return false;
        if (moveToBoardPosition != null ? !moveToBoardPosition.equals(cowboy.moveToBoardPosition) : cowboy.moveToBoardPosition != null)
            return false;
        if (view != null ? !view.equals(cowboy.view) : cowboy.view != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (view != null ? view.hashCode() : 0);
        result = 31 * result + numLives;
        result = 31 * result + numBullets;
        result = 31 * result + (time != +0.0f ? Float.floatToIntBits(time) : 0);
        result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
        result = 31 * result + (moving ? 1 : 0);
        result = 31 * result + (moveTo != null ? moveTo.hashCode() : 0);
        result = 31 * result + (moveToBoardPosition != null ? moveToBoardPosition.hashCode() : 0);
        result = 31 * result + (currentDirection != null ? currentDirection.hashCode() : 0);
        result = 31 * result + (shooting ? 1 : 0);
        return result;
    }
}
