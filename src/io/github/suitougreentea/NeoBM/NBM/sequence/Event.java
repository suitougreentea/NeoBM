package io.github.suitougreentea.NeoBM.NBM.sequence;

public class Event {
    private long tick;

    public Event(long tick){
        this.tick = tick;
    }

    public long getTick() {
        return tick;
    }
}
