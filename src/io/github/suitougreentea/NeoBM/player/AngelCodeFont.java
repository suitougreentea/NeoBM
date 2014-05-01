package io.github.suitougreentea.NeoBM.player;

import io.github.suitougreentea.util.Image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class AngelCodeFont {
    private GameRenderer renderer;

    private int lineHeight;
    private int base;
    private int scaleW;
    private int scaleH;
    private Image[] page;

    private Map<Integer, Glyph> glyphs = new HashMap<Integer, Glyph>();

    public AngelCodeFont(GameRenderer r, String fntPath) throws IOException{
        this.renderer = r;
        parseFnt(fntPath);
    }

    private void parseFnt(String path) throws IOException{
        BufferedReader in = null;//new BufferedReader(path);
        String info, common, page;

        boolean done = false;
        while (!done) {
            String line = in.readLine();
            if (line == null) {
                done = true;
            } else {

            }
        }
    }
}

class Glyph {
    private Image image;
    private int x;
    private int y;
    private int width;
    private int height;
    private int xoffset;
    private int yoffset;
    private int xadvance;

    private Map<Integer, Integer> kerning;
}
