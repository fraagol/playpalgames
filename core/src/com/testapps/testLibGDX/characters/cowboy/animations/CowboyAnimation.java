package com.testapps.testLibGDX.characters.cowboy.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

abstract public class CowboyAnimation {
    protected Animation animation;

    public CowboyAnimation(Texture texture, float frameDuration){
        Array<TextureRegion> regions = extractRegionsFromTexture(texture);
        createAnimation(regions, frameDuration);
    }

    public TextureRegion getFrame(float stateTime, boolean looping) {
        return this.animation.getKeyFrame(stateTime, looping);
    }

    protected abstract void createAnimation(Array<TextureRegion> regions, float frameDuration);


    protected Array<TextureRegion> extractRegionsFromTexture(Texture texture){
        Integer numRows = 10;
        Integer numColumns = 14;
        Integer cellWidth = texture.getWidth() / numColumns;
        Integer cellHeight = texture.getHeight() / numRows;
        Array<TextureRegion> animationTextures = new Array<TextureRegion>();
        TextureRegion region;

        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numColumns; j++)
            {
                region = new TextureRegion(texture, j*cellWidth, i*cellHeight, cellWidth, cellHeight );
                animationTextures.add(region);
            }
        }

        return animationTextures;
    }


}
