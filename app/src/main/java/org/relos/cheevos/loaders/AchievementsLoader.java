package org.relos.cheevos.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.relos.cheevos.objects.Game;

import java.net.URL;
import java.util.List;

public class AchievementsLoader extends AsyncTaskLoader<List<Game>> {
    // fields
    private Context mContext;
    private List<Game> mList;
    private String mExMessage;
    private final String BASE_URL;
    private final int GAME_ID;

    public AchievementsLoader(Context context, List<Game> list, String url, int gameId) {
        super(context);

        mContext = context;
        mList = list;
        BASE_URL = url;
        GAME_ID = gameId;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mList.size() == 0) {
            forceLoad();
        } else {
            deliverResult(mList);
        }
    }

    @Override
    public List<Game> loadInBackground() {
        // get data in the background
        try {
            // get html data
            Document doc = Jsoup.parse(new URL(BASE_URL).openStream(), "UTF-8", BASE_URL);

            // get root elements
            Elements root = doc.getElementsByClass("divtext");
            Elements gameInfoData = doc.getElementsByClass("men_h_content");
            Elements naviBar = doc.getElementsByClass("navbar");

            // get game title
            String mGameTitle = doc.getElementsByClass("tt").first().text();

            // get achievement list
            if (!root.toString().equals("")) {

                int mAchDescCounter = 0;

                // get element sizes to be use with if statement
                int mSize = root.select("td.ac2").size();
                int mAchDescSize = root.select("td.ac1 a.link_ach").size();
                int mNaviSize = naviBar.select("div.pt3 a.link_w2").size();

                // extract elements info
                for (int i = 0; i < mSize; i++) {


                    // extract elements details
                    String title = root.select("td.ac2 b").get(i).text();
                    String gamerScore = root.select("td.ac4 strong").get(i).text();
                    String image = "";
                    String mdesc = "";
                    String itemPageUrl = "";

                    // check for secret achievement list
                    if (i < mAchDescSize) {
                        image = root.select("td.ac1 a img").get(i).attr("abs:src");
                        mdesc = root.select("td.ac3").get(mAchDescCounter).text();
                        itemPageUrl = root.select("td.ac1 a.link_ach").get(i).attr("abs:href");
                    } else if (title.equals("Secret Achievement")) {
                        image = root.select("td.ac1 img").get(i).attr("abs:src");
                        mdesc = "Continue playing to unlock this secret achievement.";
                    }

                    // increase counter
                    mAchDescCounter += 2;

                    // create objects and add it to list
                    Game game = new Game();
                }

                // get game guide url
                String mGameGuide = "";
                String mScreensPageUrl = "";
                for (int i = 0; i < mNaviSize; i++) {
                    if (naviBar.select("div.pt3 a.link_w2").get(i).text().equals("Achievement Guide")) {
                        mGameGuide = naviBar.select("div.pt3 a.link_w2").get(i).attr("abs:href");
                    }

                    if (naviBar.select("div.pt3 a.link_w2").get(i).text().equals("Screens")) {
                        mScreensPageUrl = naviBar.select("div.pt3 a.link_w2").get(i).attr("abs:href");
                    }
                }

                // get game cover url
                String mGameCoverUrl = gameInfoData.select("td[width=125] img").get(0).attr("abs:src");

                // add game info items
                //mGameInfo.add(mGameTitle);
                //mGameInfo.add(mGameCoverUrl);
                //mGameInfo.add(mGameGuide);
                //mGameInfo.add(mScreensPageUrl);

                // get game info details
                for (int i = 0; i < gameInfoData.select("a[title]").size(); i++) {
                    // check for multiple game genre
                    //if (mGameInfo.size() == 7) {
                    //    mGameInfo.set(6, (mGameInfo.get(6) + " / " + gameInfoData.select("a[title]").get(i).text()));
                    //} else {
                    //    mGameInfo.add(gameInfoData.select("a[title]").get(i).text());
                    //}
                }

                // get game released dates
                for (int i = 0; i < gameInfoData.select("img[width=16]").size(); i++) {
                    if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("US")) {
                        //mGameInfo.add("US:" + gameInfoData.select("img[alt=US]").get(0).nextSibling());
                    } else if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("Europe")) {
                        //mGameInfo.add("Europe:" + gameInfoData.select("img[alt=Europe]").get(0).nextSibling());
                    } else if (gameInfoData.select("img[width=16]").get(i).attr("alt").equals("Japan")) {
                        //mGameInfo.add("Japan:" + gameInfoData.select("img[alt=Japan]").get(0).nextSibling());
                    }
                }

            }

        } catch (Exception e) {
            // log exception
            Log.e("Exception", e.getMessage());
            mExMessage = e.getMessage();
        }

        return mList;
    }

    @Override
    public void deliverResult(List<Game> data) {

        if (isStarted() && data != null) {
            super.deliverResult(data);
        } else {
            Toast.makeText(mContext, mExMessage, Toast.LENGTH_LONG).show();
        }
    }
}
