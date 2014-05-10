package io.github.suitougreentea.NeoBM.NBM.sequence;

public class EventLongNoteBackSpin extends EventLongNote {
    int endSoundId;
    boolean startJudged;
    long releasedTime = -1;

    public EventLongNoteBackSpin(int lane, int soundId, int endSoundId, int gate, long tick) {
        super(lane, soundId, gate, tick);
        this.endSoundId = endSoundId;
    }

    public void setStartJudged(boolean startJudged){
        this.startJudged = startJudged;
    }

    public boolean isStartJudged(){
        return startJudged;
    }

    public long getReleasedTime() {
        return releasedTime;
    }

    public void setReleasedTime(long elapsedTime) {
        this.releasedTime = elapsedTime;
    }

    public int getEndSoundId() {
        return endSoundId;
    }
}
