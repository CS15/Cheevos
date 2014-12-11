package org.cs15.xchievements.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.AlphabetAdapter;
import org.cs15.xchievements.adapters.GameListAdapter;
import org.cs15.xchievements.loaders.GameListLoader;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;
import java.util.List;

public class GameList extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<GameDetails>> {
    // field
    private List<GameDetails> mList;
    private ListView mAlphabetMenu;
    private SlidingPaneLayout mSlidingPane;
    private GridView mLvContent;
    private GameListAdapter mAdapter;
    private String mUrl = "http://www.xboxachievements.com/browsegames/xbox-one/a/";
    private boolean isAnAdmin = UserProfile.isAnAdmin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // browsing menu titles
        final String[] mAlphabetTitles = getResources().getStringArray(R.array.slide_menu_alphabet);

        onBrowsingMenu();

        mAlphabetMenu = (ListView) findViewById(R.id.lv_alphabet_content);
        mAlphabetMenu.setAdapter(new AlphabetAdapter(this, mAlphabetTitles));
        mAlphabetMenu.setItemChecked(0, true);
        mAlphabetMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mList.clear();
                mAdapter.notifyDataSetChanged();

                if (isAnAdmin) {
                    String alphabetCode = (mAlphabetTitles[position].equals("0-9")) ? "-" : mAlphabetTitles[position].toLowerCase();
                    mUrl = "http://www.xboxachievements.com/browsegames/xbox-one/" + alphabetCode + "/";
                    getData();
                } else {
                    String alphabetCode = (mAlphabetTitles[position].equals("0-9")) ? "-" : mAlphabetTitles[position].toLowerCase();
                    getFromParse(alphabetCode);
                }

                mAlphabetMenu.setItemChecked(position, true);
                mSlidingPane.closePane();
            }
        });

        mList = new ArrayList<GameDetails>();

        mAdapter = new GameListAdapter(mList, this);

        mLvContent = (GridView) findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);
        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(GameList.this, Achievements.class);
                intent.putExtra("title", mList.get(i).getTitle());
                intent.putExtra("url", mList.get(i).getAchievementsPageUrl());
                intent.putExtra("coverUrl", mList.get(i).getCoverUrl());
                intent.putExtra("achsAmount", mList.get(i).getAchievementsAmount());
                intent.putExtra("gamerscore", mList.get(i).getGamerscore());
                intent.putExtra("gameId", mList.get(i).getId());
                startActivity(intent);

                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
            }
        });

        getData();
    }

    private void getData() {
        if (isAnAdmin) {
            if (this.getSupportLoaderManager().getLoader(0) == null) {
                this.getSupportLoaderManager().initLoader(0, null, this);
            } else {
                this.getSupportLoaderManager().restartLoader(0, null, this);
            }
        } else {
            getFromParse("all");
        }
    }

    private void onBrowsingMenu() {
        // instantiate browsing menu
        mSlidingPane = new SlidingPaneLayout(this);
        mSlidingPane = (SlidingPaneLayout) findViewById(R.id.slp_game_list);
        mSlidingPane.setParallaxDistance(30);
        mSlidingPane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {
                // set actionbar title
                getSupportActionBar().setTitle(R.string.ab_title_browse);
            }

            @Override
            public void onPanelClosed(View view) {
                // set actionbar title
                getSupportActionBar().setTitle(R.string.ab_title_game_list);
            }
        });
    }

    @Override
    public Loader<List<GameDetails>> onCreateLoader(int i, Bundle bundle) {
        return new GameListLoader(this, mList, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<GameDetails>> listLoader, List<GameDetails> games) {
        mList = games;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<GameDetails>> listLoader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_game_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_browse:
                // show/close browse menu
                if (!mSlidingPane.isOpen()) {
                    mSlidingPane.openPane();
                } else {
                    mSlidingPane.closePane();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFromParse(String alphabetLetter) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        if (!alphabetLetter.equals("all")) {
            query.whereStartsWith("title", alphabetLetter.toUpperCase());
        }
        query.orderByAscending("title");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : parseObjects) {
                        GameDetails game = new GameDetails();
                        game.setId(obj.getInt("gameId"));
                        game.setCoverUrl(obj.getString("coverUrl"));
                        game.setTitle(obj.getString("title"));
                        game.setAchievementsAmount(obj.getInt("achsAmount"));
                        game.setGamerscore(obj.getInt("gamerscore"));
                        game.setAchievementsPageUrl(obj.getString("achsUrl"));
                        mList.add(game);
                    }

                    mAdapter.notifyDataSetInvalidated();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        // show/close browse menu
        if (!mSlidingPane.isOpen()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
        } else {
            mSlidingPane.closePane();
        }
    }
}
