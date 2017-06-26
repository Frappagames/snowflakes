package com.frappagames.snowflakes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.frappagames.snowflakes.Tools.Assets;
import com.frappagames.snowflakes.Tools.Settings;
import com.frappagames.snowflakes.Screens.SplashScreen;

public class Snowflakes extends Game {
	public static final int GROUND_HEIGHT = 96;
	public static final int DROPLET_SPAWN_SPEED_MS = 2250;
	public static final int DROPLET_MAX_SPAWN_SPEED = 500;
	public SpriteBatch batch;
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 800;
	public static final int SPAWN_WIDTH = 1224;
	public static final int SPAWN_SPEED_MS = 1500;
	public static final int MAX_SPAWN_SPEED = 300;

	/**
	 * TODO : Vies
	 * TODO : game Over
	 * TODO : Timer
	 * TODO : Win screen
	 * TODO : Best score
	 * TODO : impact anim et son
	 */

	@Override
	public void create () {
		this.batch = new SpriteBatch();

		Assets.load();
		Settings.load();
		setScreen(new SplashScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		Assets.dispose();
	}
}
