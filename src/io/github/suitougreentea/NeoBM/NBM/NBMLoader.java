package io.github.suitougreentea.NeoBM.NBM;

import io.github.suitougreentea.NeoBM.NBM.sequence.Event;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventNote;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventTempo;
import io.github.suitougreentea.NeoBM.NBM.sequence.EventTime;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class NBMLoader {
    public static NBMData loadNBM(String path) throws IOException, NBMSyntaxError {
        return new NBMLoaderPrivate(path).load();
    }
}

class NBMLoaderPrivate {
    //String path;
    LineNumberReader r;

    NBMData d;

    Stack<Integer> level = new Stack<Integer>();
    private static final int LEVEL_HEADER = 1;
    private static final int LEVEL_RESOURCE = 2;
    private static final int LEVEL_SEQUENCE = 3;
    private static final int LEVEL_RANDOM = 4;
    private static final int LEVEL_IF = 5;
    private static final int LEVEL_SWITCH = 6;
    private static final int LEVEL_SOUND = 7;
    private static final int LEVEL_IMAGE = 8;

    public NBMLoaderPrivate(String path) throws FileNotFoundException{
        //this.path = path;
        r = new LineNumberReader(new FileReader(path));
    }

    public NBMData load() throws IOException, NBMSyntaxError{
        String docType = null;

        // First line : .doctype
        String line = r.readLine().trim();
        if(line.startsWith(".doctype")){
            String[] array = getTupleString(line);
            docType = getStringValue(array[1]);
        }else{
            throw new NBMSyntaxError("First line must be started with \".doctype\"", r.getLineNumber());
        }
        d = new NBMData(docType);

        long tick = 0;
        float nowBPM = 0;
        //int nowBeat = 0;
        int nowBaseTick = 0;
        int resolution = 0;

        boolean definedTempo = false;
        boolean definedTime = false;

        // 2+ line
        line = r.readLine();
        while(line != null){
            line = line.trim();
            if(line.equals("")){}
            else if(line.startsWith("//")){}
            else if(level.empty()){
                if(line.equals(".header")){
                    level.push(LEVEL_HEADER);
                }else if(line.equals(".resource")){
                    level.push(LEVEL_RESOURCE);
                }else if(line.equals(".sequence")){
                    level.push(LEVEL_SEQUENCE);
                }else{
                    throw new NBMSyntaxError("Invalid syntax", r.getLineNumber());
                }
            }else{
                if(line.equals(".end")){
                    level.pop();
                }else{
                    if(level.peek() == LEVEL_HEADER){
                        String[] array = getTupleString(line);
                        if(array[0].matches("((sub)?(title|artist)|genre)")) d.getHeaderMap().put(array[0], getStringValue(array[1]));
                        else if(array[0].matches("(resolution)")){
                            resolution = getIntegerValue(array[1]);
                            d.getHeaderMap().put(array[0], resolution);
                        }
                    }else if(level.peek() == LEVEL_RESOURCE) {
                        if(line.equals(".sound")){
                            level.push(LEVEL_SOUND);
                        }else if(line.equals(".image")){
                            level.push(LEVEL_IMAGE);
                        }else{
                            throw new NBMSyntaxError("Invalid syntax", r.getLineNumber());
                        }
                    }else if(level.peek() == LEVEL_SOUND) {
                        String[] array = getTupleString(line);
                        int id = getIntegerValue(array[0]);
                        if(d.getSoundMap().containsKey(id)){
                            throw new NBMSyntaxError("This id is already registered", r.getLineNumber());
                        }else{
                            d.getSoundMap().put(id, getStringValue(array[1]));
                        }
                    }else if(level.peek() == LEVEL_IMAGE) {
                        String[] array = getTupleString(line);
                        int id = getIntegerValue(array[0]);
                        if(d.getImageMap().containsKey(id)){
                            throw new NBMSyntaxError("This id is already registered", r.getLineNumber());
                        }else{
                            d.getImageMap().put(id, getStringValue(array[1]));
                        }
                    }else if(level.peek() == LEVEL_SEQUENCE) {
                        String[] array = getEventTupleString(line);
                        Event e = null;
                        if(array[0].equals("n")){
                            if(definedTime && definedTempo){
                                String[] args = getArgumentsString(array[1], 2);
                                e = new EventNote(getIntegerValue(args[0]), getIntegerValue(args[1]), tick);
                            }else{
                                throw new NBMSyntaxError("Tempo or time event is not defined", r.getLineNumber());
                            }
                        }else if(array[0].equals("time")){
                            String[] args = getArgumentsString(array[1], 2);
                            nowBaseTick = getIntegerValue(args[1]);
                            e = new EventTime(getIntegerValue(args[0]), nowBaseTick, tick);
                            definedTime = true;
                            if(definedTime && definedTempo) addSpeedList(tick, nowBPM, nowBaseTick, resolution);
                        }else if(array[0].equals("tempo")){
                            String[] args = getArgumentsString(array[1], 1);
                            nowBPM = getFloatValue(args[0]);
                            e = new EventTempo(nowBPM, tick);
                            definedTempo = true;
                            if(definedTime && definedTempo) addSpeedList(tick, nowBPM, nowBaseTick, resolution);
                        }
                        int step = getIntegerValue(array[2]);
                        tick += step;
                        d.getSequence().add(e);
                    }
                }
            }
            line = r.readLine();
        }
        r.close();

        return d;
    }

    private void addSpeedList(long tick, float nowBPM, int nowBaseTick, int resolution) {
        d.getSpeedList().add(new NBMSpeedData(
                tick,
                (nowBPM * resolution * resolution) / (nowBaseTick * 60 * 1000),
                (nowBaseTick * 60 * 1000) / (nowBPM * resolution * resolution)
                ));
    }

    /**
     * name:data
     * @param line
     * @return String[] ([0]: name, [1]: data(if nothing, null))
     */
    public String[] getTupleString(String line){
        int point = line.indexOf(":");
        if(point == -1){
            return new String[]{line.trim(), null};
        }else{
            return new String[]{line.substring(0, point).trim(), line.substring(point+1).trim()};
        }
    }

    public String[] getEventTupleString(String line) throws NBMSyntaxError{
        int pointStart = line.indexOf(":");
        int pointEnd = line.lastIndexOf(":");
        if(pointStart == -1) throw new NBMSyntaxError("Missing \":\"", r.getLineNumber());
        if(pointStart == pointEnd){
            // EventWithoutArguments:<Step>
            return new String[]{line.substring(0, pointStart).trim(), null, line.substring(pointStart+1).trim()};
        }else{
            // EventWithArguments:<Args>:<Step>
            return new String[]{line.substring(0, pointStart).trim(), line.substring(pointStart+1, pointEnd).trim(), line.substring(pointEnd+1).trim()};
        }
    }

    //TODO: 個数チェック / ""で囲まれた,の無視
    public String[] getArgumentsString(String line, int requiredCounts) throws NBMSyntaxError{
        if(line == null) throw new NBMSyntaxError(String.format("This event requires %d arguments", requiredCounts), r.getLineNumber());
        return line.split(",");
    }

    public int getIntegerValue(String data) throws NBMSyntaxError {
        if(!data.matches("([1-9][0-9]*|0)")) throw new NBMSyntaxError("Property must be integer", r.getLineNumber());
        return Integer.valueOf(data);
    }

    public float getFloatValue(String data) throws NBMSyntaxError {
        if(!data.matches("([1-9][0-9]*|0)(\\.?[0-9]+)?")) throw new NBMSyntaxError("Property must be number", r.getLineNumber());
        return Float.valueOf(data);
    }

    public String getStringValue(String data) throws NBMSyntaxError {
        if(!data.matches("\".*\"")) throw new NBMSyntaxError("Property must be string", r.getLineNumber());
        return data.substring(1, data.length()-1);
    }
}