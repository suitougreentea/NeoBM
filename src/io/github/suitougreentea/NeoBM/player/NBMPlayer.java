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

    private int ALPHA = 1000;
    private int preCursor;

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

    private final int PLAYLANES = 8;
    private EventNote[] laneJudgeActiveNoteList = new EventNote[PLAYLANES];
    private EventNote[] laneSoundActiveNoteList = new EventNote[PLAYLANES];
    private int[] laneLastJudge = new int[PLAYLANES];


    public static final int JUDGE_MISS_POOR = 1;
    public static final int JUDGE_FAST_POOR = 2;
    public static final int JUDGE_SLOW_POOR = 3;
    public static final int JUDGE_BAD = 4;
    public static final int JUDGE_BAD_LN = 5;
    public static final int JUDGE_GOOD = 6;
    public static final int JUDGE_GREAT = 7;
    public static final int JUDGE_PGREAT = 8;

    // For Renderer
    private int showJudgeTimer = 0;
    private int lastJudgeState = 0;
    private int lastJudgeDelay = 0;
    private int combo = 0;

    private float[] keyBeam = new float[PLAYLANES];
    private float beatRate;

    private float gauge = 22;
    private int gaugeType;
    private float[] gaugeDelta = new float[]{0,-6f,-2f,-2f,-2f,-2f,100/8,200/8,200/8};
    public static final int GAUGE_NORMAL = 0;

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

    public float[] getKeyBeam(){
        return keyBeam;
    }

    public float getBeatRate(){
        return beatRate;
    }

    public int getCalculatedGauge(){
        return (int) (Math.floor(gauge/2)*2);
    }

    public int getGaugeType(){
        return gaugeType;
    }

    public void setJudge(int judgeState, int delay){
        gauge += gaugeDelta[judgeState];
        if(gauge > 100f) gauge = 100f;
        if(gauge < 2f) gauge = 2f;

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
        /*while(data.getSequence().get(cursor).getTick() == 0){
            Event e = data.getSequence().get(cursor);
            if(e instanceof EventTime){
                nowBeat = ((EventTime) e).getBeat();
                nowBaseTick = ((EventTime) e).getBaseTick();
            }else if(e instanceof EventTempo){
                nowBPM = ((EventTempo) e).getTempo();
            }
            cursor++;
        }*/
        calculateTime(0, 0);
        startTime = game.getCurrentFrameTime();
    }

    public void eachFrame(){
        long elapsedTime = game.getCurrentFrameTime() - startTime;
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

            NBMSpeedData speed = getFromTime(elapsedTime);
            tick = speed.getTick(elapsedTime);

            // 現在のtickのリアルタイム処理
            /*while(data.getSequence().get(cursor).getTick() <= tick){
                //Event e = data.getSequence().get(cursor);
                cursor++;
            }*/

            // 予測(PreCursor)
            while(preCursor < data.getSequence().size() && data.getSequence().get(preCursor).getTick() <= tick + ALPHA + 240 /* || JUDGESTART */){
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

                    if(e instanceof EventLongNote){
                        float endpos = ((tick - (e.getTick()+((EventLongNote)e).getGate()))/(ALPHA))+1;
                        ((EventLongNote)e).setEndPosition(endpos);
                    }

                    // ノートをアクティブ(判定対象)にする
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
                        }else if(e.getTick() < laneJudgeActiveNoteList[lane].getTick()){
                            // 先に次のノートが判定リストに加えられてしまっていて、後から前のノートが出てきた場合 (visibleNoteListは順番通りになっていないため)
                            laneJudgeActiveNoteList[lane] = e;
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
                        visibleNoteList.remove(e);
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
                            //data.getSoundDataMap().get(1).stop();
                            //data.getSoundDataMap().get(1).playAsSoundEffect(1f,1f,false);
                        }
                    }
                }
            }

            if(showJudgeTimer != 0) showJudgeTimer += game.getDelta();
            if(showJudgeTimer > 1000) showJudgeTimer = 0;

            beatRate = getBeatRate((long) tick);
        }

        for(int i=0;i<PLAYLANES;i++){
            if(game.getInputState()[i] >= 2) keyBeam[i] += game.getDelta() / 50f;
            else keyBeam[i] -= game.getDelta() / 150f;
            if(keyBeam[i] > 1f) keyBeam[i] = 1f;
            else if(keyBeam[i] < 0f) keyBeam[i] = 0f;
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

    public NBMSpeedData getFromTime(long time){
        int i = 0;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getEventTime() < time) i++;
            else break;
        }
        return data.getSpeedList().get(i);
    }

    public NBMSpeedData getFromTick(long tick){
        int i = 0;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getEventTick() < tick) i++;
            else break;
        }
        return data.getSpeedList().get(i);
    }

    public void calculateTime(long nowTime, long nowTick){
        long tick = nowTick;
        long time = nowTime;
        int i = 0;
        while(i < data.getSpeedList().size() - 1){
            //System.out.println(data.getSpeedList().get(i).getTime());
            data.getSpeedList().get(i + 1).setEventTime(Math.round(time + (data.getSpeedList().get(i + 1).getEventTick() - tick) * data.getSpeedList().get(i).getMilliSecondPerTick()));
            i++;
            tick = data.getSpeedList().get(i).getEventTick();
            time = data.getSpeedList().get(i).getEventTime();
        }
    }

    public float getBeatRate(long tick){
        int i = 0;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getEventTick() < tick) i++;
            else break;
        }
        NBMSpeedData speed = data.getSpeedList().get(i);
        return ((tick - speed.getEventTick()) % speed.getBaseTick()) / ((float)(speed.getBaseTick()));
    }
}
