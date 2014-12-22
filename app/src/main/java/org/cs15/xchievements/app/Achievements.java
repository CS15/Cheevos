package org.cs15.xchievements.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ListViewCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.adapters.AchievementsAdapter;
import org.cs15.xchievements.loaders.AchievementsLoader;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.Singleton;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.Achievement;
import org.cs15.xchievements.objects.Game;
import org.cs15.xchievements.objects.GameDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Achievements extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Game> {
    private List<Achievement> mList;
    private GameDetails mGameDetails;
    private Game mGame;
    private SlidingPaneLayout mSlidingPane;
    private AchievementsAdapter mAdapter;
    private ActionMode mActionMode;
    private ActionModeCallback mActionModeCallback;
    private ListView mLvContent;
    private String mTitle;
    private String mUrl;
    private String mCoverUrl;
    private String mParseGameId;
    private boolean mIsFavorite;
    private int mAchsAmount;
    private int mGamerscore;
    private int mGameId;

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

            mList = new ArrayList<>();
            mGameDetails = new GameDetails();
            mGameDetails.setGamerscore(mGamerscore);
            mGameDetails.setId(mGameId);
            mGameDetails.setTitle(mTitle);
            mGameDetails.setCoverUrl(mCoverUrl);
            mGameDetails.setAchievementsAmount(mAchsAmount);
            mGameDetails.setAchievementsPageUrl(mUrl);

            mGame = new Game();
            mGame.setGameDetails(mGameDetails);
            mGame.setAchievements(mList);

            mAdapter = new AchievementsAdapter(mList, this);

            mLvContent = (ListView) findViewById(R.id.lv_content);
            mLvContent.setAdapter(mAdapter);
            mLvContent.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    mLvContent.setChoiceMode(ListViewCompat.CHOICE_MODE_MULTIPLE);
                    mLvContent.setItemChecked(position, true);

                    if (mActionMode == null) {
                        mActionModeCallback = new ActionModeCallback();
                        mActionMode = startSupportActionMode(mActionModeCallback);
                    }

                    return true;
                }
            });

            mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mActionMode == null) {
                        Intent intent = new Intent(Achievements.this, AchComments.class);
                        intent.putExtra("coverUrl", mList.get(i).getCoverUrl());
                        intent.putExtra("title", mList.get(i).getTitle());
                        intent.putExtra("subtitle", mList.get(i).getDescription());
                        intent.putExtra("gamerscore", mList.get(i).getGamerscore());
                        intent.putExtra("gameTitle", mGameDetails.getTitle());
                        intent.putExtra("achId", mList.get(i).getParseId());
                        startActivity(intent);

                        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
                    }
                }
            });

            getData();
        }
    }

    private void getData() {
        if (UserProfile.isAnAdmin()) {
            if (this.getSupportLoaderManager().getLoader(0) == null) {
                this.getSupportLoaderManager().initLoader(0, null, this);
            } else {
                this.getSupportLoaderManager().restartLoader(0, null, this);
            }
        } else {
            getGameDetails();
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
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onPanelClosed(View view) {
                // set actionbar title
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
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
        TextView tvSummary = (TextView) findViewById(R.id.tv_summary);

        // data
        String title = gameDetails.getTitle();
        String developer = (gameDetails.getDevelopers() != null) ? gameDetails.getDevelopers() : "N/A";
        String publisher = (gameDetails.getPublishers() != null) ? gameDetails.getPublishers() : "N/A";
        String genres = gameDetails.getGenre();
        String usa = (gameDetails.getUsaRelease() != null) ? new SimpleDateFormat("MMM dd, yyyy").format(new Date(gameDetails.getUsaRelease().replace("-", "/"))) : "N/A";
        String summary = (gameDetails.getSummary() != null) ? gameDetails.getSummary() : "N/A";

        // set data
        ivCoverImage.setImageUrl(gameDetails.getCoverUrl(), Singleton.getImageLoader());

        tvGameTitle.setText(String.format("%s", title));
        tvDeveloper.setText(String.format("Developer: %s", developer));
        tvPublisher.setText(String.format("Publisher: %s", publisher));
        tvGenre.setText(String.format("Genre: %s", genres));
        tvUsDate.setText(String.format("Original Release Date: %s", usa));
        tvSummary.setText(String.format("%s", summary));
    }

    private void getGameDetails() {
        new Database(this).getGameDetails(mGameDetails.getGbGameId(), mGameDetails, new Database.IGameDetails() {
            @Override
            public void onSuccess(GameDetails game) {
                mGameDetails = game;

                if (UserProfile.getCurrentUser() != null) isFavorite();

                displayGameDetails(mGameDetails);
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Achievements.this, error);
            }
        });

        new Database().getAchievements(mGameDetails.getId(), mList, new Database.IAchievements() {
            @Override
            public void onSuccess(List<Achievement> data) {
                mList = data;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Achievements.this, error);
            }
        });
    }

    private void saveGameDetails() {
        new Database().saveGameDetails(mGameDetails, new Database.ISaveGameDetails() {
            @Override
            public void onSuccess(String parseObjectId) {
                mParseGameId = parseObjectId;

                HelperClass.toast(Achievements.this, "Successfully uploaded to parse.");
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Achievements.this, error);
            }
        });
    }

    private void saveAchievements() {
        new Database().saveAchievements(mGameDetails, mList, mParseGameId, new Database.ISaveAchievements() {
            @Override
            public void onSuccess(String parseGameId, String message) {
                mParseGameId = parseGameId;
                HelperClass.toast(Achievements.this, message);
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Achievements.this, error);
            }
        });
    }

    private void addToFavorites() {
        new Database().addToFavorites(mIsFavorite, mGameDetails.getGame(), new Database.IAddToFavorites() {
            @Override
            public void onSuccess(String message, boolean isFavorite) {
                mIsFavorite = isFavorite;
                supportInvalidateOptionsMenu();
                HelperClass.toast(Achievements.this, message);
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(Achievements.this, error);
            }
        });
    }

    private void isFavorite() {
        new Database().checkIfFavorite(mGameDetails.getParseId(), new Database.IIsFavorite() {
            @Override
            public void onSuccess(boolean isFavorite) {
                mIsFavorite = isFavorite;
                supportInvalidateOptionsMenu();
            }
        });
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

        Toast.makeText(Achievements.this, "Loaded From Page!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(Loader<Game> listLoader) {

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_upload_game_details).setVisible(UserProfile.isAnAdmin());
        menu.findItem(R.id.menu_upload_achievements).setVisible(UserProfile.isAnAdmin());

        if (!mSlidingPane.isOpen()) {
            menu.findItem(R.id.menu_game_details).setIcon(R.drawable.ic_action_about);
        } else {
            menu.findItem(R.id.menu_game_details).setIcon(R.drawable.ic_action_ach_logo);
        }

        if (UserProfile.getCurrentUser() != null) {
            menu.findItem(R.id.menu_add_to_fave).setVisible(true);

            if (mIsFavorite) {
                menu.findItem(R.id.menu_add_to_fave).setIcon(R.drawable.ic_action_toggle_star);
                menu.findItem(R.id.menu_add_to_fave).setTitle("Remove from Favorites");
            } else {
                menu.findItem(R.id.menu_add_to_fave).setIcon(R.drawable.ic_action_toggle_star_outline);
                menu.findItem(R.id.menu_add_to_fave).setTitle("Add to Favorites");
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_achievements, menu);

        menu.findItem(R.id.menu_game_details).setIcon(R.drawable.ic_action_about);

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

            case R.id.menu_screenshots:
                Intent intent = new Intent(Achievements.this, Screenshots.class);
                intent.putExtra("gbGameId", mGameDetails.getGbGameId());
                intent.putExtra("gameTitle", mGameDetails.getTitle());
                startActivity(intent);

                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);

                break;

            case R.id.menu_add_to_fave:
                addToFavorites();
                break;

            case R.id.menu_upload_game_details:
                saveGameDetails();
                break;

            case R.id.menu_upload_achievements:
                saveAchievements();
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

    private final class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.menu_achievements_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            // Respond to clicks on the actions in the CAB
            switch (menuItem.getItemId()) {
                // remove items
                case R.id.menu_mark_completed:
                    // remove items
                    for (int i = mAdapter.getCount(); i >= 0; i--) {
                        if (mLvContent.getCheckedItemPositions().get(i)) {
                            mList.remove(i);

                            final ParseRelation<ParseObject> relation = UserProfile.getCurrentUser().getRelation("achsCompleted");

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Achievements");
                            query.getInBackground(mList.get(i).getParseId(), new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    relation.add(parseObject);
                                    UserProfile.getCurrentUser().saveInBackground();
                                }
                            });
                        }
                    }

                    // update list
                    mAdapter.notifyDataSetChanged();

                    // Action picked, so close the CAB
                    actionMode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            // Here you can make any necessary updates to the activity when
            // the CAB is removed. By default, selected items are deselected/unchecked.
            for (int i = 0; i < mAdapter.getCount(); i++) {
                mLvContent.setItemChecked(i, false);
            }

            // set ActionMode to null
            if (actionMode == mActionMode) {
                mActionMode = null;
            }

            // set choice mode to single
            mLvContent.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }
}
