package io.github.suitougreentea.NeoBM.NBM;

import java.io.IOException;

public class LoaderThread extends Thread {
    private String path;
    private float progress;
    private NBMData result;

    public LoaderThread(String path){
        this.path = path;
    }

    @Override
    public void run(){
        try {
            result = NBMLoader.loadNBM(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NBMSyntaxError e) {
            e.printStackTrace();
        }
        this.progress = 1.0f;
    }

    public float getProgress(){
        return this.progress;
    }

    public NBMData getResult(){
        if(progress != 1.0f) return null;
        return result;
    }
}
