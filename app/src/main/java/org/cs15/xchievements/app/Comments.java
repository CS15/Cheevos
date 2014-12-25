package org.cs15.xchievements.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.adapters.CommentsAdapter;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.Singleton;

import java.util.ArrayList;
import java.util.List;

public class Comments extends ActionBarActivity {
    private NetworkImageView mIvAch;
    private TextView mTvAchTitle;
    private TextView mTvAchSubTitle;
    private ListView mLvContent;
    private List<ParseObject> mList;
    private CommentsAdapter mAdapter;
    private String mCoverUrl;
    private String mGameTitle;
    private String mTitle;
    private String mSubTitle;
    private String mAchParseId;
    private String mNewsFeedId;
    private int mGamerscore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mCoverUrl = getIntent().getExtras().getString("coverUrl");
            mTitle = getIntent().getExtras().getString("title");
            mSubTitle = getIntent().getExtras().getString("subtitle");
            mAchParseId = getIntent().getExtras().getString("achId");
            mGamerscore = getIntent().getExtras().getInt("gamerscore");
            mGameTitle = getIntent().getExtras().getString("gameTitle");
            mNewsFeedId = getIntent().getExtras().getString("newsFeedId");

            getSupportActionBar().setTitle(mGameTitle);

            mList = new ArrayList<>();

            mIvAch = (NetworkImageView) findViewById(R.id.iv_ach_image);
            mIvAch.setImageUrl(mCoverUrl, Singleton.getImageLoader());

            mTvAchTitle = (TextView) findViewById(R.id.tv_ach_title);
            mTvAchTitle.setText(mTitle);

            mTvAchSubTitle = (TextView) findViewById(R.id.tv_ach_subtitle);
            mTvAchSubTitle.setText(mSubTitle);

            getDataFromParse();
        }
    }

    private void getDataFromParse() {
        new Database().getComments(mAchParseId, mNewsFeedId, new Database.IAchComments() {
            @Override
            public void onSuccess(List<ParseObject> data) {
                mAdapter = new CommentsAdapter(Comments.this, data);

                mLvContent = (ListView) findViewById(R.id.lv_content);
                mLvContent.setAdapter(mAdapter);
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Comments.this, error);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getDataFromParse();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_comment).setVisible(ParseUser.getCurrentUser() != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_ach_comments, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_add_comment:
                Intent intent = new Intent(Comments.this, AddComment.class);
                intent.putExtra("parseAchId", mAchParseId);
                intent.putExtra("parseNewsFeedId", mNewsFeedId);

                startActivity(intent);

                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
    }
}
