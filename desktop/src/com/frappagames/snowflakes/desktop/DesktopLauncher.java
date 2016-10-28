package com.frappagames.snowflakes.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.frappagames.snowflakes.Snowflakes;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "SnowFlakes";
		config.width = 1280;
		config.height = 800;
		new LwjglApplication(new Snowflakes(), config);
	}
}
