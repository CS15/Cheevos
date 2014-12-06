package org.relos.cheevos.objects;

public class GameDetails {
    // properties
    private int id;
    private String coverUrl;
    private String icoUrl;
    private String title;
    private int achievementsAmount;
    private String achievementsPageUrl;
    private String usaRelease;
    private String euRelease;
    private String japanRelease;
    private String developers;
    private String publishers;
    private String[] genre;
    private int gamerscore;

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

    public String getUsaRelease() {
        return usaRelease;
    }

    public void setUsaRelease(String usaRelease) {
        this.usaRelease = usaRelease;
    }

    public String getEuRelease() {
        return euRelease;
    }

    public void setEuRelease(String euRelease) {
        this.euRelease = euRelease;
    }

    public String getJapanRelease() {
        return japanRelease;
    }

    public void setJapanRelease(String japanRelease) {
        this.japanRelease = japanRelease;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public String[] getGenre() {
        return genre;
    }

    public void setGenre(String[] genre) {
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
}
