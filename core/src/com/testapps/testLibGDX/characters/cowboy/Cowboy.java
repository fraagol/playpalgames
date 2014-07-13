package com.testapps.testLibGDX.characters.cowboy;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.testapps.testLibGDX.GameBoard;
import com.testapps.testLibGDX.characters.cowboy.views.CowboyView;

public class Cowboy {
    private int id;
    private CowboyView view;
    private int boardPos;

    private float time = 0;

    final private float speed = 4f;
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
            updatePos(time);
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

    public int getBoardPos() {
        return boardPos;
    }

    public void positionInBoard(int boardPos) {
        this.boardPos = boardPos;
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
            this.boardPos = this.moveToBoardPosition;
            this.moveToBoardPosition = null;
        }

        this.view.setPos(newPos);
    }

    public void shootTo(Integer boardPos) {
        setShootingAnimation(boardPos);
        this.shooting = true;
    }

    public void die(){
        this.view.die();
    }

    public void dispose(){
        this.view.dispose();
    }

    private void setShootingAnimation(Integer boardPos) {
        Vector2 myPos = GameBoard.getScreenPos(this.boardPos);
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


}
