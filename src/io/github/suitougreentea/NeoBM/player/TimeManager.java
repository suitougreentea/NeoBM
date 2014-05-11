package io.github.suitougreentea.NeoBM.player;

import java.util.List;

import io.github.suitougreentea.NeoBM.NBM.NBMSpeedData;

public class TimeManager {
    private List<NBMSpeedData> list;

    public TimeManager(List<NBMSpeedData> list){
        this.list = list;
    }

    public NBMSpeedData getFromTime(long time){
        int i = 0;
        while(i < list.size() - 1){
            if(list.get(i + 1).getEventTime() < time) i++;
            else break;
        }
        return list.get(i);
    }

    public NBMSpeedData getFromTick(long tick){
        int i = 0;
        while(i < list.size() - 1){
            if(list.get(i + 1).getEventTick() < tick) i++;
            else break;
        }
        return list.get(i);
    }

    public void calculateTime(long nowTime, long nowTick){
        long tick = nowTick;
        long time = nowTime;
        int i = 0;
        while(i < list.size() - 1){
            list.get(i + 1).setEventTime(Math.round(time + (list.get(i + 1).getEventTick() - tick) * list.get(i).getMilliSecondPerTick()));
            i++;
            tick = list.get(i).getEventTick();
            time = list.get(i).getEventTime();
        }
    }

    public float getBeatRate(long tick){
        int i = 0;
        while(i < list.size() - 1){
            if(list.get(i + 1).getEventTick() < tick) i++;
            else break;
        }
        NBMSpeedData speed = list.get(i);
        return ((tick - speed.getEventTick()) % speed.getBaseTick()) / ((float)(speed.getBaseTick()));
    }
}
