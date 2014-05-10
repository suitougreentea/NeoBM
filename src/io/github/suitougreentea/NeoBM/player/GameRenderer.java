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
    private Game game;

    private int canvasWidth, canvasHeight;

    private float r = 1, g = 1, b = 1, a = 1;

    public GameRenderer(Game game){
        this.game = game;
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
        glPushMatrix();
        glTranslatef(1, 1, 0);
        bindNone();
        GLFont.drawString(NeoBM.getFullVersion());
        glPopMatrix();

        glPushMatrix();
        glTranslatef(1, 16, 0);
        bindNone();
        GLFont.drawString(String.valueOf(game.getFPS()));
        glPopMatrix();
    }

    public void renderTestPoint(){
        // This is a debug feature
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(639, 0);
        GL11.glVertex2f(0, 479);
        GL11.glVertex2f(639, 479);
        GL11.glEnd();
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
