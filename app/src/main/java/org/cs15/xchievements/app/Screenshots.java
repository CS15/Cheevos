package org.cs15.xchievements.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.adapters.ScreenshotsAdapter;
import org.cs15.xchievements.misc.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class Screenshots extends ActionBarActivity {
    private String mGameId;
    private ListView mLvContent;
    private ScreenshotsAdapter mAdapter;
    private List<String> mList;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mGameId = getIntent().getExtras().getString("gbGameId");
            mTitle = getIntent().getExtras().getString("gameTitle");

            getSupportActionBar().setTitle(mTitle);

            mList = new ArrayList<String>();

            mAdapter = new ScreenshotsAdapter(mList, this);

            mLvContent = (ListView) findViewById(R.id.lv_content);
            mLvContent.setAdapter(mAdapter);
            mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(position)));
                    startActivity(intent);
                }
            });

            new Database(this).getGameImages(mGameId, mList, new Database.IGameImages() {
                @Override
                public void onSuccess(List<String> list) {
                    mList = list;
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String error) {
                    HelperClass.toast(Screenshots.this, error);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        // show/close browse menu
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
    }
}
