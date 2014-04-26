package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventLongNote extends EventNote {
    private int gate;

    public EventLongNote(int lane, int soundId, int gate, long tick) {
        super(lane, soundId, tick);
        this.gate = gate;
    }

    public int getGate(){
        return gate;
    }
}
