package io.github.suitougreentea.util;
import static org.lwjgl.opengl.GL11.*;

public class GLFont {
    /*
     * Bit: FEDCBA9876543210
     *  0 1
     * 23456
     *  7 8
     * 9ABCD
     *  E F
     */
    private static final int B0 = 0x0001;
    private static final int B1 = 0x0002;
    private static final int B2 = 0x0004;
    private static final int B3 = 0x0008;
    private static final int B4 = 0x0010;
    private static final int B5 = 0x0020;
    private static final int B6 = 0x0040;
    private static final int B7 = 0x0080;
    private static final int B8 = 0x0100;
    private static final int B9 = 0x0200;
    private static final int BA = 0x0400;
    private static final int BB = 0x0800;
    private static final int BC = 0x1000;
    private static final int BD = 0x2000;
    private static final int BE = 0x4000;
    private static final int BF = 0x8000;


    private static int charset[] = {
        0,
        B5 | BE,
        B4 | B6,
        0xFFFF,
        B0 | B1 | B2 | B5 | B7 | B8 | BA | BD | BE | BF,
        B2 | B5 | BA | BD,
        B0 | B1 | B3 | B6 | B7 | B8 | B9 | BB | BC | BE,
        B5,
        B5 | BC,
        B3 | BA,
        B3 | B5 | B7 | B8 | BA | BC,
        B4 | B7 | B8 | BB,
        BA,
        B7 | B8,
        BE,
        B5 | BA,

        B0 | B1 | B2 | B6 | B9 | BD | BE | BF,
        B6 | BD,
        B0 | B1 | B6 | B7 | B8 | B9 | BE | BF,
        B0 | B1 | B6 | B7 | B8 | BD | BE | BF,
        B2 | B6 | B7 | B8 | BD,
        B0 | B1 | B2 | B7 | B8 | BD | BE | BF,
        B0 | B1 | B2 | B7 | B8 | B9 | BD | BE | BF,
        B0 | B1 | B2 | B6 | BD,
        B0 | B1 | B2 | B6 | B7 | B8 | B9 | BD | BE | BF,
        B0 | B1 | B2 | B6 | B7 | B8 | BD | BE | BF,
        0xFFFF,
        0xFFFF,
        B5 | BC,
        0xFFFF,
        B3 | BA,
        B0 | B1 | B2 | B5 | BE,
        
        B0 | B1 | B2 | B6 | B9 | BB | BC | BE | BF,
        B5 | B6 | B8 | BA | BD,
        B0 | B1 | B2 | B5 | B7 | B8 | B9 | BD | BE | BF,
        B1 | B3 | BA | BF,
        B0 | B2 | B4 | B8 | B9 | BD | BE | BF,
        B0 | B1 | B2 | B7 | B8 | B9 | BE | BF,
        B0 | B1 | B2 | B7 | B8 | B9,
        B0 | B1 | B2 | B9 | BC | BE | BF,
        B2 | B6 | B7 | B8 | B9 | BD,
        B0 | B1 | B4 | BB | BE | BF,
        B1 | B6 | B9 | BD | BE | BF,
        B2 | B5 | B7 | B9 | BC,
        B2 | B9 | BE | BF,
        B2 | B3 | B5 | B6 | B9 | BD,
        B2 | B3 | B6 | B9 | BC | BD,
        B0 | B1 | B2 | B6 | B9 | BD | BE | BF,

        B0 | B1 | B2 | B6 | B7 | B8 | B9,
        B0 | B1 | B2 | B6 | B9 | BC | BD | BE | BF,
        B0 | B1 | B2 | B6 | B7 | B8 | B9 | BC,
        B0 | B1 | B2 | B7 | B8 | BD | BE | BF,
        B0 | B1 | B4 | BB,
        B2 | B6 | B9 | BD | BE | BF,
        B2 | B5 | B9 | BA,
        B2 | B6 | B9 | BA | BC | BD,
        B3 | B5 | BA | BC,
        B3 | B5 | BB,
        B0 | B1 | B5 | BA | BE | BF,
        B1 | B4 | BB | BF,
        B3 | B5 | B7 | B8 | BB,
        B0 | B4 | BB | BE,
        B5 | B6,
        BE | BF,
        
        // TODO: small letters
        B3,
        B5 | B6 | B8 | BA | BD,
        B0 | B1 | B2 | B5 | B7 | B8 | B9 | BD | BE | BF,
        B1 | B3 | BA | BF,
        B0 | B2 | B4 | B8 | B9 | BD | BE | BF,
        B0 | B1 | B2 | B7 | B8 | B9 | BE | BF,
        B0 | B1 | B2 | B7 | B8 | B9,
        B0 | B1 | B2 | B9 | BC | BE | BF,
        B2 | B6 | B7 | B8 | B9 | BD,
        B0 | B1 | B4 | BB | BE | BF,
        B1 | B6 | B9 | BD | BE | BF,
        B2 | B5 | B7 | B9 | BC,
        B2 | B9 | BE | BF,
        B2 | B3 | B5 | B6 | B9 | BD,
        B2 | B3 | B6 | B9 | BC | BD,
        B0 | B1 | B2 | B6 | B9 | BD | BE | BF,

        B0 | B1 | B2 | B6 | B7 | B8 | B9,
        B0 | B1 | B2 | B6 | B9 | BC | BD | BE | BF,
        B0 | B1 | B2 | B6 | B7 | B8 | B9 | BC,
        B0 | B1 | B2 | B7 | B8 | BD | BE | BF,
        B0 | B1 | B4 | BB,
        B2 | B6 | B9 | BD | BE | BF,
        B2 | B5 | B9 | BA,
        B2 | B6 | B9 | BA | BC | BD,
        B3 | B5 | BA | BC,
        B3 | B5 | BB,
        B0 | B1 | B5 | BA | BE | BF,
        B1 | B4 | B7 | BB | BF,
        B4 | BB,
        B0 | B4 | B8 | BB | BE,
        B0 | B1,
        0xFFFF,
    };

    public static void drawString(String s){
        String[] a = s.split("\n");
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a[i].length();j++){
                char c = a[i].charAt(j);
                glPushMatrix();
                glTranslatef(j*8,i*12,0);
                drawChar(c);
                glPopMatrix();
            }
        }
    }

    public static void drawChar(char c){
        int d = charset[c-0x20];
        glLineWidth(1);
        glBegin(GL_LINES);
        if((d & B0) != 0){
            glVertex2f(0,0);
            glVertex2f(3,0);
        }
        if((d & B1) != 0){
            glVertex2f(3,0);
            glVertex2f(6,0);
        }
        if((d & B2) != 0){
            glVertex2f(0,0);
            glVertex2f(0,5);
        }
        if((d & B3) != 0){
            glVertex2f(0,0);
            glVertex2f(3,5);
        }
        if((d & B4) != 0){
            glVertex2f(3,0);
            glVertex2f(3,5);
        }
        if((d & B5) != 0){
            glVertex2f(6,0);
            glVertex2f(3,5);
        }
        if((d & B6) != 0){
            glVertex2f(6,0);
            glVertex2f(6,5);
        }
        if((d & B7) != 0){
            glVertex2f(0,5);
            glVertex2f(3,5);
        }
        if((d & B8) != 0){
            glVertex2f(3,5);
            glVertex2f(6,5);
        }
        if((d & B9) != 0){
            glVertex2f(0,5);
            glVertex2f(0,10);
        }
        if((d & BA) != 0){
            glVertex2f(3,5);
            glVertex2f(0,10);
        }
        if((d & BB) != 0){
            glVertex2f(3,5);
            glVertex2f(3,10);
        }
        if((d & BC) != 0){
            glVertex2f(3,5);
            glVertex2f(6,10);
        }
        if((d & BD) != 0){
            glVertex2f(6,5);
            glVertex2f(6,10);
        }
        if((d & BE) != 0){
            glVertex2f(0,10);
            glVertex2f(3,10);
        }
        if((d & BF) != 0){
            glVertex2f(3,10);
            glVertex2f(6,10);
        }
        glEnd();
    }
}
