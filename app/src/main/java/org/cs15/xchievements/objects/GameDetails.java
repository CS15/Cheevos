package org.cs15.xchievements.objects;

import com.parse.ParseObject;

public class GameDetails {
    // properties
    private int id;
    private String gbGameId;
    private String parseId;
    private String coverUrl;
    private String icoUrl;
    private String title;
    private String achievementsPageUrl;
    private String originalUsaReleasedDate;
    private String developers;
    private String publishers;
    private String genre;
    private String summary;
    private int achievementsAmount;
    private int achievementsAmountCompleted;
    private int gamerscore;
    private ParseObject game;

    public int getAchievementsAmountCompleted() {
        return achievementsAmountCompleted;
    }

    public void setAchievementsAmountCompleted(int achievementsAmountCompleted) {
        this.achievementsAmountCompleted = achievementsAmountCompleted;
    }

    public ParseObject getGame() {
        return game;
    }

    public void setGame(ParseObject game) {
        this.game = game;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getAchievementsAmount() {
        return achievementsAmount;
    }

    public void setAchievementsAmount(int achievementsAmount) {
        this.achievementsAmount = achievementsAmount;
    }

    public String getAchievementsPageUrl() {
        return achievementsPageUrl;
    }

    public void setAchievementsPageUrl(String achievementsPageUrl) {
        this.achievementsPageUrl = achievementsPageUrl;
    }

    public String getOriginalUsaReleasedDate() {
        return originalUsaReleasedDate;
    }

    public void setOriginalUsaReleasedDate(String originalUsaReleasedDate) {
        this.originalUsaReleasedDate = originalUsaReleasedDate;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public int getGamerscore() {
        return gamerscore;
    }

    public void setGamerscore(int gamerscore) {
        this.gamerscore = gamerscore;
    }

    public String getIcoUrl() {
        return icoUrl;
    }

    public void setIcoUrl(String icoUrl) {
        this.icoUrl = icoUrl;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public String getGbGameId() {
        return gbGameId;
    }

    public void setGbGameId(String gbGameId) {
        this.gbGameId = gbGameId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


}
