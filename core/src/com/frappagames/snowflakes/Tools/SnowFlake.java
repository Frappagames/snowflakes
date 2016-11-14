package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.frappagames.snowflakes.Snowflakes;

/**
 * Created by gfp on 14/11/16.
 */

public class SnowFlake extends Rectangle {
    private final int speed;
    private final static int DEFAULT_SPEED = 80;
    public final int width;
    public final int height;

    public SnowFlake() {
        super();

        this.speed = MathUtils.round(DEFAULT_SPEED * MathUtils.random(70, 130) / 100);
        float scale = MathUtils.random(0.8f, 1f);
        this.width = MathUtils.round(Assets.snowflake.getRegion().getRegionWidth() * scale);
        this.height = MathUtils.round(Assets.snowflake.getRegion().getRegionHeight() * scale);
        this.setPosition(MathUtils.random(0, Snowflakes.WIDTH - this.width), Snowflakes.HEIGHT);
    }

    public int getSpeed() {
        return speed;
    }
}
