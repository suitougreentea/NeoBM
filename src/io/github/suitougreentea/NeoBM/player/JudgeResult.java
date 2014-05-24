package io.github.suitougreentea.NeoBM.player;

public class JudgeResult {
    public static final int JUDGE_MISS_POOR = 1;
    public static final int JUDGE_MISS_POOR_DOUBLE = 2;
    public static final int JUDGE_FAST_POOR = 3;
    public static final int JUDGE_SLOW_POOR = 4;
    public static final int JUDGE_BAD = 5;
    public static final int JUDGE_BAD_LN = 6;
    public static final int JUDGE_GOOD = 7;
    public static final int JUDGE_GREAT = 8;
    public static final int JUDGE_PGREAT = 9;
    public static final int JUDGE_RESTORE_TEMP = -1;

    public int state;
    public int delay;
    public boolean temp;

    public JudgeResult(int state, int delay){
        this.state = state;
        this.delay = delay;
    }

    public JudgeResult(int state, int delay, boolean temp){
        this(state, delay);
        this.temp = temp;
    }

    public int getState() {
        return state;
    }

    public int getDelay() {
        return delay;
    }

    public boolean isTemp(){
        return temp;
    }
}
