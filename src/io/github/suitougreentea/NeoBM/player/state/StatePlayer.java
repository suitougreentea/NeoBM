package io.github.suitougreentea.NeoBM.player.state;

import java.io.IOException;

import io.github.suitougreentea.NeoBM.NBM.NBMLoader;
import io.github.suitougreentea.NeoBM.NBM.NBMSyntaxError;
import io.github.suitougreentea.NeoBM.player.Game;
import io.github.suitougreentea.NeoBM.player.NBMPlayer;
import io.github.suitougreentea.NeoBM.player.SkinManager;

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
        try {
            player = new NBMPlayer(game, NBMLoader.loadNBM("test/rhythmtest.nbm"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NBMSyntaxError e) {
            e.printStackTrace();
        }


        skin = new SkinManager(this, game, game.getRenderer());
        skin.init();

        //player.start();
    }

    public void render(){
        skin.render();
    }

    public void update(){
        player.eachFrame();
    }
}
