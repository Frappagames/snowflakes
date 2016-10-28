package com.frappagames.snowflakes.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.frappagames.snowflakes.Snowflakes;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(640, 400);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new Snowflakes();
        }
}