package io.github.suitougreentea.NeoBM;

public class NBMHeader {
    private String title, subtitle, artist, subartist, genre;
    private int basebpm, minbpm, maxbpm;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSubartist() {
        return subartist;
    }

    public void setSubartist(String subartist) {
        this.subartist = subartist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}
