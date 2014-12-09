package org.cs15.xchievements.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.cs15.xchievements.objects.Achievement;
import org.cs15.xchievements.objects.Game;

import java.net.URL;
import java.util.ArrayList;

public class AchievementsLoader extends AsyncTaskLoader<Game> {
    // fields
    private Context mContext;
    private Game mGame;
    private String mExMessage;
    private final String BASE_URL;
    private final int GAME_ID;

    private enum Genre {
        ACTION,
        ADVENTURE,
        CARD_AND_BOARD,
        FIGHTING,
        FITNESS,
        PUZZLE_AND_WORD,
        RPG,
        SHMUP,
        SHOOTER,
        SPORT,
        SPORT_AND_FITNESS,
        STRATEGY,
        RACING
    }

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

                mGame.getGameDetails().setDevelopers(developer);
                mGame.getGameDetails().setPublishers(publisher);

                ArrayList<String> genres = new ArrayList<String>();

                for (Element genre : gameInfoData.select("a[title]")) {
                    for (Genre g : Genre.values()) {
                        if (genre.text().equalsIgnoreCase(g.name().replace("_", " "))) {
                            genres.add(genre.text());
                        }
                    }
                }

                mGame.getGameDetails().setGenre(genres.toArray(new String[genres.size()]));

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
