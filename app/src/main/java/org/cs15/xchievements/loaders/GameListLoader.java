package org.cs15.xchievements.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.widget.Toast;

import org.cs15.xchievements.objects.GameDetails;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;

public class GameListLoader extends AsyncTaskLoader<List<GameDetails>> {
    private final String BASE_URL;
    // fields
    private Context mContext;
    private List<GameDetails> mList;
    private String mExMessage;

    public GameListLoader(Context context, List<GameDetails> list, String url) {
        super(context);

        mContext = context;
        mList = list;
        BASE_URL = url;
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
    public List<GameDetails> loadInBackground() {
        try {
            // get html
            Document doc = Jsoup.parse(new URL(BASE_URL).openStream(), "UTF-8", BASE_URL);

            // root element
            Element root = doc.getElementsByClass("divText").first();

            if (root != null) {
                // local variables
                Elements oddRows = root.getElementsByClass("trA1");
                Elements evenRows = root.getElementsByClass("trA2");
                Elements pagination = root.getElementsByClass("pagination").select("a");
                Elements alphabetUrl = root.select("td[colspan=5] a");

                int achsAmountCounter = 0;
                int gamerscoreCounter = 1;
                int pageCounter = 0;

                if (alphabetUrl.size() > 0) {

                }

                if (pagination.size() > 0) {

                }

                for (int i = 0; i < oddRows.size(); i++) {
                    if (i < oddRows.size()) {

                        // extract element details
                        String ico = oddRows.select("td a img").get(i).attr("abs:src");
                        String title = oddRows.select("strong").get(i).text();
                        String achsAmount = oddRows.select("td[align]").get(achsAmountCounter).text();
                        String gamerscore = oddRows.select("td[align]").get(gamerscoreCounter).text();
                        String achsUrl = oddRows.select("td a").get(pageCounter).attr("abs:href");

                        String id = ico.replace("http://www.xboxachievements.com/images/achievements/", "");

                        int index = id.indexOf("/");

                        int gameId = Integer.parseInt(id.substring(0, index));

                        String fileType = ico.substring(ico.lastIndexOf("."), ico.length());

                        String cover = "http://www.xboxachievements.com/images/achievements/" + gameId + "/cover" + fileType;

                        // create object
                        GameDetails game = new GameDetails();
                        game.setId(gameId);
                        game.setCoverUrl(cover);
                        game.setIcoUrl(ico);
                        game.setTitle(title);
                        game.setAchievementsAmount(Integer.parseInt(achsAmount));
                        game.setGamerscore(Integer.parseInt(gamerscore));
                        game.setAchievementsPageUrl(achsUrl);

                        mList.add(game);
                    }

                    if (i < evenRows.size()) {

                        // extract element details
                        String ico = evenRows.select("td a img").get(i).attr("abs:src");
                        String title = evenRows.select("strong").get(i).text();
                        String achsAmount = evenRows.select("td[align]").get(achsAmountCounter).text();
                        String gamerscore = evenRows.select("td[align]").get(gamerscoreCounter).text();
                        String achsUrl = evenRows.select("td a").get(pageCounter).attr("abs:href");

                        String id = ico.replace("http://www.xboxachievements.com/images/achievements/", "");

                        int index = id.indexOf("/");

                        int gameId = Integer.parseInt(id.substring(0, index));

                        String fileType = ico.substring(ico.lastIndexOf("."), ico.length());

                        String cover = "http://www.xboxachievements.com/images/achievements/" + gameId + "/cover" + fileType;

                        // create object
                        GameDetails game = new GameDetails();
                        game.setId(gameId);
                        game.setCoverUrl(cover);
                        game.setIcoUrl(ico);
                        game.setTitle(title);
                        game.setAchievementsAmount(Integer.parseInt(achsAmount));
                        game.setGamerscore(Integer.parseInt(gamerscore));
                        game.setAchievementsPageUrl(achsUrl);

                        mList.add(game);
                    }

                    // increase counters
                    pageCounter += 3;
                    achsAmountCounter += 2;
                    gamerscoreCounter += 2;
                }
            }

        } catch (Exception e) {
            Log.e("Error at Game List: ", e.getMessage());
            mExMessage = e.getMessage();
        }

        return mList;
    }

    @Override
    public void deliverResult(List<GameDetails> data) {

        if (isStarted() && data != null) {
            super.deliverResult(data);
        } else {
            Toast.makeText(mContext, mExMessage, Toast.LENGTH_LONG).show();
        }
    }
}
