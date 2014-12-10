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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.AchievementsAdapter;
import org.cs15.xchievements.loaders.AchievementsLoader;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.Achievement;
import org.cs15.xchievements.objects.Game;
import org.cs15.xchievements.objects.GameDetails;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private boolean isAnAdmin = UserProfile.isAnAdmin();

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
            mGameDetails.setTitle(mTitle);
            mGameDetails.setCoverUrl(mCoverUrl);
            mGameDetails.setGamerscore(mGamerscore);
            mGameDetails.setId(mGameId);
            mGameDetails.setAchievementsAmount(mAchsAmount);
            mGameDetails.setAchievementsPageUrl(mUrl);

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

            getData();
        }
    }

    private void getData() {
        if (isAnAdmin) {
            if (this.getSupportLoaderManager().getLoader(0) == null) {
                this.getSupportLoaderManager().initLoader(0, null, this);
            } else {
                this.getSupportLoaderManager().restartLoader(0, null, this);
            }
        } else {
            getFromParse();
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
        TextView tvSummary = (TextView) findViewById(R.id.tv_summary);

        // data
        String title = gameDetails.getTitle();
        String developer = (gameDetails.getDevelopers() != null) ? gameDetails.getDevelopers() : "N/A";
        String publisher = (gameDetails.getPublishers() != null) ? gameDetails.getPublishers() : "N/A";
        String genres = gameDetails.getGenre();
        String usa = gameDetails.getUsaRelease();
        String eu = gameDetails.getEuRelease();
        String japan = gameDetails.getJapanRelease();
        String summary = (gameDetails.getSummary() != null) ? gameDetails.getSummary() : "N/A";

        // set data
        ivCoverImage.setImageUrl(gameDetails.getCoverUrl(), SingletonVolley.getImageLoader());

        tvGameTitle.setText(String.format("%s", title));
        tvDeveloper.setText(String.format("Developer: %s", developer));
        tvPublisher.setText(String.format("Publisher: %s", publisher));
        tvGenre.setText(String.format("Genre: %s", genres));
        tvUsDate.setText(String.format("USA: %s", usa));
        tvEuDate.setText(String.format("Europe: %s", eu));
        tvJpDate.setText(String.format("Japan: %s", japan));
        tvSummary.setText(String.format("%s", summary));
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

            case R.id.menu_screenshots:
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

    private void getFromParse() {
        ParseQuery<ParseObject> gameDetails = ParseQuery.getQuery("Games");
        gameDetails.whereEqualTo("gameId", mGameDetails.getId());
        gameDetails.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() > 0) {
                        // set data data
                        for (ParseObject game : data) {
                            mGameDetails.setId(game.getInt("gameId"));
                            mGameDetails.setGbGameId(game.getString("gbGameId"));
                            mGameDetails.setParseId(game.getObjectId());
                            mGameDetails.setTitle(game.getString("title"));
                            mGameDetails.setCoverUrl(game.getString("coverUrl"));
                            mGameDetails.setAchievementsAmount(game.getInt("achsAmount"));
                            mGameDetails.setGamerscore(game.getInt("gamerscore"));
                            mGameDetails.setDevelopers(game.getString("developer"));
                            mGameDetails.setPublishers(game.getString("publisher"));
                            mGameDetails.setGenre(game.getString("genre"));
                            mGameDetails.setUsaRelease(game.getString("usaRelease"));
                            mGameDetails.setEuRelease(game.getString("euRelease"));
                            mGameDetails.setJapanRelease(game.getString("japanRelease"));
                        }

                        displayGameDetails(mGameDetails);
                        getGameSummary(mGameDetails.getGbGameId());

                    } else {
                        Toast.makeText(Achievements.this, "Game details do not exist.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Achievements.this, "Error getting the object: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        ParseQuery<ParseObject> achievements = ParseQuery.getQuery("Achievements");
        achievements.whereEqualTo("gameId", mGameDetails.getId());
        achievements.orderByAscending("title");
        achievements.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() > 0) {
                        // set data data
                        for (ParseObject achievement : data) {
                            Achievement ach = new Achievement();
                            ach.setGameId(achievement.getInt("gameId"));
                            ach.setCoverUrl(achievement.getString("coverUrl"));
                            ach.setTitle(achievement.getString("title"));
                            ach.setDescription(achievement.getString("description"));
                            ach.setGamerscore(achievement.getInt("gamerscore"));
                            mList.add(ach);
                        }

                        mAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(Achievements.this, "No achievements found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Achievements.this, "Error getting the object: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveGameDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.whereEqualTo("gameId", mGameDetails.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() == 0) {
                        // upload game data
                        ParseObject game = new ParseObject("Games");
                        game.put("gameId", mGameDetails.getId());
                        game.put("title", mGameDetails.getTitle());
                        game.put("coverUrl", mGameDetails.getCoverUrl());
                        game.put("achsAmount", mGameDetails.getAchievementsAmount());
                        game.put("gamerscore", mGameDetails.getGamerscore());
                        game.put("achsUrl", mGameDetails.getAchievementsPageUrl());
                        game.put("developer", mGameDetails.getDevelopers());
                        game.put("publisher", mGameDetails.getPublishers());
                        game.put("genre", mGameDetails.getGenre());
                        game.put("usaRelease", mGameDetails.getUsaRelease());
                        game.put("euRelease", mGameDetails.getEuRelease());
                        game.put("japanRelease", mGameDetails.getJapanRelease());
                        game.put("summary", mGameDetails.getSummary());
                        game.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(Achievements.this, "Successfully uploaded to parse.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Achievements.this, "Error saving the object: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(Achievements.this, "Game already exist.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Achievements.this, "Error getting the object: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveAchievements() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Achievements");
        query.whereEqualTo("gameId", mGameDetails.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() == 0) {
                        // upload game data
                        for (Achievement ach : mList) {
                            ParseObject achievement = new ParseObject("Achievements");
                            achievement.put("gameTitle", mGameDetails.getTitle());
                            achievement.put("gameId", ach.getGameId());
                            achievement.put("title", ach.getTitle());
                            achievement.put("coverUrl", ach.getCoverUrl());
                            achievement.put("description", ach.getDescription());
                            achievement.put("gamerscore", ach.getGamerscore());
                            achievement.saveInBackground();
                        }
                    } else if (mList.size() > data.size()) {
                        Toast.makeText(Achievements.this, "Need to update achievements", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Achievements.this, "Achievements up to date", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(Achievements.this, "Error getting the object: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getGameSummary(String gbGameId) {

        String url = "http://www.giantbomb.com/api/game/3030-" + gbGameId + "/?api_key=" + getResources().getString(R.string.gb_api) + "&format=json&field_list=id,name,deck";

        RequestQueue queue = SingletonVolley.getRequestQueque();

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject> (){
                    @Override
                    public void onResponse(JSONObject data) {
                        try {
                            mGameDetails.setSummary(data.getJSONObject("results").getString("deck"));
                            displayGameDetails(mGameDetails);
                        } catch (JSONException e) {
                            Toast.makeText(Achievements.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Achievements.this, "Error: " + volleyError, Toast.LENGTH_LONG).show();
            }
        });

        queue.add(request);
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
