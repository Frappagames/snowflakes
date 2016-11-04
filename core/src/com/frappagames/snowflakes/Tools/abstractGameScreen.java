package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.frappagames.snowflakes.Snowflakes;

/**
 * Default game screen
 * Created by jmoreau on 12/01/16.
 */
public abstract class abstractGameScreen implements Screen {
    protected final Viewport viewport;
    private final OrthographicCamera camera;
    private ParticleEffect snowEffect;
    protected Snowflakes game;
    protected Stage stage;
    protected Table table;

    private Texture background;

    public abstractGameScreen(Snowflakes game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.position.set(Snowflakes.WIDTH / 2, Snowflakes.HEIGHT / 2, 0);
        viewport = new FitViewport(Snowflakes.WIDTH, Snowflakes.HEIGHT, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        snowEffect = new ParticleEffect();
        snowEffect.load(Gdx.files.internal("snowflakes-effect.fx"), Gdx.files.internal(""));
        snowEffect.setPosition(0, Snowflakes.HEIGHT);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Color clearColor = Color.valueOf("#C9EAF3FF");
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background, 0, 0);
        snowEffect.draw(game.batch, delta);
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (((height * 16) / width) == 9) {
            viewport.setWorldHeight(720);
            camera.position.y = 360;
            background = new Texture("background-16-9.jpg");
        } else {
            viewport.setWorldHeight(800);
            camera.position.y = 400;
            background = new Texture("background-8-5.jpg");
        }
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        background.dispose();
        stage.dispose();
        snowEffect.dispose();
    }
}
