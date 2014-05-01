package io.github.suitougreentea.NeoBM.NBM;

public class NBMSpeedData {
    long eventTick;
    long eventTime;
    float tickPerMilliSecond, milliSecondPerTick;
    int baseTick;
    //int stopGate;

    public NBMSpeedData(long tick, float tickPerMilliSecond, float milliSecondPerTick, int baseTick){
        this.eventTick = tick;
        this.tickPerMilliSecond = tickPerMilliSecond;
        this.milliSecondPerTick = milliSecondPerTick;
        this.baseTick = baseTick;
    }

    public long getEventTime() {
        return eventTime;
    }
    public void setEventTime(long time) {
        this.eventTime = time;
    }
    public long getEventTick() {
        return eventTick;
    }
    public float getTickPerMilliSecond() {
        return tickPerMilliSecond;
    }
    public float getMilliSecondPerTick() {
        return milliSecondPerTick;
    }

    public float getTick(long time){
        return eventTick + (time - eventTime) * tickPerMilliSecond;
    }

    public float getTime(long tick){
        return eventTime + (tick - eventTick) * milliSecondPerTick;
    }

    public int getBaseTick(){
        return baseTick;
    }
}
