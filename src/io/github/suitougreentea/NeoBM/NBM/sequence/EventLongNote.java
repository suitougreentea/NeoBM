package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventLongNote extends EventNote {
    private int gate;
    private float endPosition;
    private boolean active;

    public EventLongNote(int lane, int soundId, int gate, long tick) {
        super(lane, soundId, tick);
        this.gate = gate;
    }

    public int getGate(){
        return gate;
    }

    public void setEndPosition(float endPosition){
        this.endPosition = endPosition;
    }

    public float getEndPosition(){
        return endPosition;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
