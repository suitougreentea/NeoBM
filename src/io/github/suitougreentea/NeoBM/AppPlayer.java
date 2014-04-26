package io.github.suitougreentea.NeoBM;

import io.github.suitougreentea.NeoBM.player.Game;

public class AppPlayer {
    public static void main(String[] args) {
        NeoBM.logger.info(NeoBM.getFullVersion());
        NeoBM.logger.info("Player");

        new Game().start();
    }
}
