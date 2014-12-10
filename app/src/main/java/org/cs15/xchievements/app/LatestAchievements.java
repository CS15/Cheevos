package org.cs15.xchievements.app;

import android.animation.ObjectAnimator;
import android.content.Intent;
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

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.LatestAchievementsAdapter;
import org.cs15.xchievements.loaders.LatestAchievementsLoader;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.GameDetails;

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
    private ArrayList<GameDetails> mList;
    private LatestAchievementsAdapter mAdapter;
    private final String BASE_URL = "http://www.xboxachievements.com/archive/achievements/1/";
    private boolean mIsAnAdmin = UserProfile.isAnAdmin();
    private int mBannerGameId;
    private String mBannerTitle;
    private String mBannerAchsUrl;
    private String mBannerGamerscore;
    private String mBannerCoverUrl;
    private String mBannerAchsAmount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_latest_achievements, container, false);

        mList = new ArrayList<GameDetails>();
        mAdapter = new LatestAchievementsAdapter(getActivity(), mList);
        mIvBanner = (NetworkImageView) view.findViewById(R.id.iv_latest_image);
        mTvTitle = (TextView) view.findViewById(R.id.tv_latest_image_title);
        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);

        getBannerInfo("Screenshots");

        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Intent intent = new Intent(getActivity(), Achievements.class);
                intent.putExtra("title", mList.get(i).getTitle());
                intent.putExtra("url", mList.get(i).getAchievementsPageUrl());
                intent.putExtra("coverUrl", mList.get(i).getCoverUrl());
                intent.putExtra("achsAmount", mList.get(i).getAchievementsAmount());
                intent.putExtra("gamerscore", mList.get(i).getGamerscore());
                intent.putExtra("gameId", mList.get(i).getId());
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
            }
        });

        mIvBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Achievements.class);
                intent.putExtra("title", mBannerTitle);
                intent.putExtra("url", mBannerAchsUrl);
                intent.putExtra("coverUrl", mBannerCoverUrl);
                intent.putExtra("achsAmount", mBannerAchsAmount);
                intent.putExtra("gamerscore", mBannerGamerscore);
                intent.putExtra("gameId", mBannerGameId);
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData();
    }

    @Override
    public Loader<ArrayList<GameDetails>> onCreateLoader(int i, Bundle bundle) {
        return new LatestAchievementsLoader(getActivity(), mList, BASE_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<GameDetails>> arrayListLoader, ArrayList<GameDetails> list) {
        mList = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<GameDetails>> arrayListLoader) {

    }

    private void getData() {
        if (mIsAnAdmin) {
            if (this.getLoaderManager().getLoader(0) == null) {
                this.getLoaderManager().initLoader(0, null, this);
            } else {
                this.getLoaderManager().restartLoader(0, null, this);
            }
        } else {
            getLatestAchs();
        }
    }

    private void getBannerInfo(String className) {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery(className);
        parseObject.orderByDescending("createdAt");
        parseObject.setLimit(1);
        parseObject.include("game");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    mBannerGameId = data.get(0).getParseObject("game").getInt("gameId");
                    mBannerTitle = data.get(0).getParseObject("game").getString("title");
                    mBannerCoverUrl = data.get(0).getParseObject("game").getString("coverUrl");
                    mBannerAchsAmount = data.get(0).getParseObject("game").getString("achsAmount");
                    mBannerGamerscore = data.get(0).getParseObject("game").getString("gamerscore");
                    mBannerAchsUrl = data.get(0).getParseObject("game").getString("achsUrl");

                    mIvBanner.setImageUrl(data.get(0).getString("imageUrl"), SingletonVolley.getImageLoader());
                    mTvTitle.setText(mBannerTitle);

                    // animation
                    ObjectAnimator.ofFloat(mTvTitle, "translationY", 200, 0).setDuration(1000).start();
                } else {
                    Log.e("ParseObject", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getLatestAchs() {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery("Games");
        parseObject.orderByDescending("createdAt");
        parseObject.setLimit(25);
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    for (ParseObject g : data) {
                        GameDetails game = new GameDetails();
                        game.setId(g.getInt("gameId"));
                        game.setCoverUrl(g.getString("coverUrl"));
                        game.setTitle(g.getString("title"));
                        game.setAchievementsAmount(g.getInt("achsAmount"));
                        mList.add(game);
                    }
                } else {
                    Toast.makeText(getActivity(), "Error loading games: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
