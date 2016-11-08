package com.frappagames.snowflakes.Screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.frappagames.snowflakes.Snowflakes;
import com.frappagames.snowflakes.Tools.Assets;
import com.frappagames.snowflakes.Tools.abstractGameScreen;

/**
 * Game class
 *
 * Created by Jérémy MOREAU on 14/08/15.
 */
class GameScreen extends abstractGameScreen {
    GameScreen(final Snowflakes gameApp) {
        super(gameApp);

        Label lblScore = new Label("9999", Assets.fontScore);
        lblScore.setAlignment(Align.right);
        Image imgScore  = new Image(Assets.btnScore);
        Image imgGround  = new Image(Assets.ground);
        ImageButton btnMenu  = new ImageButton(Assets.btnMenu, Assets.btnMenuOver);

        Stack stack = new Stack();
        stack.add(imgScore);
        stack.add(lblScore);

        table.add(stack).expand().top().left().pad(15, 15, 0, 0);
        table.add(btnMenu).expand().top().right().pad(15, 0, 0, 15);
        table.row();
        table.add(imgGround).colspan(2).bottom();

        btnMenu.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Assets.playSound(Assets.clickSound);
                game.setScreen(new MenuScreen(gameApp));
            }
        });
    }
}
