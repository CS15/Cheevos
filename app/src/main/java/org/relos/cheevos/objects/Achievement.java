package org.relos.cheevos.objects;


public class Achievement {
    // properties
    private int gameId;
    private String coverUrl;
    private String title;
    private String Description;
    private int gamerscore;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getGamerscore() {
        return gamerscore;
    }

    public void setGamerscore(int gamerscore) {
        this.gamerscore = gamerscore;
    }
}
