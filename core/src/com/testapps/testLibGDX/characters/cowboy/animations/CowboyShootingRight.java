package com.testapps.testLibGDX.characters.cowboy.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CowboyShootingRight extends CowboyAnimation {
    public CowboyShootingRight(Texture texture, float frameDuration) {
        super(texture, frameDuration);
    }

    @Override
    protected void createAnimation(Array<TextureRegion> regions, float frameDuration) {
        Array<TextureRegion> animationRegions = new Array();
        animationRegions.add(regions.get(53));
        animationRegions.add(regions.get(54));
        animationRegions.add(regions.get(55));

        animation = new Animation(frameDuration, animationRegions);
    }
}
