package io.github.suitougreentea.NeoBM.player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.github.suitougreentea.NeoBM.NeoBM;
import io.github.suitougreentea.NeoBM.NBM.NBMLoader;
import io.github.suitougreentea.NeoBM.NBM.NBMSyntaxError;
import io.github.suitougreentea.NeoBM.player.state.StatePlayer;
import io.github.suitougreentea.util.GLFont;
import io.github.suitougreentea.util.Image;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.renderer.Renderer;

public class Game {
    private int targetFPS = 120;
    private long currentFrameTime;
    private long lastFrameTime;

    private GameRenderer renderer;

    private StatePlayer currentState = new StatePlayer(this);

    public void start(){
        try {
            Display.setDisplayMode(new DisplayMode(640,480));
            Display.setResizable(true);
            Display.setTitle(NeoBM.getFullVersion());
            Display.create();
        } catch (LWJGLException e){
            e.printStackTrace();
            return;
        }

        renderer = new GameRenderer(this);
        renderer.initGL();

        currentState.init();    //TODO

        lastFrameTime = getTime();

        while (!Display.isCloseRequested()) {
            currentFrameTime = getTime();

            if(Display.wasResized()) renderer.resizeOrtho();

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            pollInput();

            currentState.update();
            currentState.render();

            renderer.renderGlobal();

            Display.update();
            if(targetFPS > 0) Display.sync(targetFPS);

            lastFrameTime = currentFrameTime;
        }
    }

    public static final int INPUT_P2_1 = 0;
    public static final int INPUT_P2_2 = 1;
    public static final int INPUT_P2_3 = 2;
    public static final int INPUT_P2_4 = 3;
    public static final int INPUT_P2_5 = 4;
    public static final int INPUT_P2_6 = 5;
    public static final int INPUT_P2_7 = 6;
    public static final int INPUT_P2_S = 7;
    private int[] inputState = new int[8];

    public void pollInput(){
        for(int i=0;i<inputState.length;i++){
            if(inputState[i] == 2) inputState[i] = 3;
            if(inputState[i] == 1) inputState[i] = 0;
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == Keyboard.KEY_L) {
                inputState[INPUT_P2_1] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_P) {
                inputState[INPUT_P2_2] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_EQUALS) {
                inputState[INPUT_P2_3] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE) {
                inputState[INPUT_P2_4] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_SEMICOLON) {
                inputState[INPUT_P2_5] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_LBRACKET) {
                inputState[INPUT_P2_6] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_RBRACKET) {
                inputState[INPUT_P2_7] = Keyboard.getEventKeyState() ? 2 : 1;
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
                inputState[INPUT_P2_S] = Keyboard.getEventKeyState() ? 2 : 1;
            }
        }
    }

    public int[] getInputState(){
        return inputState;
    }

    public long getCurrentFrameTime() {
        return currentFrameTime;
    }

    public long getDelta() {
        return currentFrameTime - lastFrameTime;
    }

    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public GameRenderer getRenderer() {
        return renderer;
    }
}
