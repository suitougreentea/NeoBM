package io.github.suitougreentea.NeoBM;

import java.util.logging.Logger;

import javax.script.ScriptException;

import io.github.suitougreentea.NeoBM.player.Game;
import io.github.suitougreentea.util.GLFont;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class AppPlayer {
    public static void main(String[] args) throws ScriptException {
        NeoBM.logger.info(NeoBM.getFullVersion());
        NeoBM.logger.info("Player");

        new Game().start();
    }
}
