package io.github.suitougreentea.NeoBM.player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.github.suitougreentea.NeoBM.NeoBM;
import io.github.suitougreentea.NeoBM.NBM.LoaderThread;
import io.github.suitougreentea.NeoBM.NBM.NBMData;
import io.github.suitougreentea.NeoBM.NBM.NBMSpeedData;
import io.github.suitougreentea.NeoBM.NBM.sequence.*;

public class NBMPlayer {
    private Game game;

    String path;
    private LoaderThread loader;
    private NBMData data;
    private float nowBPM;
    private int nowBeat;
    private int nowBaseTick;

    private long startTime;

    // tick = -2 ... not initialized
    // tick = -1 ... loading
    private float tick = -2;
    private int cursor;

    private Set<EventNote> visibleNoteList = new HashSet<EventNote>();

    private int ALPHA = 800;
    private int preCursor;
    private long preTime;
    private float preBPM;
    private int preBeat;
    private int preBaseTick;

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
    private final int J_GD = 84;
    // sound: active, judge: active (BAD)
    private final int J_BD = 168;
    // sound: active, judge: active if note is PG/GR/GD (POOR)
    private final int J_PR_SLOW = 1000;
    // sound: active, judge: inactive

    private final int PLAYLANES = 8;
    private EventNote[] laneJudgeActiveNoteList = new EventNote[PLAYLANES];
    private int[] laneLastJudge = new int[PLAYLANES];


    public static final int JUDGE_MISS_POOR = 1;
    public static final int JUDGE_FAST_POOR = 2;
    public static final int JUDGE_SLOW_POOR = 3;
    public static final int JUDGE_BAD = 4;
    public static final int JUDGE_GOOD = 5;
    public static final int JUDGE_GREAT = 6;
    public static final int JUDGE_PGREAT = 7;
    private int showJudgeTimer = 0;
    private int lastJudgeState = 0;
    private int lastJudgeDelay = 0;
    private int combo = 0;

    public int getShowJudgeTimer(){
        return showJudgeTimer;
    }


    public int getLastJudgeState() {
        return lastJudgeState;
    }


    public int getLastJudgeDelay() {
        return lastJudgeDelay;
    }

    public int getCombo() {
        return combo;
    }

    public void setJudge(int judgeState, int delay){
        if(judgeState == JUDGE_BAD || judgeState == JUDGE_MISS_POOR){
            combo = 0;
        }else if(judgeState == JUDGE_GOOD || judgeState == JUDGE_GREAT || judgeState == JUDGE_PGREAT){
            combo++;
        }
        this.lastJudgeState = judgeState;
        this.lastJudgeDelay = delay;
        this.showJudgeTimer = 1;
    }

    public NBMPlayer(Game game, String path){
        this.game = game;
        this.path = path;
    }

    public void start(){
        while(data.getSequence().get(cursor).getTick() == 0){
            Event e = data.getSequence().get(cursor);
            if(e instanceof EventTime){
                nowBeat = ((EventTime) e).getBeat();
                nowBaseTick = ((EventTime) e).getBaseTick();
            }else if(e instanceof EventTempo){
                nowBPM = ((EventTempo) e).getTempo();
            }
            cursor++;
        }
        calculateTime(0, 0);
        startTime = game.getCurrentFrameTime();
    }

    public void eachFrame(){
        if(tick == -2){
            loader = new LoaderThread(path);
            loader.start();
            tick = -1;
        }else if(tick == -1){
            float progress = loader.getProgress();
            if(progress == 1.0f){
                this.data = loader.getResult();
                start();
                tick = 0;
            }else{
                NeoBM.logger.info(String.valueOf(progress));
            }
        }else{
            long elapsedTime = game.getCurrentFrameTime() - startTime;
            NBMSpeedData speed = nextFromTime(elapsedTime);
            tick = speed.getTick(elapsedTime);

            // 現在のtickのリアルタイム処理
            /*while(data.getSequence().get(cursor).getTick() <= tick){
                //Event e = data.getSequence().get(cursor);
                cursor++;
            }*/

            // 予測(PreCursor)
            while(data.getSequence().get(preCursor).getTick() <= tick + ALPHA + 240 /* || JUDGESTART */){
                Event e = data.getSequence().get(preCursor);
                if(e instanceof EventNote) {
                    // 可視ノートの一覧に追加
                    visibleNoteList.add((EventNote)e);
                }
                preCursor++;
            }

            // 可視ノートの位置セット/削除/アクティブに
            for (Iterator<EventNote> i = visibleNoteList.iterator(); i.hasNext();) {
                EventNote e = i.next();
                NBMSpeedData s = getFromTick(e.getTick());
                if(s.getTime(e.getTick()) + J_BD < elapsedTime){
                    //i.remove();
                }else{
                    float pos = ((tick-e.getTick())/(ALPHA))+1;
                    if(pos>1)pos=1;
                    e.setPosition(pos);

                    // ノートをアクティブ(キー音鳴る)にする
                    int lane = e.getLane();

                    if(s.getTime(e.getTick()) - J_PR_FAST <= elapsedTime && laneJudgeActiveNoteList[lane] == null){
                        // 前のノートがアクティブでない (早POORがありえる)
                        laneJudgeActiveNoteList[lane] = e;
                        laneLastJudge[lane] = 0;
                    }else if(laneJudgeActiveNoteList[lane] == null){
                        // まだ範囲内でない
                    }else{
                        //NBMSpeedData prevspeed = getFromTick(laneJudgeActiveNoteList[lane].getTick());
                        if(s.getTime(e.getTick()) - J_BD <= elapsedTime && !visibleNoteList.contains(laneJudgeActiveNoteList[lane])){
                            // 次のノートがBAD範囲内 かつ 前のノートがすでに不可視(つまり、一度判定された)
                            laneJudgeActiveNoteList[lane] = e;
                            laneLastJudge[lane] = 0;
                        }
                    }
                }
            }

            for(int lane=0;lane<PLAYLANES;lane++){
                EventNote e = laneJudgeActiveNoteList[lane];
                if(laneJudgeActiveNoteList[lane] != null){
                    NBMSpeedData s = getFromTick(e.getTick());
                    //System.out.println(String.format("%f, %d", s.getTime(e.getTick()) + J_BD, elapsedTime));
                    //カレントレーンの判定が無しになる条件
                    if(s.getTime(e.getTick()) + J_PR_SLOW < elapsedTime){
                        // 遅POORラインより後になった場合
                        laneJudgeActiveNoteList[lane] = null;
                    }else if(s.getTime(e.getTick()) + J_BD < elapsedTime && visibleNoteList.contains(e)){
                        // 遅POORエリアにおいて、ノートが判定されなかった場合、(見逃しPOOR)
                        setJudge(JUDGE_MISS_POOR, 0);
                        visibleNoteList.remove(e);
                        laneJudgeActiveNoteList[lane] = null;
                        /*}else if(s.getTime(e.getTick()) + J_BD < elapsedTime && !visibleNoteList.contains(e) && (laneLastJudge[lane] < -J_GD || J_GD < laneLastJudge[lane])){
                        // 遅POORエリアにおいて、そのノートの判定がBADだった場合
                        System.out.println("called");
                        laneJudgeActiveNoteList[lane] = null; */
                    }else{
                        //System.out.println(game.getInputState()[lane]);
                        if(game.getInputState()[lane] == 2){
                            int delay = Math.round(elapsedTime - s.getTime(e.getTick()));
                            System.out.println(String.format("%d", delay));
                            if(visibleNoteList.contains(e)){
                                //まだノートが判定されていない
                                if(delay < -J_BD){
                                    //早POOR
                                    setJudge(JUDGE_FAST_POOR, delay);
                                }else{
                                    //BAD以降
                                    laneLastJudge[lane] = delay;
                                    if(-J_PG <= delay && delay <= J_PG){
                                        setJudge(JUDGE_PGREAT, delay);
                                    }else if(-J_GR <= delay && delay <= J_GR){
                                        setJudge(JUDGE_GREAT, delay);
                                    }else if(-J_GD <= delay && delay <= J_GD){
                                        setJudge(JUDGE_GOOD, delay);
                                    }else{
                                        setJudge(JUDGE_BAD, delay);
                                        laneJudgeActiveNoteList[lane] = null; 
                                    }
                                    visibleNoteList.remove(e);
                                }
                            }else{
                                //すでに判定された (遅POOR)
                                setJudge(JUDGE_SLOW_POOR, delay);
                            }
                        }
                    }
                }
            }

            if(showJudgeTimer % 1000 == 0) showJudgeTimer = 0; else showJudgeTimer++;
        }

        //System.out.println(String.format("%.10f",tick));
    }

    public float getTick(){
        return tick;
    }

    public NBMData getData(){
        return data;
    }
    public Set<EventNote> getActiveNoteList(){
        return visibleNoteList;
    }

    // data.speedList周り
    int speedListCursor = 0;
    public NBMSpeedData nextFromTime(long nowTime){
        while(speedListCursor < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(speedListCursor + 1).getEventTime() < nowTime) speedListCursor++;
            else break;
        }
        return data.getSpeedList().get(speedListCursor);
    }

    public NBMSpeedData getFromTime(long time){
        int i = speedListCursor;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getEventTime() < time) i++;
            else break;
        }
        return data.getSpeedList().get(i);
    }

    public NBMSpeedData nextFromTick(long nowTick){
        while(speedListCursor < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(speedListCursor + 1).getEventTick() < nowTick) speedListCursor++;
            else break;
        }
        return data.getSpeedList().get(speedListCursor);
    }

    public NBMSpeedData getFromTick(long tick){
        int i = speedListCursor;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getEventTick() < tick) i++;
            else break;
        }
        return data.getSpeedList().get(i);
    }

    public void calculateTime(long nowTime, long nowTick){
        long tick = nowTick;
        long time = nowTime;
        int i = speedListCursor;
        while(i < data.getSpeedList().size() - 1){
            //System.out.println(data.getSpeedList().get(i).getTime());
            data.getSpeedList().get(i + 1).setEventTime(Math.round(time + (data.getSpeedList().get(i + 1).getEventTick() - tick) * data.getSpeedList().get(i).getMilliSecondPerTick()));
            i++;
            tick = data.getSpeedList().get(i).getEventTick();
            time = data.getSpeedList().get(i).getEventTime();
        }
    }

}
