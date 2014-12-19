package org.cs15.xchievements.app;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.LatestAchievementsAdapter;
import org.cs15.xchievements.loaders.LatestAchievementsLoader;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.GameDetails;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        getApkVersionCode("Apks");

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
        parseObject.orderByDescending("updatedAt");
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

    private void getApkVersionCode(String className) {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery(className);
        parseObject.orderByDescending("updatedAt");
        parseObject.setLimit(1);
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    try {
                        int apkVersionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;

                        if (apkVersionCode < data.get(0).getInt("versionCode")) {
                            getApk(data.get(0));
                        }

                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.e("ParseObject", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getApk(final ParseObject parseObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_update_apk_message);
        builder.setIcon(R.drawable.ic_app_logo);
        builder.setTitle(R.string.dialog_update_apk_title);
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Downloading...");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                ParseFile apk = (ParseFile) parseObject.get("apk");
                apk.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            try {
                                String path = Environment.getExternalStorageDirectory() + "/Download/xchievement-v" + parseObject.getString("apkVersion") + ".apk";

                                File file = new File(path);
                                file.getParentFile().mkdirs();
                                file.createNewFile();

                                BufferedOutputStream objectOut = new BufferedOutputStream(new FileOutputStream(file));

                                objectOut.write(data);

                                objectOut.close();

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");

                                getActivity().startActivity(intent);

                                progressDialog.dismiss();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                });
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.create();
        builder.show();
    }
}
