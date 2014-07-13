package com.testapps.testLibGDX.characters.cowboy.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CowboyWalkingRight extends CowboyAnimation{

    public CowboyWalkingRight(Texture texture, float frameDuration) {
        super(texture, frameDuration);
    }

    @Override
    protected void createAnimation(Array<TextureRegion> regions, float frameDuration){
        Array<TextureRegion> animationRegions = new Array();
        for(int i = 44; i < 52; i++)
        {
            animationRegions.add(regions.get(i));
        }
        animation = new Animation(frameDuration, animationRegions);
    }
}
