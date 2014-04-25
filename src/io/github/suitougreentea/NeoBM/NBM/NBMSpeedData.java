package io.github.suitougreentea.NeoBM.NBM;

public class NBMSpeedData {
    long tick;
    long time;
    float tickPerMilliSecond, milliSecondPerTick;
    //int stopGate;

    public NBMSpeedData(long tick, float tickPerMilliSecond, float milliSecondPerTick){
        this.tick = tick;
        this.tickPerMilliSecond = tickPerMilliSecond;
        this.milliSecondPerTick = milliSecondPerTick;
    }

    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getTick() {
        return tick;
    }
    public float getTickPerMilliSecond() {
        return tickPerMilliSecond;
    }
    public float getMilliSecondPerTick() {
        return milliSecondPerTick;
    }
}
