package io.github.suitougreentea.NeoBM.player;

import io.github.suitougreentea.NeoBM.player.state.StatePlayer;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SkinManager {
    StatePlayer s;
    Game g;
    GameRenderer r;

    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
    Invocable inv = (Invocable) engine;

    public SkinManager(StatePlayer state, Game game, GameRenderer renderer){
        this.s = state;
        this.g = game;
        this.r = renderer;
    }

    public void init(){
        try {
            engine.put("s", s);
            engine.put("g", g);
            engine.put("r", r);
            engine.eval("function getPath(s){ return \"skin/default/\"+ s};");
            engine.eval("var Image = Java.type('io.github.suitougreentea.util.Image');");
            engine.eval(new FileReader("skin/default/script.js"));

            inv.invokeFunction("init");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    public void render(){
        try {
            inv.invokeFunction("render");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

    }
}
