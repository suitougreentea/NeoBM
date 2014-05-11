package io.github.suitougreentea.NeoBM.player;

import io.github.suitougreentea.util.Image;

/**
 * 0123456789 .の順に並ぶ画像を読み込んで表示します。
 */  
public class DigitFont {
    GameRenderer renderer;
    Image image;
    private int sx, sy, height;
    private int[] charwidth = new int[12];
    private int sourcewidth;
    private int[] padding = new int[12];

    public DigitFont(GameRenderer renderer, Image image, int sx, int sy, int sourcewidth, int height, int defaultcharwidth, int defaultpadding){
        this.renderer = renderer;
        this.image = image;
        this.sx = sx;
        this.sy = sy;
        this.height = height;
        this.sourcewidth = sourcewidth;
        for(int i=0;i<12;i++){
            this.charwidth[i] = defaultcharwidth;
            this.padding[i] = defaultpadding;
        }
    }

    public void drawString(String str, int dx, int dy){
        int x = dx;
        for(int i=0;i<str.length();i++){
            char c = str.charAt(i);
            int index;
            if(c == ' ') index = 10;
            else if(c == '.') index = 11;
            else index = c - 48;
            renderer.drawImage(image, x, dy, charwidth[index], height, sx+sourcewidth*index, sy);
            x += charwidth[index] + padding[index];
        }
    }

    public void drawStringRight(String str, int dx, int dy){
        drawString(str, dx - getWidth(str), dy);
    }

    public int getWidth(String str){
        int x = 0;
        for(int i=0;i<str.length();i++){
            char c = str.charAt(i);
            int index;
            if(c == ' ') index = 10;
            else if(c == '.') index = 11;
            else index = c - 48;
            x += charwidth[index] + padding[index];
        }

        return x;
    }

    public void setCharWidth(int index, int width){
        this.charwidth[index] = width;
    }
    public void setPadding(int index, int padding){
        this.charwidth[index] = padding;
    }
}
