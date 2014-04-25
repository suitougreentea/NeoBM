package io.github.suitougreentea.NeoBM.NBM.sequence;


//TODO: gate
public class EventLongNote extends Event {
    private int lane, soundId;

    public EventLongNote(int lane, int soundId, long tick) {
        super(tick);
        this.lane = lane;
        this.soundId = soundId;
    }

    public int getLane() {
        return lane;
    }

    public int getSoundId() {
        return soundId;
    }
}
