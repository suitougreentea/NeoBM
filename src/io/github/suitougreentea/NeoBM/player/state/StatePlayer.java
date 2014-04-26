package io.github.suitougreentea.NeoBM.player.state;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import io.github.suitougreentea.NeoBM.NBM.LoaderThread;
import io.github.suitougreentea.NeoBM.NBM.NBMLoader;
import io.github.suitougreentea.NeoBM.NBM.NBMSyntaxError;
import io.github.suitougreentea.NeoBM.player.Game;
import io.github.suitougreentea.NeoBM.player.NBMPlayer;
import io.github.suitougreentea.NeoBM.player.SkinManager;
import io.github.suitougreentea.util.GLFont;

public class StatePlayer {
    private Game game;
    private SkinManager skin;

    private NBMPlayer player;

    public StatePlayer(Game game) {
        super();
        this.game = game;
    }

    public NBMPlayer getPlayer() {
        return player;
    }

    public void init(){


        skin = new SkinManager(this, game, game.getRenderer());
        skin.init();

        player = new NBMPlayer(game, "test/rhythmtest.nbm");
    }

    public void enter(){

    }

    public void render(){
        skin.render();
        TextureImpl.bindNone();
        GL11.glTranslatef(50, 50, 0);
        GLFont.drawString(String.valueOf(player.getTick()));
        GL11.glTranslatef(-50, -50, 0);
    }

    public void update(){

        player.eachFrame();
    }
}
