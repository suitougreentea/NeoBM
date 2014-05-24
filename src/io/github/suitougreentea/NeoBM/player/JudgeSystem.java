package io.github.suitougreentea.NeoBM.player;

import io.github.suitougreentea.NeoBM.NBM.sequence.EventNote;

public class JudgeSystem {
    private TimeManager timeManager;



    /*
     * JUDGE 仕様
     */
    // sound: inactive, judge: inactive
    private final int J_PR_FAST = 1000;
    // sound: active if previous note is inactive, judge: active if previous note is inactive (POOR)
    // J_BD
    /*
     * ・前のノートの空POORは次のノートのBAD範囲まで続く
     * ・(前のノートが判定されるまで次のノートはアクティブにならない)
     */
    // sound: active, judge: active (OPTIONAL if previous note is judged)(BAD)
    // J_GD
    // (GOOD)
    // J_GR
    // (GREAT)
    // J_PG
    // (PGREAT)
    private final int J_PG = 21;
    // (GREAT)
    private final int J_GR = 42;
    // (GOOD)
    private final int J_GD = 150;
    // sound: active, judge: active (BAD)
    private final int J_BD = 600;
    // sound: active, judge: active if note is PG/GR/GD (POOR)
    private final int J_PR_SLOW = 1000;
    // sound: active, judge: inactive

    private final int backSpinThreshold = 100;

    public JudgeSystem(TimeManager timeManager){
        this.timeManager = timeManager;
    }

    public boolean needJudgeQueue(){
        return false;
    }

    public boolean needKeySoundQueue(){
        return false;
    }

    public boolean needJudgeRefresh(EventNote oldEvent, EventNote newEvent, long newEventTime, long elapsedTime){
        if(oldEvent == null && newEventTime - J_PR_FAST <= elapsedTime){
            // 前のノートがアクティブでない
            return true;
        }else if(oldEvent.isJudged() && newEventTime - J_BD <= elapsedTime){
            // 次のノートがBAD範囲内 かつ前のノートが判定された
            return true;
        }
        return false;
    }

    public boolean needKeySoundRefresh(EventNote oldEvent, EventNote newEvent, long newEventTime, long elapsedTime){
        return true;
    }

    public JudgeResult checkJudge(int keyState, EventNote event, long eventTime, long elapsedTime){
        int delay = (int) (elapsedTime - eventTime);
        if(keyState == 2){
            if(!event.isJudged()){
                //まだノートが判定されていない
                if(delay < -J_BD){
                    //早POOR
                    return new JudgeResult(JudgeResult.JUDGE_FAST_POOR, delay);
                }else{
                    //BAD以降
                    //laneLastJudge[lane] = delay;
                    if(-J_PG <= delay && delay <= J_PG){
                        return new JudgeResult(JudgeResult.JUDGE_PGREAT, delay);
                    }else if(-J_GR <= delay && delay <= J_GR){
                        return new JudgeResult(JudgeResult.JUDGE_GREAT, delay);
                    }else if(-J_GD <= delay && delay <= J_GD){
                        return new JudgeResult(JudgeResult.JUDGE_GOOD, delay);
                    }else{
                        return new JudgeResult(JudgeResult.JUDGE_BAD, delay);
                    }
                }
            }else{
                //すでに判定された (遅POOR)
                return new JudgeResult(JudgeResult.JUDGE_SLOW_POOR, delay);
            }
        }else{
            return null;
        }
    }

    public boolean checkRemove(int keyState, EventNote event, long eventTime, long elapsedTime){
        if(eventTime + J_PR_SLOW < elapsedTime){
            return true;
        }else if(keyState == 2 && -J_BD < elapsedTime - eventTime && elapsedTime - eventTime < J_BD){
            return true;
        }
        return false;
    }

    public boolean checkUnregister(int keyState, EventNote event, long eventTime, long elapsedTime){
        if(eventTime + J_PR_SLOW < elapsedTime){
            return true;
        }else if(keyState == 2 && -J_BD < elapsedTime - eventTime && elapsedTime - eventTime < J_BD){
            if(-J_GD < elapsedTime - eventTime && elapsedTime - eventTime < J_GD){
                // GOOD-PGREAT
                return false;
            }else{
                // BAD
                return true;
            }
        }
        return false;
    }

    public JudgeResult checkJudgeLongNote(int keyState, EventNote event, long eventStartTime, long eventEndTime, long elapsedTime){
        int delay = (int) (elapsedTime - eventStartTime);
        if(keyState == 2){
            if(!event.isJudged()){
                //まだノートが判定されていない
                if(delay < -J_BD){
                    //早POOR
                    return new JudgeResult(JudgeResult.JUDGE_FAST_POOR, delay);
                }else{
                    //BAD以降
                    //laneLastJudge[lane] = delay;
                    if(-J_PG <= delay && delay <= J_PG){
                        return new JudgeResult(JudgeResult.JUDGE_PGREAT, delay);
                    }else if(-J_GR <= delay && delay <= J_GR){
                        return new JudgeResult(JudgeResult.JUDGE_GREAT, delay);
                    }else if(-J_GD <= delay && delay <= J_GD){
                        return new JudgeResult(JudgeResult.JUDGE_GOOD, delay);
                    }else{
                        return new JudgeResult(JudgeResult.JUDGE_BAD, delay);
                    }
                }
            }else{
                //すでに判定された (遅POOR)
                return new JudgeResult(JudgeResult.JUDGE_SLOW_POOR, delay);
            }
        }else{
            return null;
        }
    }

    public boolean checkRemoveLongNote(int keyState, EventNote event, long eventStartTime, long eventEndTime, long elapsedTime){
        return eventStartTime + J_PR_SLOW < elapsedTime;
    }

    public boolean checkUnregisterLongNote(int keyState, EventNote event, long eventStartTime, long eventEndTime, long elapsedTime){
        /*if(eventEndTime + J_PR_SLOW < elapsedTime){
            return true;
        }else if(keyState == 2 && -J_BD < elapsedTime - eventTime && elapsedTime - eventTime < J_BD){
            if(-J_GD < elapsedTime - eventTime && elapsedTime - eventTime < J_GD){
                // GOOD-PGREAT
                return false;
            }else{
                // BAD
                return true;
            }
        }*/
        return false;
    }
}
