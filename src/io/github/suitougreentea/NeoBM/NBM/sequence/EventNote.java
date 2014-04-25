package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventNote extends Event {
    private int lane, soundId;
    private float position;

    public EventNote(int lane, int soundId, long tick) {
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


    public void setPosition(float position){
        this.position = position;
    }

    public float getPosition(){
        return position;
    }
}
