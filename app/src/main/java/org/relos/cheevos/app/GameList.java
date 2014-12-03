package org.relos.cheevos.app;

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
import android.widget.ListView;
import android.widget.Toast;

import org.relos.cheevos.R;
import org.relos.cheevos.adapters.AlphabetAdapter;
import org.relos.cheevos.adapters.GameListAdapter;
import org.relos.cheevos.loaders.GameListLoader;
import org.relos.cheevos.objects.Game;

import java.util.ArrayList;
import java.util.List;

public class GameList extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Game>> {
    // field
    private List<Game> mList;
    private ListView mAlphabetMenu;
    private SlidingPaneLayout mSlidingPane;
    private ListView mLvContent;
    private GameListAdapter mAdapter;
    private String url = "http://www.xboxachievements.com/games/xbox-one/";

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

        mList = new ArrayList<Game>();

        mAdapter = new GameListAdapter(mList, this);

        mLvContent = (ListView) findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);
        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(GameList.this, mList.get(i).getCoverUrl(), Toast.LENGTH_LONG).show();
            }
        });

        boolean isAnAdmin = true;

        if (isAnAdmin) {
            this.getSupportLoaderManager().initLoader(0, null, this);
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
    public Loader<List<Game>> onCreateLoader(int i, Bundle bundle) {
        return new GameListLoader(this, mList, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Game>> listLoader, List<Game> games) {
        mList = games;
        mAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Game list: " + games.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<List<Game>> listLoader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.game_list, menu);

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

    @Override
    public void onBackPressed() {

        // show/close browse menu
        if (!mSlidingPane.isOpen()) {
            super.onBackPressed();
        } else {
            mSlidingPane.closePane();
        }
    }
}
