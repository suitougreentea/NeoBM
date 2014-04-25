package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventTempo extends Event {
    private float tempo;

    public EventTempo(float tempo, long tick) {
        super(tick);
        this.tempo = tempo;
    }

    public float getTempo() {
        return tempo;
    }
}
