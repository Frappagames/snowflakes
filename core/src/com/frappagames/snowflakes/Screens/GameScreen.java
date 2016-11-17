package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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
    private final Texture monsterLeftImage;
    private final Texture monsterRightImage;
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
    private Table uiTable;
    private Stage uiStage;
    private Rectangle monster;

    GameScreen(final Snowflakes gameApp) {
        super(gameApp);

        lastSpawnTime = 0;
        lastDropletSpawnTime = 0;
        snowFlakes    = new Array<SnowFlake>();
        droplets      = new Array<Rectangle>();
        gameStartTime = TimeUtils.nanoTime();
        gameIsPlaying = true;
        monsterJump   = false;

        Label lblScore = new Label("0", Assets.fontScore);
        Image imgScore  = new Image(Assets.btnScore);
        ImageButton btnMenu  = new ImageButton(Assets.btnMenu, Assets.btnMenuOver);

        Table scoreTable = new Table();
        scoreTable.setFillParent(true);
        scoreTable.add(lblScore).pad(3, 0, 0, 8).expandX().align(Align.right);

        Stack stack = new Stack();
        stack.add(imgScore);
        stack.add(scoreTable);


        uiStage = new Stage(viewport);
        Gdx.input.setInputProcessor(uiStage);
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiStage.addActor(uiTable);
        uiTable.add(stack).expand().top().left().pad(15, 15, 0, 0);
        uiTable.add(btnMenu).expand().top().right().pad(15, 0, 0, 15);
        uiTable.row();


        btnMenu.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MenuScreen(gameApp));
            }
        });

        // create a Rectangle to logically represent the monster
        monster = new Rectangle();
        monster.width = 128;
        monster.height = 212;
        monster.x = Snowflakes.WIDTH / 2 - monster.width / 2;
        monster.y = Snowflakes.GROUND_HEIGHT;
        monsterLeftImage = new Texture(Gdx.files.internal("monsterLeft.png"));
        monsterRightImage = new Texture(Gdx.files.internal("monsterRight.png"));
        monsterDirection = DIRECTION.LEFT;
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
            monsterDirection = DIRECTION.RIGHT;
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

        if (monsterJump) {
            monster.y += YSpeed;
            YSpeed -= FALL_SPEED;

            if (monster.y <= Snowflakes.GROUND_HEIGHT) {
                monsterJump = false;
                YSpeed = 0;
            }
        }

        // make sure the monster stays within the screen bounds
        if(monster.x < 0) monster.x = 0;
        if(monster.x > Snowflakes.WIDTH - monster.width) monster.x = Snowflakes.WIDTH - monster.width;


        Iterator<SnowFlake> iter = snowFlakes.iterator();
        while (iter.hasNext()) {
            SnowFlake snowFlake = iter.next();
            snowFlake.y -= snowFlake.getSpeed() * Gdx.graphics.getDeltaTime();

            if (snowFlake.y < Snowflakes.GROUND_HEIGHT) iter.remove();
        }

        Iterator<Rectangle> iter2 = droplets.iterator();
        while (iter2.hasNext()) {
            Rectangle rect = iter2.next();
            rect.y -= SnowFlake.DEFAULT_DROPLET_SPEED * Gdx.graphics.getDeltaTime();

            if (rect.y < Snowflakes.GROUND_HEIGHT) iter2.remove();
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

        for (Rectangle drop : droplets) {
            game.batch.draw(
                    Assets.droplet.getRegion(),
                    drop.x,
                    drop.y
            );
        }

        game.batch.draw(
                (monsterDirection == DIRECTION.LEFT) ? monsterLeftImage : monsterRightImage,
                monster.x,
                monster.y,
                128, 212
        );

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
