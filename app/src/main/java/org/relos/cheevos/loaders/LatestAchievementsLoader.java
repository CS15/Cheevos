package org.relos.cheevos.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.relos.cheevos.R;
import org.relos.cheevos.app.MainActivity;

import java.net.URL;
import java.util.ArrayList;

/**
 * Latest achievements loader
 * <p/>
 * Created by Christian Soler on 11/28/14.
 */
public class LatestAchievementsLoader extends AsyncTaskLoader<ArrayList<JSONObject>> {
    // instance
    private Context mContext;
    private ArrayList<JSONObject> mList;
    private final String BASE_URL;
    private String mExMessage;

    public LatestAchievementsLoader(Context context, ArrayList<JSONObject> list, String url) {
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
    public ArrayList<JSONObject> loadInBackground() {
        try {
            // local variables
            int counter = 0;

            // get html
            Document doc = Jsoup.parse(new URL(BASE_URL).openStream(), "UTF-8", BASE_URL);

            // get main root
            Element root = doc.getElementsByClass("divtext").first();

            if (!root.toString().equals("")) {
                int tagSize = root.select("div.newsTitle").size();

                for (int i = 0; i < tagSize; i++) {
                    // parse data
                    String imageUrl = root.select("td[width=70] img").get(i).attr("abs:src");
                    String title = root.select("div.newsTitle").get(i).text();
                    String achAmount;
                    String itemPageUrl = root.select("td[width=442] a").get(counter).attr("abs:href");

                    // check mAchAmount element tag format
                    if (root.select("td[width=442]").get(i).select("p").size() > 0) {
                        achAmount = root.select("td[width=442]").get(i).select("p").get(0).textNodes().get(0).text();
                    } else {
                        achAmount = root.select("td[width=442]").get(i).textNodes().get(0).text();
                    }

                    // clean title by removing Game and DLC added tags
                    title = title.contains("Game Added: ") ? title.replace("Game Added: ", "") : title.replace("DLC Added: ", "");

                    // increase item page url counter
                    counter += 2;

                    try {
                        // create json object and add it to json array
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("cover", imageUrl);
                        jsonObj.put("title", title);
                        jsonObj.put("achsAmount", achAmount);
                        jsonObj.put("achsUrl", itemPageUrl);
                        mList.add(jsonObj);

                    } catch (JSONException ex) {
                        Log.e("LatestAchievementsLoader, loadInBackground(): JSONException", ex.getMessage());
                        mExMessage = ex.getMessage();
                        return null;
                    }
                }
            }

            return mList;

        } catch (Exception e) {
            mExMessage = e.getMessage();
            return null;
        }

    }

    @Override
    public void deliverResult(ArrayList<JSONObject> data) {

        if (isStarted() && data != null) {
            super.deliverResult(data);
        } else {
            Toast.makeText(mContext, mExMessage, Toast.LENGTH_LONG).show();
        }
    }
}
