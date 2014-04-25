package io.github.suitougreentea.util;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;


public class Image {
    private Texture texture;

    public Image(String path){
        try {
            this.texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Texture getTexture() {
        return texture;
    }
}
