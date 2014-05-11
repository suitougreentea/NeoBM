package io.github.suitougreentea.NeoBM.player;

import java.io.IOException;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import io.github.suitougreentea.NeoBM.NeoBM;
import io.github.suitougreentea.util.GLFont;
import io.github.suitougreentea.util.Image;
import static org.lwjgl.opengl.GL11.*;

public class GameRenderer {
    private int canvasWidth, canvasHeight;
    private AngelCodeFont font;

    private float r = 1, g = 1, b = 1, a = 1;

    public GameRenderer(){
        try {
            font = new AngelCodeFont(this, "res/font.fnt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawImage(Image image, float dx, float dy){
        drawImage(image, dx, dy, image.getTexture().getImageWidth(), image.getTexture().getImageHeight(), 0, 0, image.getTexture().getImageWidth(), image.getTexture().getImageHeight());
    }
    public void drawImage(Image image, float dx, float dy, float width, float height){
        drawImage(image, dx, dy, width, height, 0, 0, image.getTexture().getImageWidth(), image.getTexture().getImageHeight());
    }
    public void drawImage(Image image, float dx, float dy, float width, float height, float sx, float sy){
        drawImage(image, dx, dy, width, height, sx, sy, width, height);
    }
    public void drawImage(Image image, float dx, float dy, float dw, float dh, float sx, float sy, float sw, float sh){

        image.getTexture().bind();
        float textureHeight = image.getTexture().getTextureHeight();
        float textureWidth = image.getTexture().getTextureWidth();

        glBegin(GL_QUADS);

        glColor4f(r, g, b, a);
        glTexCoord2f(sx / textureWidth, sy / textureHeight);
        glVertex3f(dx, dy, 0);
        glTexCoord2f(sx / textureWidth, (sy + sh) / textureHeight);
        glVertex3f(dx, dy + dh, 0);
        glTexCoord2f((sx + sw) / textureWidth, (sy + sh) / textureHeight);
        glVertex3f(dx + dw, dy + dh, 0);
        glTexCoord2f((sx + sw) / textureWidth, sy / textureHeight);
        glVertex3f(dx + dw, dy, 0);

        glEnd();
    }

    public void setColor(float r, float g, float b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void renderGlobal(){
        font.drawString(NeoBM.getFullVersion(),0,0);
    }

    public void renderFPS(int fps){
        GLFont.drawString(String.valueOf(fps));
    }

    public void bindNone(){
        TextureImpl.bindNone();
    }

    public void initGL() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        resizeCanvas(640, 480);
    }

    public void resizeCanvas(int canvasWidth, int canvasHeight){
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        resizeOrtho();
    }

    public void resizeOrtho(){
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL11.GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, canvasWidth, canvasHeight, 0, 0, 100);
        glMatrixMode(GL11.GL_MODELVIEW);
    }

    public long getRenderTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution() * 60 / 1000;
    }
}
