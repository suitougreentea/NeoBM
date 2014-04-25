package io.github.suitougreentea.NeoBM.player;

import java.util.ArrayList;
import java.util.List;

import io.github.suitougreentea.NeoBM.NBM.NBMData;
import io.github.suitougreentea.NeoBM.NBM.NBMSpeedData;
import io.github.suitougreentea.NeoBM.NBM.sequence.*;

public class NBMPlayer {
    private Game game;

    private NBMData data;
    private float nowBPM;
    private int nowBeat;
    private int nowBaseTick;

    private long startTime;

    private float tick = -1;
    private int cursor;

    private List<EventNote> visibleNoteList = new ArrayList<EventNote>();

    private int ALPHA = 1200;
    private int preCursor;
    private long preTime;
    private float preBPM;
    private int preBeat;
    private int preBaseTick;

    private final int JUDGESTART = 1000;
    private final int J_PG = 20;
    private final int J_GR = 75;
    private final int J_GD = 200;
    private final int J_BD = 400;

    private EventNote[] laneActiveNoteList = new EventNote[8];

    //private final int J_PR = 1;


    public NBMPlayer(Game game, NBMData data){
        this.game = game;
        this.data = data;
    }

    public void start(){
        // TODO: tempo, timeを省略した場合の例外
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
        //int resolution = (int) data.getHeaderMap().get("resolution");
        if(tick == -1){
            start();
            tick = 0;
        }

        // speed[t/s] = ((bpm*(resolution/basetick))/60)*resoluton
        //tick += ((nowBPM * (resolution/((float)nowBaseTick)))/60f)*resolution * (game.getDelta()/1000f);
        NBMSpeedData speed = next(game.getCurrentFrameTime() - startTime);
        //System.out.println(speed.getTickPerMilliSecond());
        tick = speed.getTick() + (game.getCurrentFrameTime() - startTime - speed.getTime()) * speed.getTickPerMilliSecond();

        while(data.getSequence().get(cursor).getTick() <= tick){
            Event e = data.getSequence().get(cursor);
            /*if(e instanceof EventTime){ //理想は変更されたtickまで戻って変更後のBPMで進める
                nowBeat = ((EventTime) e).getBeat();
                nowBaseTick = ((EventTime) e).getBaseTick();
            }else if(e instanceof EventTempo){
                nowBPM = ((EventTempo) e).getTempo();
            }*/
            //System.out.println(String.format("%f, %d",speed.getTime() + (e.getTick() - speed.getTick()) * speed.getMilliSecondPerTick(),game.getCurrentFrameTime() - startTime));
            cursor++;
        }

        while(data.getSequence().get(preCursor).getTick() <= tick + ALPHA + 240 /* || JUDGESTART */){
            Event e = data.getSequence().get(preCursor);
            if(e instanceof EventNote) {
                if(speed.getTime() + (e.getTick() - speed.getTick()) * speed.getMilliSecondPerTick() - JUDGESTART < game.getCurrentFrameTime() - startTime
                        && laneActiveNoteList[((EventNote) e).getLane()] == null){
                    laneActiveNoteList[((EventNote) e).getLane()] = (EventNote) e;
                }
                visibleNoteList.add((EventNote)e);
            }
            preCursor++;
        }

        int i=0;
        while(i<visibleNoteList.size()){
            EventNote e = visibleNoteList.get(i);
            if(speed.getTime() + (e.getTick() - get(e.getTick()).getTick()) * get(e.getTick()).getMilliSecondPerTick() + J_BD < game.getCurrentFrameTime() - startTime){
                visibleNoteList.remove(i);
            }else{
                float pos = ((tick-e.getTick())/(ALPHA))+1;
                if(pos>1)pos=1;
                e.setPosition(pos);
                i++;
            }
        }

        for(int lane=0;lane<8;lane++){
            if(laneActiveNoteList[lane] != null){
                if(get(laneActiveNoteList[lane].getTick()).getTime() + (laneActiveNoteList[lane].getTick() - get(laneActiveNoteList[lane].getTick()).getTick()) * get(laneActiveNoteList[lane].getTick()).getMilliSecondPerTick() + J_BD < game.getCurrentFrameTime() - startTime){
                    laneActiveNoteList[lane] = null;
                }else{
                    System.out.println(game.getInputState()[lane]);
                    if(game.getInputState()[lane] == 2){
                        System.out.println(String.format("%d %d", get(laneActiveNoteList[lane].getTick()).getTime() + (laneActiveNoteList[lane].getTick() - get(laneActiveNoteList[lane].getTick()).getTick()) * get(laneActiveNoteList[lane].getTick()).getMilliSecondPerTick(),game.getCurrentFrameTime() - startTime));
                    }
                }
            }
        }

        //System.out.println(String.format("%.10f",tick));
    }

    public NBMData getData(){
        return data;
    }
    public List<EventNote> getActiveNoteList(){
        return visibleNoteList;
    }

    // data.speedList周り
    int speedListCursor = 0;
    public NBMSpeedData next(long nowTime){
        while(speedListCursor < data.getSpeedList().size() - 1){
            //System.out.println(data.getSpeedList().get(speedListCursor).getTime());
            if(data.getSpeedList().get(speedListCursor + 1).getTime() < nowTime) speedListCursor++;
            else break;
        }
        //System.out.println(speedListCursor);
        return data.getSpeedList().get(speedListCursor);
    }

    public NBMSpeedData get(long time){
        int i = speedListCursor;
        while(i < data.getSpeedList().size() - 1){
            if(data.getSpeedList().get(i + 1).getTime() < time) i++;
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
            data.getSpeedList().get(i + 1).setTime(Math.round(time + (data.getSpeedList().get(i + 1).getTick() - tick) * data.getSpeedList().get(i).getMilliSecondPerTick()));
            i++;
            tick = data.getSpeedList().get(i).getTick();
            time = data.getSpeedList().get(i).getTime();
        }
    }

}
