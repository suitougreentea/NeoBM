package io.github.suitougreentea.NeoBM;

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
    NBMHeader header;

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
        header = new NBMHeader();

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
                        if(array[0].equals("title")) header.setTitle(getStringValue(array[1]));
                        else if(array[0].equals("subtitle")) header.setSubtitle(getStringValue(array[1]));
                        else if(array[0].equals("artist")) header.setArtist(getStringValue(array[1]));
                        else if(array[0].equals("subartist")) header.setSubartist(getStringValue(array[1]));
                        else if(array[0].equals("genre")) header.setGenre(getStringValue(array[1]));
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
                    }
                }
            }
            line = r.readLine();
        }
        r.close();

        d.setHeader(header);

        return d;
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
