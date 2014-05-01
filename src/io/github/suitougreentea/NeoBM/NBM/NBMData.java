package io.github.suitougreentea.NeoBM.NBM;

import io.github.suitougreentea.NeoBM.NBM.sequence.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.openal.Audio;

public class NBMData {
    private String docType;
    private String parentPath;
    private Map<String, Object> headerMap = new HashMap<String, Object>();
    private Map<Integer, String> soundMap = new HashMap<Integer, String>();
    private Map<Integer, Audio> soundDataMap = new HashMap<Integer, Audio>();
    private Map<Integer, String> imageMap = new HashMap<Integer, String>();
    private Map<String, Object> aliasMap = new HashMap<String, Object>();
    private List<Event> sequence = new ArrayList<Event>();
    private List<NBMSpeedData> speedList = new ArrayList<NBMSpeedData>();

    public Map<Integer, String> getSoundMap() {
        return soundMap;
    }

    public Map<Integer, Audio> getSoundDataMap() {
        return soundDataMap;
    }

    public Map<Integer, String> getImageMap() {
        return imageMap;
    }

    public Map<String, Object> getAliasMap() {
        return aliasMap;
    }

    public List<NBMSpeedData> getSpeedList() {
        return speedList;
    }

    public List<Event> getSequence() {
        return sequence;
    }

    public NBMData(String parentPath, String docType) {
        this.parentPath = parentPath;
        this.docType = docType;
    }

    public String getDocType() {
        return docType;
    }

    public String getParentPath(){
        return parentPath;
    }

    public Map<String, Object> getHeaderMap() {
        return headerMap;
    }
}