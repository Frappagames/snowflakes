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
 * <p/>
 * Created by jmoreau on 19/08/15.
 */
public class Assets {
    public static TextureRegionDrawable play;
    public static TextureRegionDrawable playOver;
    public static TextureRegionDrawable exit;
    public static TextureRegionDrawable exitOver;
    public static TextureRegionDrawable soundOn;
    public static TextureRegionDrawable soundOff;
    public static TextureRegionDrawable musicOn;
    public static TextureRegionDrawable musicOff;

    public static TextureRegionDrawable scoreBackground;
    public static TextureRegion smallStar;
    public static TextureRegion bigStar;
    public static TextureRegion cocarde;
    public static TextureRegionDrawable home;
    public static TextureRegionDrawable homeOver;
    public static TextureRegionDrawable replay;
    public static TextureRegionDrawable replayOver;

    public static Array<TextureRegion> balloonsTextures;
    public static Sound clickSound;
    public static Music music;
    private static TextureAtlas itemsAtlas;

    public static Label.LabelStyle gameFont;

    public static void load() {
        //Fonts
//        BitmapFont souses20Font = new BitmapFont(Gdx.files.internal("souses-20.fnt"), false);
//        souses20 = new Label.LabelStyle(souses20Font, Color.WHITE);

        // Load Textures
        itemsAtlas = new TextureAtlas(Gdx.files.internal("snowflakes.pack"));
//
//        play = new TextureRegionDrawable(itemsAtlas.findRegion("btnPlay"));

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
