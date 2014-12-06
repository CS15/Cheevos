package org.relos.cheevos.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.relos.cheevos.objects.Achievement;
import org.relos.cheevos.objects.Game;
import org.relos.cheevos.objects.GameDetails;

import java.net.URL;
import java.util.List;

public class AchievementsLoader extends AsyncTaskLoader<Game> {
    // fields
    private Context mContext;
    private Game mGame;
    private String mExMessage;
    private final String BASE_URL;
    private final int GAME_ID;

    public AchievementsLoader(Context context, Game game, String url, int gameId) {
        super(context);

        mContext = context;
        mGame = game;
        BASE_URL = url;
        GAME_ID = gameId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mGame.getAchievements().size() == 0) {
            forceLoad();
        } else {
            deliverResult(mGame);
        }
    }

    @Override
    public Game loadInBackground() {
        // get data in the background
        try {

            Document doc = Jsoup.parse(new URL(BASE_URL).openStream(), "UTF-8", BASE_URL);

            Elements root = doc.getElementsByClass("divtext");
            Elements gameInfoData = doc.getElementsByClass("men_h_content");

            // get achievement list
            if (root != null) {

                int mAchDescCounter = 0;
                int mSize = root.select("td.ac2").size();
                int mAchDescSize = root.select("td.ac1 a.link_ach").size();

                // extract elements info
                for (int i = 0; i < mSize; i++) {

                    // extract elements details
                    String title = root.select("td.ac2 b").get(i).text();
                    String gamerScore = root.select("td.ac4 strong").get(i).text();
                    String image = "";
                    String description = "";

                    // check for secret achievement list
                    if (i < mAchDescSize) {
                        image = root.select("td.ac1 a img").get(i).attr("abs:src");
                        description = root.select("td.ac3").get(mAchDescCounter).text();
                    } else if (title.equals("Secret Achievement")) {
                        image = root.select("td.ac1 img").get(i).attr("abs:src");
                        description = "Continue playing to unlock this secret achievement.";
                    }

                    image = image.replace("lo", "hi");

                    // increase counter
                    mAchDescCounter += 2;

                    // create objects and add it to list
                    Achievement ach = new Achievement();
                    ach.setGameId(GAME_ID);
                    ach.setCoverUrl(image);
                    ach.setTitle(title);
                    ach.setDescription(description);
                    ach.setGamerscore(Integer.parseInt(gamerScore));
                    mGame.getAchievements().add(ach);
                }

                // get game info details
                String developer = gameInfoData.select("a[title]").get(0).text();
                String publisher = gameInfoData.select("a[title]").get(1).text();
                String publisher2 = gameInfoData.select("a[title]").get(2).text();
                String publisher3 = (gameInfoData.select("a[title]").get(3).text() != null) ? gameInfoData.select("a[title]").get(3).text() : "";
                String publisher4 = (gameInfoData.select("a[title]").get(4).text() != null) ? gameInfoData.select("a[title]").get(3).text() : "";
                String publisher5 = (gameInfoData.select("a[title]").get(5).text() != null) ? gameInfoData.select("a[title]").get(3).text() : "";
                String publisher6= (gameInfoData.select("a[title]").get(6).text() != null) ? gameInfoData.select("a[title]").get(3).text() : "";

                mGame.getGameDetails().setDevelopers(new String[] { developer });
                mGame.getGameDetails().setPublishers(new String[] { publisher });

                for (int i = 0; i < gameInfoData.select("a[title]").size(); i++) {
                    // check for multiple game genre
                    //if (mGameInfo.size() == 7) {
                    //    gameInfoData.set(6, (mGameInfo.get(6) + " / " + gameInfoData.select("a[title]").get(i).text()));
                    //} else {
                    //    gameInfoData.add(gameInfoData.select("a[title]").get(i).text());
                    //}
                }

                // get game released dates
                for (int i = 0; i < gameInfoData.select("img[width=16]").size(); i++) {
                    if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("US")) {
                        mGame.getGameDetails().setUsaRelease(gameInfoData.select("img[alt=US]").get(0).nextSibling().toString().trim());
                    } else if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("Europe")) {
                        mGame.getGameDetails().setEuRelease(gameInfoData.select("img[alt=Europe]").get(0).nextSibling().toString().trim());
                    } else if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("Japan")) {
                        mGame.getGameDetails().setJapanRelease(gameInfoData.select("img[alt=Japan]").get(0).nextSibling().toString().trim());
                    }
                }

            }

        } catch (Exception e) {
            // log exception
            Log.e("Exception", e.getMessage());
            mExMessage = e.getMessage();
        }

        return mGame;
    }

    @Override
    public void deliverResult(Game data) {

        if (isStarted() && data != null) {
            super.deliverResult(data);
        } else {
            Toast.makeText(mContext, mExMessage, Toast.LENGTH_LONG).show();
        }
    }
}
