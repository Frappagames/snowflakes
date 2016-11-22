package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.frappagames.snowflakes.Tools.Settings;
import com.frappagames.snowflakes.Tools.SnowFlake;
import com.frappagames.snowflakes.Tools.abstractGameScreen;

import java.util.Iterator;

/**
 * Game class
 *
 * Created by Jérémy MOREAU on 14/08/15.
 */
class GameScreen extends abstractGameScreen {
    private static final int JUMP_SPEED = 20;
    private static final int FALL_SPEED = 2;
    private final int MONSTER_SPEED = 600;
    private boolean monsterJump;
    private int YSpeed;


    private enum DIRECTION {LEFT, RIGHT}
    private DIRECTION monsterDirection;

    private Array<SnowFlake> snowFlakes;
    private Array<Rectangle> droplets;
    private long lastSpawnTime;
    private long lastDropletSpawnTime;
    private final long gameStartTime;
    private final boolean gameIsPlaying;
    private Stage uiStage;
    private Rectangle monster;

    private float stateTime;

    private ParticleEffectPool snowImpactEffectPool, dropletImpactEffectPool;
    private Array<ParticleEffectPool.PooledEffect> snowImpactEffects, dropletImpactEffects;
    private ParticleEffect snowImpactEffect, dropletImpactEffect;

    GameScreen(final Snowflakes gameApp) {
        super(gameApp);

        lastSpawnTime = 0;
        lastDropletSpawnTime = 0;
        snowFlakes    = new Array<SnowFlake>();
        droplets      = new Array<Rectangle>();
        gameStartTime = TimeUtils.nanoTime();
        gameIsPlaying = true;
        monsterJump   = false;
        stateTime = 0f;

        Label lblScore = new Label("0", Assets.fontScore);
        Image imgScore  = new Image(Assets.btnScore);
        ImageButton btnMenu  = new ImageButton(Assets.btnMenu, Assets.btnMenuOver);

        Table scoreTable = new Table();
        scoreTable.setFillParent(true);
        scoreTable.add(lblScore).pad(3, 0, 0, 8).expandX().align(Align.right);

        Stack stack = new Stack();
        stack.add(imgScore);
        stack.add(scoreTable);

        // Scores ☼
        uiStage = new Stage(viewport);
        Gdx.input.setInputProcessor(uiStage);
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);
        uiTable.add(stack).expand().top().left().pad(15, 15, 0, 0);
        uiTable.add(btnMenu).expand().top().right().pad(15, 0, 0, 15);
        uiTable.row();

        // Bouton "Menu" ≡
        btnMenu.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MenuScreen(gameApp));
            }
        });

        // create a Rectangle to logically represent the monster ☻
        monster = new Rectangle();
        monster.width = 128;
        monster.height = 213;
        monster.x = Snowflakes.WIDTH / 2 - monster.width / 2;
        monster.y = Snowflakes.GROUND_HEIGHT;
        monsterDirection = DIRECTION.LEFT;

        // Snow Impact Effects
        snowImpactEffects = new Array<ParticleEffectPool.PooledEffect>();
        snowImpactEffect = new ParticleEffect();
        snowImpactEffect.load(Gdx.files.internal("snowImpact.fx"), Gdx.files.internal(""));
        snowImpactEffectPool = new ParticleEffectPool(snowImpactEffect, 0, 10);

        // Droplet Impact Effects
        dropletImpactEffects = new Array<ParticleEffectPool.PooledEffect>();
        dropletImpactEffect = new ParticleEffect();
        dropletImpactEffect.load(Gdx.files.internal("dropletImpact.fx"), Gdx.files.internal(""));
        dropletImpactEffectPool = new ParticleEffectPool(dropletImpactEffect, 0, 10);

        // Play Music ♫
        if (Settings.soundEnabled) Assets.music.play();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Reduce balloons spawning delay to add difficulty over time
        double acceleration = Math.round((TimeUtils.nanoTime() - gameStartTime) / 130000000);

        // check if we need to create a new raindrop
        if (gameIsPlaying) {
            if (TimeUtils.nanoTime() - lastSpawnTime > (Snowflakes.SPAWN_SPEED_MS - acceleration) * 1000000) {
                spawnSnowFlake();
            }

            if (TimeUtils.nanoTime() - lastDropletSpawnTime > (Snowflakes.DROPLET_SPAWN_SPEED_MS - acceleration) * 1000000L) {
                spawnDroplet();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && monster.y == Snowflakes.GROUND_HEIGHT) {
            monsterJump = true;
            YSpeed = JUMP_SPEED;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            monster.x -= this.MONSTER_SPEED * delta;
            monsterDirection = DIRECTION.LEFT;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            monster.x += this.MONSTER_SPEED * delta;
            monsterDirection = DIRECTION.RIGHT;
        }

        TextureRegion currentFrame;
        if (monsterJump) {
            monster.y += YSpeed;
            YSpeed -= FALL_SPEED;

            if (monster.y <= Snowflakes.GROUND_HEIGHT) {
                monsterJump = false;
                YSpeed = 0;
            }
            currentFrame = Assets.jumpAnimation.getKeyFrame(stateTime, false);
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentFrame = Assets.walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = Assets.standAnimation.getKeyFrame(stateTime, true);
        }

        if (monsterDirection == DIRECTION.RIGHT && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (monsterDirection == DIRECTION.LEFT && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        stateTime += Gdx.graphics.getDeltaTime();

        // make sure the monster stays within the screen bounds
        if(monster.x < 0) monster.x = 0;
        if(monster.x > Snowflakes.WIDTH - monster.width) monster.x = Snowflakes.WIDTH - monster.width;


        Iterator<SnowFlake> iter = snowFlakes.iterator();
        while (iter.hasNext()) {
            SnowFlake snowFlake = iter.next();
            snowFlake.y -= snowFlake.getSpeed() * Gdx.graphics.getDeltaTime();

            if (snowFlake.y < Snowflakes.GROUND_HEIGHT)
            {
                iter.remove();

                // Add effect
                ParticleEffectPool.PooledEffect effect = snowImpactEffectPool.obtain();
                effect.setPosition(snowFlake.x + snowFlake.width / 2, snowFlake.y);
                snowImpactEffects.add(effect);
            }
        }

        Iterator<Rectangle> iter2 = droplets.iterator();
        while (iter2.hasNext()) {
            Rectangle rect = iter2.next();
            rect.y -= SnowFlake.DEFAULT_DROPLET_SPEED * Gdx.graphics.getDeltaTime();

            if (rect.y < Snowflakes.GROUND_HEIGHT) {
                iter2.remove();

                // Add effect
                ParticleEffectPool.PooledEffect effect = dropletImpactEffectPool.obtain();
                effect.setPosition(rect.x + rect.width / 2, rect.y);
                dropletImpactEffects.add(effect);
            }
        }

        game.batch.begin();

        game.batch.draw(Assets.ground.getRegion(), 0, 0);

        for (SnowFlake snowFlake : snowFlakes) {
            game.batch.draw(
                    Assets.snowflake.getRegion(),
                    snowFlake.x,
                    snowFlake.y,
                    snowFlake.width,
                    snowFlake.height
            );
        }

        for (int i = snowImpactEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = snowImpactEffects.get(i);
            effect.draw(game.batch, delta);
            if (effect.isComplete()) {
                effect.free();
                snowImpactEffects.removeValue(effect, true);
            }
        }

        for (Rectangle drop : droplets) {
            game.batch.draw(
                    Assets.droplet.getRegion(),
                    drop.x,
                    drop.y
            );
        }

        for (int i = dropletImpactEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = dropletImpactEffects.get(i);
            effect.draw(game.batch, delta);
            if (effect.isComplete()) {
                effect.free();
                dropletImpactEffects.removeValue(effect, true);
            }
        }

        game.batch.draw(currentFrame, monster.x, monster.y);


        game.batch.end();

        uiStage.act(delta);
        uiStage.draw();
    }

    private void spawnDroplet() {
        Rectangle droplet = new Rectangle();
        droplet.setPosition(MathUtils.random(0, Snowflakes.WIDTH - 64), Snowflakes.HEIGHT);
        droplet.width = 64;
        droplet.height = 64;
        droplets.add(droplet);
        lastDropletSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnSnowFlake() {
        snowFlakes.add(new SnowFlake());
        lastSpawnTime = TimeUtils.nanoTime();
    }
}
