package io.github.suitougreentea.NeoBM;

import io.github.suitougreentea.util.GLFont;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class AppPlayer {

    public static void main(String[] args) {
        System.out.println("NeoBM Player");

        try {
            Display.setDisplayMode(new DisplayMode(640,480));
            Display.setTitle("NeoBM Player");
            Display.create();
        } catch (LWJGLException e){
            e.printStackTrace();
            return;
        }

        long a,b;
        a = getTime();


        GL11.glViewport(0, 0, 640, 480);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();   
        GL11.glOrtho(0, 640, 480, 0, 0, 100);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        while (!Display.isCloseRequested()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            b = getTime();

            GL11.glPushMatrix();
            GL11.glTranslatef(10, 10, 0);
            GLFont.drawString(String.format("%d",b-a));
            GL11.glPopMatrix();

            a = b;
            Display.update();
        }
    }

    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

}
