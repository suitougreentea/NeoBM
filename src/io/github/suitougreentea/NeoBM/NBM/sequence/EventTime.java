package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventTime extends Event {
    private int beat, baseTick;

    public EventTime(int beat, int baseTick, long tick) {
        super(tick);
        this.beat = beat;
        this.baseTick = baseTick;
    }

    public int getBeat() {
        return beat;
    }

    public int getBaseTick() {
        return baseTick;
    }
}
