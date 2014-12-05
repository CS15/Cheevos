package org.relos.cheevos.app;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.relos.cheevos.R;
import org.relos.cheevos.adapters.LatestAchievementsAdapter;
import org.relos.cheevos.loaders.LatestAchievementsLoader;
import org.relos.cheevos.misc.SingletonVolley;
import org.relos.cheevos.objects.GameDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Latest Achievements fragments
 * <p/>
 * Created by Christian Soler on 9/22/2014.
 */
public class LatestAchievements extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<GameDetails>> {
    // instance variables
    private ListView mLvContent;
    private NetworkImageView mIvBanner;
    private TextView mTvTitle;
    private ArrayList<GameDetails> mGameList;
    private LatestAchievementsAdapter mAdapter;
    private final String BASE_URL = "http://www.xboxachievements.com/archive/achievements/1/";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_latest_achievements, container, false);

        mGameList = new ArrayList<GameDetails>();
        mAdapter = new LatestAchievementsAdapter(getActivity(), mGameList);
        mIvBanner = (NetworkImageView) view.findViewById(R.id.iv_latest_image);
        mTvTitle = (TextView) view.findViewById(R.id.tv_latest_image_title);
        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);

        getBannerInfo("Screenshots");

        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Toast.makeText(getActivity(), mGameList.get(position).getAchievementsPageUrl(), Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ArrayList<GameDetails>> onCreateLoader(int i, Bundle bundle) {
        return new LatestAchievementsLoader(getActivity(), mGameList, BASE_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GameDetails>> arrayListLoader, ArrayList<GameDetails> list) {
        mGameList = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<GameDetails>> arrayListLoader) {

    }

    private void getBannerInfo(String className) {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery(className);
        parseObject.orderByDescending("createdAt");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    mIvBanner.setImageUrl(data.get(0).getString("imageUrl"), SingletonVolley.getImageLoader());
                    mTvTitle.setText(data.get(0).getString("gameTitle"));

                    // animation
                    ObjectAnimator.ofFloat(mTvTitle, "translationY", 200, 0).setDuration(1000).start();
                } else {
                    Log.e("ParseObject", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getLatestAchs(String className) {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery(className);
        parseObject.orderByDescending("createdAt");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {

                } else {
                    Log.e("ParseObject", "Error: " + e.getMessage());
                }
            }
        });
    }
}
