package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventSound extends Event {
    private int id, soundId;

    public EventSound(int id, int soundId, long tick) {
        super(tick);
        this.id = id;
        this.soundId = soundId;
    }

    public int getId() {
        return id;
    }

    public int getSoundId() {
        return soundId;
    }
}
