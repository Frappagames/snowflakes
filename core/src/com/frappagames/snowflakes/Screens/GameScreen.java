package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private Stage uiStage;
    private Rectangle monster;

    private Animation       standAnimation;
    private Animation       walkAnimation;
    private Animation       jumpAnimation;
    private TextureRegion   currentFrame;

    private float stateTime;

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
        Table uiTable = new Table();
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
        monster.height = 213;
        monster.x = Snowflakes.WIDTH / 2 - monster.width / 2;
        monster.y = Snowflakes.GROUND_HEIGHT;
        monsterLeftImage = new Texture(Gdx.files.internal("monsterLeft.png"));
        monsterRightImage = new Texture(Gdx.files.internal("monsterRight.png"));
        monsterDirection = DIRECTION.LEFT;

        // Stand animation
        Texture sheet;
        sheet = new Texture(Gdx.files.internal("stand.png"));
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth()/4, sheet.getHeight()/2);
        TextureRegion[] frames = new TextureRegion[8];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        standAnimation = new Animation(0.15f, frames);

        // Walk animation
        sheet = new Texture(Gdx.files.internal("walk.png"));
        tmp = TextureRegion.split(sheet, sheet.getWidth()/4, sheet.getHeight()/3);
        frames = new TextureRegion[12];
        index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        walkAnimation = new Animation(0.01f, frames);


        // Jump animation
        sheet = new Texture(Gdx.files.internal("jump.png"));
        tmp = TextureRegion.split(sheet, sheet.getWidth()/4, sheet.getHeight()/2);
        frames = new TextureRegion[8];
        index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        jumpAnimation = new Animation(0.1f, frames);

        stateTime = 0f;
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

        if (monsterJump) {
            monster.y += YSpeed;
            YSpeed -= FALL_SPEED;

            if (monster.y <= Snowflakes.GROUND_HEIGHT) {
                monsterJump = false;
                YSpeed = 0;
            }
            currentFrame = jumpAnimation.getKeyFrame(stateTime, false);
        } else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = standAnimation.getKeyFrame(stateTime, true);
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
