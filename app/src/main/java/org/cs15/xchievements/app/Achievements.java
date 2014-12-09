package org.cs15.xchievements.app;

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
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.AchievementsAdapter;
import org.cs15.xchievements.loaders.AchievementsLoader;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.objects.Achievement;
import org.cs15.xchievements.objects.Game;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Achievements extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Game> {
    private List<Achievement> mList;
    private GameDetails mGameDetails;
    private Game mGame;
    private SlidingPaneLayout mSlidingPane;
    private AchievementsAdapter mAdapter;
    private GridView mLvContent;
    private String mTitle;
    private String mUrl;
    private String mCoverUrl;
    private int mAchsAmount;
    private int mGamerscore;
    private int mGameId;
    private boolean isAnAdmin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onGameDetails();

        if (getIntent().getExtras() != null) {
            mGameId = getIntent().getExtras().getInt("gameId");
            mTitle = getIntent().getExtras().getString("title");
            mUrl = getIntent().getExtras().getString("url");
            mCoverUrl = getIntent().getExtras().getString("coverUrl");
            mAchsAmount = getIntent().getExtras().getInt("achsAmount");
            mGamerscore = getIntent().getExtras().getInt("gamerscore");

            getSupportActionBar().setTitle(mTitle);

            mList = new ArrayList<Achievement>();
            mGameDetails = new GameDetails();
            mGameDetails.setTitle(mTitle);
            mGameDetails.setCoverUrl(mCoverUrl);
            mGameDetails.setGamerscore(mGamerscore);
            mGameDetails.setId(mGameId);
            mGameDetails.setAchievementsAmount(mAchsAmount);

            mGame = new Game();
            mGame.setGameDetails(mGameDetails);
            mGame.setAchievements(mList);

            mAdapter = new AchievementsAdapter(mList, this);

            mLvContent = (GridView) findViewById(R.id.lv_content);
            mLvContent.setAdapter(mAdapter);
            mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });

            if (isAnAdmin) {
                startLoader();
            }
        }
    }

    private void startLoader() {
        if (this.getSupportLoaderManager().getLoader(0) == null) {
            this.getSupportLoaderManager().initLoader(0, null, this);
        } else {
            this.getSupportLoaderManager().restartLoader(0, null, this);
        }
    }

    private void onGameDetails() {
        mSlidingPane = new SlidingPaneLayout(this);
        mSlidingPane = (SlidingPaneLayout) findViewById(R.id.slp_achievements);
        mSlidingPane.setParallaxDistance(30);
        mSlidingPane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {
                // set actionbar title
                getSupportActionBar().setTitle(R.string.ab_game_details);
            }

            @Override
            public void onPanelClosed(View view) {
                // set actionbar title
                getSupportActionBar().setTitle(mTitle);
            }
        });
    }

    private void displayGameDetails(GameDetails gameDetails) {
        // views
        NetworkImageView ivCoverImage = (NetworkImageView) findViewById(R.id.iv_game_details_cover);
        TextView tvGameTitle = (TextView) findViewById(R.id.tv_game_title);
        TextView tvDeveloper = (TextView) findViewById(R.id.tv_developer);
        TextView tvPublisher = (TextView) findViewById(R.id.tv_publisher);
        TextView tvGenre = (TextView) findViewById(R.id.tv_genre);
        TextView tvUsDate = (TextView) findViewById(R.id.tv_us_release);
        TextView tvEuDate = (TextView) findViewById(R.id.tv_eu_release);
        TextView tvJpDate = (TextView) findViewById(R.id.tv_jp_release);

        // data
        String title = gameDetails.getTitle();
        String developer = (gameDetails.getDevelopers() != null) ? gameDetails.getDevelopers() : "N/A";
        String publisher = (gameDetails.getPublishers() != null) ? gameDetails.getPublishers() : "N/A";
        String genres = Arrays.toString(gameDetails.getGenre()).replace("[", "").replace("]", "");
        String usa = (gameDetails.getUsaRelease() != null) ? gameDetails.getUsaRelease() : "N/A";
        String eu = (gameDetails.getEuRelease() != null) ? gameDetails.getEuRelease() : "N/A";
        String japan = (gameDetails.getJapanRelease() != null) ? gameDetails.getJapanRelease() : "N/A";

        // set data
        ivCoverImage.setImageUrl(gameDetails.getCoverUrl(), SingletonVolley.getImageLoader());

        tvGameTitle.setText(String.format("%s", title));
        tvDeveloper.setText(String.format("Developer: %s", developer));
        tvPublisher.setText(String.format("Publisher: %s", publisher));
        tvGenre.setText(String.format("Genre: %s", genres));
        tvUsDate.setText(String.format("USA: %s", usa));
        tvEuDate.setText(String.format("Europe: %s", eu));
        tvJpDate.setText(String.format("Japan: %s", japan));
    }

    @Override
    public Loader<Game> onCreateLoader(int i, Bundle bundle) {
        return new AchievementsLoader(this, mGame, mUrl, mGameId);
    }

    @Override
    public void onLoadFinished(Loader<Game> listLoader, Game games) {
        mList = games.getAchievements();
        mGameDetails = games.getGameDetails();
        mAdapter.notifyDataSetChanged();

        displayGameDetails(mGameDetails);
    }

    @Override
    public void onLoaderReset(Loader<Game> listLoader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_achievements, menu);

        menu.findItem(R.id.menu_upload_game_details).setVisible(isAnAdmin);
        menu.findItem(R.id.menu_upload_achievements).setVisible(isAnAdmin);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_game_details:
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
            overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
        } else {
            mSlidingPane.closePane();
        }
    }
}
