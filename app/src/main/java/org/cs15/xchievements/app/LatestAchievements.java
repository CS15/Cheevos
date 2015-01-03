package org.cs15.xchievements.app;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.ParseObject;

import org.cs15.xchievements.R;
import org.cs15.xchievements.repository.Database;
import org.cs15.xchievements.adapters.LatestAchievementsAdapter;
import org.cs15.xchievements.loaders.LatestAchievementsLoader;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.Singleton;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;

/**
 * Latest Achievements fragments
 * <p/>
 * Created by Christian Soler on 9/22/2014.
 */
public class LatestAchievements extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<GameDetails>> {
    private final String BASE_URL = "http://www.xboxachievements.com/archive/achievements/1/";
    // instance variables
    private ListView mLvContent;
    private NetworkImageView mIvBanner;
    private TextView mTvTitle;
    private ArrayList<GameDetails> mList;
    private LatestAchievementsAdapter mAdapter;
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

        mList = new ArrayList<>();
        mAdapter = new LatestAchievementsAdapter(getActivity(), mList);
        mIvBanner = (NetworkImageView) view.findViewById(R.id.iv_latest_image);
        mTvTitle = (TextView) view.findViewById(R.id.tv_latest_image_title);
        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);

        getBannerInfo();
        getUpdatedApk();

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

    private void getBannerInfo() {

        new Database().getBannerInfo(new Database.IBanner() {
            @Override
            public void onSuccess(ParseObject data) {
                mBannerGameId = data.getParseObject("game").getInt("gameId");
                mBannerTitle = data.getParseObject("game").getString("title");
                mBannerCoverUrl = data.getParseObject("game").getString("coverUrl");
                mBannerAchsAmount = data.getParseObject("game").getString("achsAmount");
                mBannerGamerscore = data.getParseObject("game").getString("gamerscore");
                mBannerAchsUrl = data.getParseObject("game").getString("achsUrl");

                mIvBanner.setImageUrl(data.getString("imageUrl"), Singleton.getImageLoader());
                mTvTitle.setText(mBannerTitle);

                // animation
                ObjectAnimator.ofFloat(mTvTitle, "translationY", 200, 0).setDuration(1000).start();
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(getActivity(), error);
            }
        });
    }

    private void getLatestAchs() {

        new Database().getLatestAchs(mList, new Database.ILatestAchs() {
            @Override
            public void onSuccess(ArrayList<GameDetails> data) {
                mList = data;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(getActivity(), error);
            }
        });
    }

    private void getUpdatedApk() {
        new Database(getActivity()).getApkVersion();
    }
}
