package com.frappagames.snowflakes.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Settings class for the game.
 *
 * Store sounds and music preferences
 *
 * Created by jmoreau on 19/08/15.
 */
public class Settings {
    public  static boolean     soundEnabled;
    private static Integer     bestScore;
    private static Preferences settings;

    public static void load() {
        settings     = Gdx.app.getPreferences("com.frappagames.snowflakes.settings");
        soundEnabled = settings.getBoolean("sound", true);
        bestScore    = settings.getInteger("best_score", 0);
    }

    public static void toggleSound() {
        soundEnabled = !soundEnabled;
        settings.putBoolean("sound", soundEnabled);
        settings.flush();
    }

    public static Integer getBestScore() {
        return bestScore;
    }

    public static void setBestScore(Integer bestScore) {
        Settings.bestScore = bestScore;
        settings.putInteger("best_score", bestScore);
        settings.flush();
    }
}
