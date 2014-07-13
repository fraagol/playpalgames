package com.testapps.testLibGDX.characters.cowboy.animations;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class CowboyShootingDown extends CowboyAnimation{
    public CowboyShootingDown(Texture texture, float frameDuration) {
        super(texture, frameDuration);
    }

    @Override
    protected void createAnimation(Array<TextureRegion> regions, float frameDuration) {
        Array<TextureRegion> animationRegions = new Array();
        animationRegions.add(regions.get(137));
        animationRegions.add(regions.get(138));
        animationRegions.add(regions.get(139));

        animation = new Animation(frameDuration, animationRegions);
    }
}
