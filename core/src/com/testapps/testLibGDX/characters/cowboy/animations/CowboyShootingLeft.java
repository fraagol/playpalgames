package com.testapps.testLibGDX.characters.cowboy.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CowboyShootingLeft extends CowboyAnimation {
    public CowboyShootingLeft(Texture texture, float frameDuration) {
        super(texture, frameDuration);
    }

    @Override
    protected void createAnimation(Array<TextureRegion> regions, float frameDuration) {
        Array<TextureRegion> animationRegions = new Array();
        animationRegions.add(regions.get(109));
        animationRegions.add(regions.get(110));
        animationRegions.add(regions.get(111));

        animation = new Animation(frameDuration, animationRegions);
    }
}
