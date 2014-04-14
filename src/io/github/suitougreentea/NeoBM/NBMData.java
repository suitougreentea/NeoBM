package io.github.suitougreentea.NeoBM;

import java.util.HashMap;
import java.util.Map;

public class NBMData {
    String docType;
    NBMHeader header;


    //TODO: 初期化はコンストラクタの方がいいかな？
    Map<Integer, String> soundMap = new HashMap<Integer, String>();
    Map<Integer, String> imageMap = new HashMap<Integer, String>();

    public Map<Integer, String> getSoundMap() {
        return soundMap;
    }

    public Map<Integer, String> getImageMap() {
        return imageMap;
    }

    public Map<String, Object> getAliasMap() {
        return aliasMap;
    }

    Map<String, Object> aliasMap = new HashMap<String, Object>();

    public NBMData(String docType) {
        this.docType = docType;
    }

    public String getDocType() {
        return docType;
    }

    public NBMHeader getHeader() {
        return header;
    }

    public void setHeader(NBMHeader header){
        this.header = header;
    }
}