package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
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
    private static final int MONSTER_SPEED = 600;

    private enum DIRECTION {LEFT, RIGHT}

    private final float[] dropletsColors;
    private boolean monsterJump, gameOver;
    private       int                YSpeed;
    private       int                currentSpawnSpeed;
    private       int                currentDropletsSpeed;
    private       int                life;
    private       DIRECTION          monsterDirection;
    private       Array<SnowFlake>   snowFlakes;
    private       Array<Rectangle>   droplets;
    private       long               lastSpawnTime;
    private       long               lastDropletSpawnTime;
    private final long               gameStartTime;
    private       boolean            gameIsPlaying;
    private       Stage              uiStage;
    private       Rectangle          monster;
    private       float              stateTime;
    private       ParticleEffectPool snowImpactEffectPool, dropletImpactEffectPool;
    private Array<ParticleEffectPool.PooledEffect> snowImpactEffects, dropletImpactEffects;
    private Rectangle bounds;
    private TextureRegion currentFrame;

    private ImageButton btnJumpLeft, btnJumpRight, btnLeft, btnRight;
    private Image life1, life2, life3;

    private Label lblScore;

    GameScreen(final Snowflakes gameApp) {
        super(gameApp);

        lastSpawnTime = 0;
        lastDropletSpawnTime = 0;
        life = 3;
        snowFlakes = new Array<SnowFlake>();
        droplets = new Array<Rectangle>();
        gameStartTime = TimeUtils.nanoTime();
        gameIsPlaying = true;
        monsterJump = false;
        stateTime = 0f;
        dropletsColors = new float[3];
        currentSpawnSpeed = Snowflakes.SPAWN_SPEED_MS;
        currentDropletsSpeed = Snowflakes.DROPLET_SPAWN_SPEED_MS;

        lblScore = new Label("0", Assets.fontScore);
        Image imgScore  = new Image(Assets.btnScore);
        ImageButton btnMenu  = new ImageButton(Assets.btnMenu, Assets.btnMenuOver);
        btnJumpLeft  = new ImageButton(Assets.btnJump, Assets.btnJumpOver);
        btnJumpLeft.pad(0, 15, 0, 0);
        btnJumpRight  = new ImageButton(Assets.btnJump, Assets.btnJumpOver);
        btnJumpRight.pad(0, 0, 0, 15);
        btnLeft  = new ImageButton(Assets.btnLeft, Assets.btnLeftOver);
        btnLeft.pad(15, 15, 15, 0);
        btnRight  = new ImageButton(Assets.btnRight, Assets.btnRightOver);
        btnRight.pad(15, 0, 15, 15);

        life1  = new Image(Assets.life);
        life2  = new Image(Assets.life);
        life3  = new Image(Assets.life);
        HorizontalGroup lifeGroup = new HorizontalGroup();
        lifeGroup.addActor(life3);
        lifeGroup.addActor(life2);
        lifeGroup.addActor(life1);

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
        uiTable.align(Align.top);
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);
        uiTable.add(stack).pad(15, 15, 0, 0);
        uiTable.add().expandX();
        uiTable.add(lifeGroup).top().right().pad(30, 0, 0, 15);
        uiTable.add(btnMenu).top().right().pad(15, 0, 0, 15);

        if (Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.iOS) {
            Table controlTable = new Table();
            controlTable.bottom();
            controlTable.setFillParent(true);
            controlTable.add(btnJumpLeft);
            controlTable.add().expandX();
            controlTable.add(btnJumpRight);
            controlTable.row();
            controlTable.add(btnLeft);
            controlTable.add().expandX();
            controlTable.add(btnRight);
            uiStage.addActor(controlTable);
        }

        // Bouton "Menu" ≡
        btnMenu.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MenuScreen(gameApp));
            }
        });

        // create a Rectangle to logically represent the monster ☻
        monster = new Rectangle();
        monster.width = 256;
        monster.height = 256;
        monster.x = Snowflakes.WIDTH / 2 - monster.width / 2;
        monster.y = Snowflakes.GROUND_HEIGHT;
        monsterDirection = DIRECTION.LEFT;
        bounds = new Rectangle(monster.x + 68, monster.y, 120, 215);

        // Snow Impact Effects
        snowImpactEffects = new Array<ParticleEffectPool.PooledEffect>();
        ParticleEffect snowImpactEffect = new ParticleEffect();
        snowImpactEffect.load(Gdx.files.internal("snowImpact.fx"), Gdx.files.internal(""));
        snowImpactEffectPool = new ParticleEffectPool(snowImpactEffect, 0, 10);

        // Droplet Impact Effects
        Color color = Color.valueOf("#78ACFFFF");
        dropletsColors[0] = color.r;
        dropletsColors[1] = color.g;
        dropletsColors[2] = color.b;

        dropletImpactEffects = new Array<ParticleEffectPool.PooledEffect>();
        ParticleEffect dropletImpactEffect = new ParticleEffect();
        dropletImpactEffect.load(Gdx.files.internal("dropletImpact.fx"), Gdx.files.internal(""));
        dropletImpactEffectPool = new ParticleEffectPool(dropletImpactEffect, 0, 10);

        // Play Music ♫
        if (Settings.soundEnabled) Assets.music.play();
    }

    @Override
    public void update(float delta) {
        // Exit to game menu on ESCAPE
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Assets.playSound(Assets.clickSound);
            game.setScreen(new MenuScreen(game));
        }

        if (gameIsPlaying) {
            // Reduce spawning delay to add difficulty over time
            if (this.currentSpawnSpeed > Snowflakes.MAX_SPAWN_SPEED) {
                this.currentSpawnSpeed = Snowflakes.SPAWN_SPEED_MS - Math.round((TimeUtils.nanoTime() - gameStartTime) / 130000000);
            }
            if (this.currentDropletsSpeed > Snowflakes.DROPLET_MAX_SPAWN_SPEED) {
                this.currentDropletsSpeed = Snowflakes.DROPLET_SPAWN_SPEED_MS - Math.round((TimeUtils.nanoTime() - gameStartTime) / 130000000);
            }

            // check if we need to create a new raindrop
            if (TimeUtils.nanoTime() - lastSpawnTime > this.currentSpawnSpeed * 1000000L) {
                spawnSnowFlake();
            }

            if (TimeUtils.nanoTime() - lastDropletSpawnTime > this.currentDropletsSpeed * 1000000L) {
                spawnDroplet();
            }

            if ((Gdx.input.isKeyPressed(Input.Keys.UP) || this.btnJumpLeft.isPressed() || this.btnJumpRight.isPressed())
                    && monster.y == Snowflakes.GROUND_HEIGHT) {
                monsterJump = true;
                YSpeed = JUMP_SPEED;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || this.btnLeft.isPressed()) {
                monster.x -= MONSTER_SPEED * delta;
                monsterDirection = DIRECTION.LEFT;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || this.btnRight.isPressed()) {
                monster.x += MONSTER_SPEED * delta;
                monsterDirection = DIRECTION.RIGHT;
            }

            if (monsterJump) {
                monster.y += YSpeed;
                YSpeed -= FALL_SPEED;

                if (monster.y <= Snowflakes.GROUND_HEIGHT) {
                    monsterJump = false;
                    YSpeed = 0;
                }
                currentFrame = Assets.jumpAnimation.getKeyFrame(stateTime, false);
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                    || this.btnLeft.isPressed() || this.btnRight.isPressed()) {
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
            if (monster.x < -68) monster.x = -68;
            if (monster.x > Snowflakes.WIDTH - monster.width + 68)
                monster.x = Snowflakes.WIDTH - monster.width + 68;
            bounds.setPosition(monster.x + 68, monster.y);

            Iterator<SnowFlake> iter = snowFlakes.iterator();
            while (iter.hasNext()) {
                SnowFlake snowFlake = iter.next();
                snowFlake.setY(snowFlake.getY() - snowFlake.getSpeed() * Gdx.graphics.getDeltaTime());

                if (snowFlake.getY() < Snowflakes.GROUND_HEIGHT || snowFlake.overlaps(bounds)) {
                    iter.remove();

                    // Add effect
                    ParticleEffectPool.PooledEffect effect = snowImpactEffectPool.obtain();
                    effect.setPosition(snowFlake.getX() + snowFlake.width / 2, snowFlake.getY());
                    snowImpactEffects.add(effect);

                    // Monster collect a SnowFlake
                    if (snowFlake.overlaps(bounds)) {
                        Assets.playSound(Assets.pickSound);
                        lblScore.setText(String.valueOf(Integer.parseInt(lblScore.getText().toString()) + 10));
                    }
                }
            }

            Iterator<Rectangle> iter2 = droplets.iterator();
            while (iter2.hasNext()) {
                Rectangle droplet = iter2.next();
                droplet.y -= SnowFlake.DEFAULT_DROPLET_SPEED * Gdx.graphics.getDeltaTime();

                if (droplet.y < Snowflakes.GROUND_HEIGHT || droplet.overlaps(bounds)) {
                    iter2.remove();

                    // Add effect
                    ParticleEffectPool.PooledEffect effect = dropletImpactEffectPool.obtain();
                    effect.setPosition(droplet.x + droplet.width / 2, droplet.y);
                    effect.getEmitters().get(0).getTint().setColors(dropletsColors);
                    dropletImpactEffects.add(effect);

                    // Droplet hit the monster
                    if (droplet.overlaps(bounds)) {
                        Assets.playSound(Assets.hitSound);
                        loseLife();
                    }
                }
            }
        }
    }

    public void draw(float delta) {
        game.batch.begin();

        // Draw background
        game.batch.draw(background, -(monster.x / 8) - 20, 0);

        // Draw snow
        snowEffect.getEmitters().first().setAttached(true);
        snowEffect.setPosition(-(monster.x / 12) - 20, Snowflakes.HEIGHT);
        snowEffect.draw(game.batch, delta);

        // Draw ground
        game.batch.draw(Assets.ground.getRegion(), 0, 0);

        if (gameIsPlaying) {
            // Draw snowflakes
            for (SnowFlake snowFlake : snowFlakes) {
                game.batch.draw(
                        Assets.snowflake.getRegion(),
                        snowFlake.x,
                        snowFlake.y,
                        snowFlake.width,
                        snowFlake.height
                );
            }

            // Draw snowflakes particules
            for (int i = snowImpactEffects.size - 1; i >= 0; i--) {
                ParticleEffectPool.PooledEffect effect = snowImpactEffects.get(i);
                effect.draw(game.batch, delta);
                if (effect.isComplete()) {
                    effect.free();
                    snowImpactEffects.removeValue(effect, true);
                }
            }

            // Draw Droplets
            for (Rectangle drop : droplets) {
                game.batch.draw(
                        Assets.droplet.getRegion(),
                        drop.x - 13,
                        drop.y
                );
            }

            // Draw droplets particules
            for (int i = dropletImpactEffects.size - 1; i >= 0; i--) {
                ParticleEffectPool.PooledEffect effect = dropletImpactEffects.get(i);
                effect.draw(game.batch, delta);
                if (effect.isComplete()) {
                    effect.free();
                    dropletImpactEffects.removeValue(effect, true);
                }
            }

            // Draw monster
            game.batch.draw(currentFrame, monster.x, monster.y - 6);
        }

        game.batch.end();

        uiStage.act(delta);
        uiStage.draw();

        super.draw(delta);
    }

    private void loseLife() {
        life--;

        if (life <= 0) {
            gameIsPlaying = false;
            Integer score = Integer.valueOf(lblScore.getText().toString());

            if (Settings.getBestScore() < score) {
                Settings.setBestScore(score);
            }

            System.out.println("Game Over !");
        }

        life1.setVisible(life >= 1);
        life2.setVisible(life >= 2);
        life3.setVisible(life >= 3);
    }

    private void spawnDroplet() {
        Rectangle droplet = new Rectangle();
        droplet.setPosition(MathUtils.random(0, Snowflakes.WIDTH - 64), Snowflakes.HEIGHT);
        droplet.width = 42;
        droplet.height = 66;
        droplets.add(droplet);
        lastDropletSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnSnowFlake() {
        snowFlakes.add(new SnowFlake());
        lastSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void hide() {
        // Pause Music ♫
        if (Settings.soundEnabled) Assets.music.pause();

        super.hide();
    }

    @Override
    public void pause() {
        // Pause Music ♫
        if (Settings.soundEnabled) Assets.music.pause();

        super.pause();
    }

    @Override
    public void resume() {
        super.resume();

        // Resume Music ♫
        if (Settings.soundEnabled) Assets.music.play();
    }

    @Override
    public void dispose() {
        // Stop Music ♫
        if (Settings.soundEnabled) Assets.music.stop();

        super.dispose();
    }
}
