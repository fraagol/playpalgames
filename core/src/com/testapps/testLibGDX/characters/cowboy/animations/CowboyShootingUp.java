package com.testapps.testLibGDX.characters.cowboy.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CowboyShootingUp extends CowboyAnimation{
    public CowboyShootingUp(Texture texture, float frameDuration) {
        super(texture, frameDuration);
    }

    @Override
    protected void createAnimation(Array<TextureRegion> regions, float frameDuration) {
        Array<TextureRegion> animationRegions = new Array();
        animationRegions.add(regions.get(81));
        animationRegions.add(regions.get(82));
        animationRegions.add(regions.get(83));

        animation = new Animation(frameDuration, animationRegions);
    }
}
