package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Assets management class
 *
 * Created by Jérémy MOREAU on 19/08/15.
 */
public class Assets {
    public static TextureRegionDrawable btnPlay, btnExit, btnMusicOn, btnMusicOff, btnMenu;
    public static TextureRegionDrawable btnPlayOver, btnExitOver, btnMenuOver;
    public static TextureRegionDrawable snowflake, title, ground, btnScore;

    public static Sound clickSound;
    public static Music music;
    private static TextureAtlas itemsAtlas;

    public static Label.LabelStyle fontScore;

    public static void load() {
        //Fonts
        BitmapFont souses20Font = new BitmapFont(Gdx.files.internal("fontScore.fnt"), false);
        fontScore = new Label.LabelStyle(souses20Font, Color.WHITE);

        // Load Textures
        itemsAtlas  = new TextureAtlas(Gdx.files.internal("snowflakes.pack"));

        title       = new TextureRegionDrawable(itemsAtlas.findRegion("title"));
        ground      = new TextureRegionDrawable(itemsAtlas.findRegion("ground"));
        snowflake   = new TextureRegionDrawable(itemsAtlas.findRegion("flocon"));
        btnPlay     = new TextureRegionDrawable(itemsAtlas.findRegion("btnPlay"));
        btnExit     = new TextureRegionDrawable(itemsAtlas.findRegion("btnExit"));
        btnMenu     = new TextureRegionDrawable(itemsAtlas.findRegion("btnMenu"));
        btnScore     = new TextureRegionDrawable(itemsAtlas.findRegion("btnScore"));
        btnPlayOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnPlayOver"));
        btnExitOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnExitOver"));
        btnMenuOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnMenuOver"));
        btnMusicOn  = new TextureRegionDrawable(itemsAtlas.findRegion("btnMusicOn"));
        btnMusicOff = new TextureRegionDrawable(itemsAtlas.findRegion("btnMusicOff"));

        // Load Music and sounds
        music = Gdx.audio.newMusic(Gdx.files.internal("music.ogg"));
        music.setLooping(true);
        music.setVolume(0.5f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sound-click.mp3"));
    }

    public static void playSound(Sound sound) {
        if (Settings.soundEnabled) sound.play(1);
    }

    public static void dispose() {
        itemsAtlas.dispose();
        clickSound.dispose();
        music.dispose();
    }
}
