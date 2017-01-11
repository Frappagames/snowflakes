package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
    public static TextureRegionDrawable title, droplet,btnScore, snowflake,  ground,
        btnPlay, btnPlayOver, btnExit, btnExitOver, btnMenu, btnMenuOver, btnMusicOn, btnMusicOff,
        btnJump, btnJumpOver, btnLeft, btnLeftOver, btnRight, btnRightOver;
    public static Animation standAnimation, walkAnimation, jumpAnimation;

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
        droplet     = new TextureRegionDrawable(itemsAtlas.findRegion("droplet"));
        btnScore    = new TextureRegionDrawable(itemsAtlas.findRegion("btnScore"));

        btnPlay     = new TextureRegionDrawable(itemsAtlas.findRegion("btnPlay"));
        btnPlayOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnPlayOver"));

        btnExit     = new TextureRegionDrawable(itemsAtlas.findRegion("btnExit"));
        btnExitOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnExitOver"));

        btnMenu     = new TextureRegionDrawable(itemsAtlas.findRegion("btnMenu"));
        btnMenuOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnMenuOver"));

        btnMusicOn  = new TextureRegionDrawable(itemsAtlas.findRegion("btnMusicOn"));
        btnMusicOff = new TextureRegionDrawable(itemsAtlas.findRegion("btnMusicOff"));

        btnLeft     = new TextureRegionDrawable(itemsAtlas.findRegion("btnLeft"));
        btnLeftOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnLeftOver"));

        btnRight     = new TextureRegionDrawable(itemsAtlas.findRegion("btnRight"));
        btnRightOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnRightOver"));

        btnJump     = new TextureRegionDrawable(itemsAtlas.findRegion("btnJump"));
        btnJumpOver = new TextureRegionDrawable(itemsAtlas.findRegion("btnJumpOver"));

        // Load Music and sounds
        music = Gdx.audio.newMusic(Gdx.files.internal("music.ogg"));
        music.setLooping(true);
        music.setVolume(0.5f);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("sound-click.mp3"));

        // Load animations

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
