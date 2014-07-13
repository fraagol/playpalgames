package com.testapps.testLibGDX.characters.cowboy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CowboyOrientation {
    //Row's frame in the image
    public static final int STOP_N = 5;
    public static final int STOP_NE = 4;
    public static final int STOP_E = 3;
    public static final int STOP_SE = 2;
    public static final int STOP_S = 9;
    public static final int STOP_SW = 8;
    public static final int STOP_W = 7;
    public static final int STOP_NW = 6;

    private int orientation;
    private Texture texture;

    public CowboyOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public TextureRegion obtainFrame() {
        if(this.texture == null)
        {
            System.out.print("CowboyOrientation error. Texture MUST be defined and it's null");
            return null;
        }

        Integer numRows = 10;
        Integer numColumns = 14;
        Integer cellWidth = this.texture.getWidth() / numColumns;
        Integer cellHeight = this.texture.getHeight() / numRows;

        return new TextureRegion(texture, 0, this.orientation*cellHeight, cellWidth, cellHeight );
    }
}
