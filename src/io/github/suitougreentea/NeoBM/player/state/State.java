package io.github.suitougreentea.NeoBM.player.state;

import io.github.suitougreentea.NeoBM.player.Game;

public abstract class State {
    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public abstract void init();
    public abstract void enter();
    public abstract void render();
    public abstract void update();
}
