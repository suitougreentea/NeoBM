package io.github.suitougreentea.NeoBM.NBM.sequence;

public class Event {
    private long tick;
    private long time;

    public Event(long tick){
        this.tick = tick;
    }

    public long getTick() {
        return tick;
    }

    /*public void setTime(long time){
        this.time = time;
    }

    public long getTime(){
        return time;
    }*/
}
