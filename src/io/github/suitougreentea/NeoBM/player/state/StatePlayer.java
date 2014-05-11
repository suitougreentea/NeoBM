package io.github.suitougreentea.NeoBM.player.state;

import org.newdawn.slick.opengl.TextureImpl;

import io.github.suitougreentea.NeoBM.player.Game;
import io.github.suitougreentea.NeoBM.player.NBMPlayer;
import io.github.suitougreentea.NeoBM.player.SkinManager;

public class StatePlayer extends State {
    private SkinManager skin;

    private NBMPlayer player;

    public StatePlayer(Game game) {
        super(game);
    }

    public NBMPlayer getPlayer() {
        return player;
    }

    @Override
    public void init(){
        skin = new SkinManager(this, game, game.getRenderer());
        skin.init();
    }

    public void initPlayer(String path){
        player = new NBMPlayer(game, path);
    }

    @Override
    public void enter(){

    }

    @Override
    public void render(){
        skin.render();
        TextureImpl.bindNone();
    }

    @Override
    public void update(){
        player.eachFrame();
    }
}
