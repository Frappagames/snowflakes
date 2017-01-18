package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.frappagames.snowflakes.Snowflakes;

/**
 * Created by gfp on 14/11/16.
 */

public class SnowFlake extends Rectangle {
    public static final int DEFAULT_DROPLET_SPEED = 160;
    private final static int DEFAULT_SPEED = 80;
    private final int speed;

    public SnowFlake() {
        super();

        this.speed = MathUtils.round(DEFAULT_SPEED * MathUtils.random(70, 130) / 100);
        float scale = MathUtils.random(0.66f, 1f);
        this.setSize(
            MathUtils.round(Assets.snowflake.getRegion().getRegionWidth() * scale),
            MathUtils.round(Assets.snowflake.getRegion().getRegionHeight() * scale)
        );
        this.setPosition(MathUtils.random(0, Snowflakes.WIDTH - this.getWidth()), Snowflakes.HEIGHT);
    }

    public int getSpeed() {
        return speed;
    }
}
