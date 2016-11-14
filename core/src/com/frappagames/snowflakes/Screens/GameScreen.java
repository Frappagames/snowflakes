package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.frappagames.snowflakes.Snowflakes;
import com.frappagames.snowflakes.Tools.Assets;
import com.frappagames.snowflakes.Tools.SnowFlake;
import com.frappagames.snowflakes.Tools.abstractGameScreen;

import java.util.Iterator;

/**
 * Game class
 *
 * Created by Jérémy MOREAU on 14/08/15.
 */
class GameScreen extends abstractGameScreen {
    private Array<SnowFlake> snowFlakes;
    private long lastSpawnTime;
    private final long gameStartTime;
    private final boolean gameIsPlaying;

    GameScreen(final Snowflakes gameApp) {
        super(gameApp);

        lastSpawnTime = 0;
        snowFlakes = new Array<SnowFlake>();
        gameStartTime = TimeUtils.nanoTime();
        gameIsPlaying = true;

        Label lblScore = new Label("0", Assets.fontScore);
        Image imgScore  = new Image(Assets.btnScore);
        Image imgGround  = new Image(Assets.ground);
        ImageButton btnMenu  = new ImageButton(Assets.btnMenu, Assets.btnMenuOver);

        Table scoreTable = new Table();
        scoreTable.setFillParent(true);
        scoreTable.add(lblScore).pad(3, 0, 0, 8).expandX().align(Align.right);

        Stack stack = new Stack();
        stack.add(imgScore);
        stack.add(scoreTable);

        table.add(stack).expand().top().left().pad(15, 15, 0, 0);
        table.add(btnMenu).expand().top().right().pad(15, 0, 0, 15);
        table.row();
        table.add(imgGround).colspan(2).bottom();

        btnMenu.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MenuScreen(gameApp));
            }
        });
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Reduce balloons spawning delay to add difficulty over time
        double acceleration = Math.round((TimeUtils.nanoTime() - gameStartTime) / 130000000);

        // check if we need to create a new raindrop
        if (gameIsPlaying) {
            if (TimeUtils.nanoTime() - lastSpawnTime > (Snowflakes.SPAWN_SPEED_MS - acceleration) * 1000000)
                spawnSnowFlake();
        }


        Iterator<SnowFlake> iter = snowFlakes.iterator();
        while (iter.hasNext()) {
            SnowFlake snowFlake = iter.next();

//            // Is balloon touched ?
//            if (Gdx.input.justTouched()) {
//                viewport.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
//                if (balloon.contains(touchPoint.x, touchPoint.y)) {
//                    if (Settings.soundEnabled) Assets.playSound(Assets.clickSound);
//                    iter.remove();
//                    balloonBursted++;
//                    balloonsTouched++;
//
//                    // Add effect
//                    ParticleEffectPool.PooledEffect effect = balloonsEffectPool.obtain();
//                    effect.setPosition(touchPoint.x, touchPoint.y);
//                    effect.getEmitters().get(0).getTint().setColors(balloon.getColor());
//                    effects.add(effect);
//
//                    continue;
//                }
//            }
            snowFlake.y -= snowFlake.getSpeed() * Gdx.graphics.getDeltaTime();

            if (snowFlake.y < Snowflakes.GROUND_HEIGHT) iter.remove();
        }

        game.batch.begin();

        for (SnowFlake snowFlake : snowFlakes) {
            game.batch.draw(
                    Assets.snowflake.getRegion(),
                    snowFlake.x,
                    snowFlake.y,
                    snowFlake.width,
                    snowFlake.height
            );
        }

        game.batch.end();
    }

    private void spawnSnowFlake() {
        snowFlakes.add(new SnowFlake());
        lastSpawnTime = TimeUtils.nanoTime();
    }
}
