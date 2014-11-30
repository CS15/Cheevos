package org.relos.cheevos.misc;

public class JsoupHelper {

    public static String getCoverUrl(String url) {
        int gameId = getGameId(url);

        int index = url.lastIndexOf(".");

        String fileType = url.substring(index, url.length());

        return ("http://www.xboxachievements.com/images/achievements/" + gameId + "/cover" + fileType);
    }

    public static int getGameId(String url){
        String id = url.replace("http://www.xboxachievements.com/images/achievements/", "");

        int index = id.indexOf("/");

        id = id.substring(0, index);

        return Integer.parseInt(id);
    }
}
