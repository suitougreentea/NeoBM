package io.github.suitougreentea.NeoBM.player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import io.github.suitougreentea.NeoBM.NeoBM;
import io.github.suitougreentea.NeoBM.NBM.LoaderThread;
import io.github.suitougreentea.NeoBM.NBM.NBMData;
import io.github.suitougreentea.NeoBM.NBM.NBMSpeedData;
import io.github.suitougreentea.NeoBM.NBM.sequence.*;
//TODO: 各レーンの次のノート
public class NBMPlayer {
    private Game game;
    private TimeManager timeManager;
    private JudgeSystem judgeSystem = new JudgeSystem(timeManager);

    String path;
    private LoaderThread loader;
    private NBMData data;
    //private float nowBPM;
    //private int nowBeat;
    //private int nowBaseTick;

    private long startTime;

    // tick = -2 ... not initialized
    // tick = -1 ... loading
    private float tick = -2;
    private int cursor;

    // 表示されているノート
    private Set<EventNote> visibleNoteList = new HashSet<EventNote>();
    private int visibleNoteListCursor;
    // 判定準備に入っているノート(FASTPOORラインより前に追加される)
    private LinkedList<EventNote> noteJudgeQueue = new LinkedList<EventNote>();
    private int noteJudgeQueueCursor;
    // 判定準備に入っているノート(FASTPOORラインより前に追加される)
    private LinkedList<EventNote> noteSoundQueue = new LinkedList<EventNote>();
    private int noteSoundQueueCursor;



    private int ALPHA = 1000;

    private final int J_PR_FAST = 1000;


    private final int PLAYLANES = 8;
    private EventNote[] laneJudgeActiveNoteList = new EventNote[PLAYLANES];
    private EventNote[] laneSoundActiveNoteList = new EventNote[PLAYLANES];
    private JudgeResult[] laneLastJudge = new JudgeResult[PLAYLANES];

    private boolean chargeNote = false;

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
    private float[] gaugeDelta;// = new float[]{0,-6f,-2f,-2f,-2f,-2f,100/8,200/8,200/8};
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

    public void setJudge(JudgeResult result){
        int judgeState = result.state;
        int delay = result.delay;
        gauge += gaugeDelta[judgeState];
        if(gauge > 100f) gauge = 100f;
        if(gauge < 2f) gauge = 2f;

        if(judgeState == JudgeResult.JUDGE_BAD || judgeState == JudgeResult.JUDGE_MISS_POOR || judgeState == JudgeResult.JUDGE_MISS_POOR_DOUBLE){
            combo = 0;
        }else if(judgeState == JudgeResult.JUDGE_GOOD || judgeState == JudgeResult.JUDGE_GREAT || judgeState == JudgeResult.JUDGE_PGREAT){
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
        timeManager.calculateTime(0, 0);
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

                // initialize
                timeManager = new TimeManager(data.getSpeedList());
                chargeNote = (boolean) data.getHeaderMap().get("chargenote");

                int totalNotes = (int) data.getHeaderMap().get("notes");
                float total = (float) data.getHeaderMap().get("total");
                float a = total / totalNotes;
                gaugeDelta = new float[]{0,-6f,-12f,-2f,-2f,-2f,-2f,a/2,a,a};
                tick = 0;

                start();
            }else{
                NeoBM.logger.info(String.valueOf(progress));
            }
        }else{
            NBMSpeedData speed = timeManager.getSpeedDataFromTime(elapsedTime);
            tick = speed.getTick(elapsedTime);

            // 現在のtickのリアルタイム処理
            while(cursor < data.getSequence().size() && data.getSequence().get(cursor).getTick() <= tick){
                Event e = data.getSequence().get(cursor);
                if(e instanceof EventSound){
                    data.getSoundDataMap().get(((EventSound) e).getSoundId()).stop();
                    data.getSoundDataMap().get(((EventSound) e).getSoundId()).playAsSoundEffect(1f,1f,false);
                }
                cursor++;
            }

            // visibleNoteListに追加
            while(visibleNoteListCursor < data.getSequence().size() && data.getSequence().get(visibleNoteListCursor).getTick() <= tick + ALPHA + 240){
                Event e = data.getSequence().get(visibleNoteListCursor);
                if(e instanceof EventNote) {
                    visibleNoteList.add((EventNote)e);
                }
                visibleNoteListCursor++;
            }

            // noteJudgeQueueに追加
            while(noteJudgeQueueCursor < data.getSequence().size()){
                Event e = data.getSequence().get(noteJudgeQueueCursor);
                NBMSpeedData s = timeManager.getSpeedDataFromTick(e.getTick());
                if(s.getTime(e.getTick()) - J_PR_FAST > elapsedTime) break;
                if(e instanceof EventNote) {
                    noteJudgeQueue.add((EventNote)e);
                }
                noteJudgeQueueCursor++;
            }

            // noteSoundQueueに追加
            while(noteSoundQueueCursor < data.getSequence().size()){
                Event e = data.getSequence().get(noteSoundQueueCursor);
                NBMSpeedData s = timeManager.getSpeedDataFromTick(e.getTick());
                if(s.getTime(e.getTick()) -  J_PR_FAST > elapsedTime) break;
                if(e instanceof EventNote) {
                    noteSoundQueue.add((EventNote)e);
                }
                noteSoundQueueCursor++;
            }

            // ノートの判定をアクティブにする
            for (Iterator<EventNote> i = noteJudgeQueue.iterator(); i.hasNext();) {
                EventNote e = i.next();
                NBMSpeedData s = timeManager.getSpeedDataFromTick(e.getTick());
                int lane = e.getLane();
                if(judgeSystem.needJudgeRefresh(laneJudgeActiveNoteList[lane], e, elapsedTime, (long) s.getTime(e.getTick()))){
                    laneJudgeActiveNoteList[lane] = e;
                    laneLastJudge[lane] = null;
                    i.remove();
                }
            }

            // ノートのキー音をアクティブにする
            for (Iterator<EventNote> i = noteSoundQueue.iterator(); i.hasNext();) {
                EventNote e = i.next();
                NBMSpeedData s = timeManager.getSpeedDataFromTick(e.getTick());
                int lane = e.getLane();
                if(judgeSystem.needKeySoundRefresh(laneJudgeActiveNoteList[lane], e, elapsedTime, (long) s.getTime(e.getTick()))){
                    laneSoundActiveNoteList[lane] = e;
                    i.remove();
                }
            }

            // 判定処理
            for(int lane=0;lane<PLAYLANES;lane++){
                EventNote e = laneJudgeActiveNoteList[lane];
                if(e != null){
                    NBMSpeedData s = timeManager.getSpeedDataFromTick(e.getTick());
                    if(e instanceof EventLongNote){
                        long endTick = e.getTick() + ((EventLongNote) e).getGate();
                        NBMSpeedData se = timeManager.getSpeedDataFromTick(endTick);
                        JudgeResult result = judgeSystem.checkJudgeLongNote(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), (long) se.getTime(endTick), elapsedTime);
                        if(result != null){
                            if(result.getState() != JudgeResult.JUDGE_FAST_POOR && result.getState() != JudgeResult.JUDGE_SLOW_POOR){
                                e.setJudged(true);
                            }
                            setJudge(result);
                        }
                        if(judgeSystem.checkRemoveLongNote(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), (long) se.getTime(endTick), elapsedTime)){
                            visibleNoteList.remove(e);
                        }
                        if(judgeSystem.checkUnregisterLongNote(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), (long) se.getTime(endTick), elapsedTime)){
                            laneJudgeActiveNoteList[lane] = null;
                        }
                    }else{
                        JudgeResult result = judgeSystem.checkJudge(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), elapsedTime);
                        if(result != null){
                            if(result.getState() != JudgeResult.JUDGE_FAST_POOR && result.getState() != JudgeResult.JUDGE_SLOW_POOR){
                                e.setJudged(true);
                            }
                            setJudge(result);
                        }
                        if(judgeSystem.checkRemove(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), elapsedTime)){
                            visibleNoteList.remove(e);
                        }
                        if(judgeSystem.checkUnregister(game.getInputState()[lane], e, (long) s.getTime(e.getTick()), elapsedTime)){
                            laneJudgeActiveNoteList[lane] = null;
                        }   
                    }
                }
            }

            if(showJudgeTimer != 0) showJudgeTimer += game.getDelta();
            if(showJudgeTimer > 1000) showJudgeTimer = 0;

            beatRate = timeManager.getBeatRate((long) tick);
        }

        for(int i=0;i<PLAYLANES;i++){
            if(game.getInputState()[i] >= 2) keyBeam[i] += game.getDelta() / 1f;
            else keyBeam[i] -= game.getDelta() / 150f;
            if(keyBeam[i] > 1f) keyBeam[i] = 1f;
            else if(keyBeam[i] < 0f) keyBeam[i] = 0f;
        }
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

    /*public EventNote findNextSameLaneNote(EventNote previousNote){
        int i = cursor;
        return null;
        while(data.getSequence().size()){

        }
    }*/

    public float getNotePosition(EventNote e){
        float pos = ((tick-e.getTick())/(ALPHA))+1;

        if(!(e instanceof EventLongNote)){
            if(pos>1)pos=1;
        }

        return pos;
    }

    public float getNoteEndPosition(EventLongNote e){
        float endpos = ((tick - (e.getTick()+e.getGate()))/(ALPHA))+1;
        e.setEndPosition(endpos);

        return endpos;
    }
}
