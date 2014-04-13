package io.github.suitougreentea.NeoBM;

public class NBMData {
    String docType;
    NBMHeader header;

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