package org.cs15.xchievements.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseUser;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.NavigationDrawerAdapter;
import org.cs15.xchievements.misc.UserProfile;

/**
 * Drawer navigation
 * <p/>
 * Created by Christian Soler on 11/28/14.
 */
public class BaseActivity extends ActionBarActivity {
    // instances
    private final String PREF_USER_LEARNED_DRAWER = "User_Learned_Drawer";
    private final String STATE_SELECTED_POSITION = "Current_position";
    private String[] mDrawerTitles;
    private boolean mUserLearnedDrawer;
    private boolean displayMenu;
    private int mCurrentSelectedPosition = 1;
    private ActionBarDrawerToggle mDrawerToggle;
    private Fragment mFrag;
    private DrawerLayout mDrawerLayout;
    private ListView mLvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = prefs.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawer = (ListView) findViewById(R.id.navigation_drawer);

        mLvDrawer.setAdapter(new NavigationDrawerAdapter(this, mDrawerTitles));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(mCurrentSelectedPosition);
        }

        mLvDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        showOrHideMenu(true);
    }

    private void selectItem(int position) {
        // switch fragment with animation
        FragmentTransaction fragTrans = this.getSupportFragmentManager().beginTransaction();

        // navigation menu item click
        switch (position) {
            case 0:
                if (UserProfile.getCurrentUser() != null) {
                    mFrag = new Favorites();
                    fragTrans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragTrans.replace(R.id.container, mFrag);
                    fragTrans.commit();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.not_logged_in);
                    builder.setIcon(R.drawable.ic_app_logo);
                    builder.setTitle(R.string.not_logged_in_title);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

                    builder.create();
                    builder.show();
                }

                break;
            case 1:
                mFrag = new LatestAchievements();
                fragTrans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragTrans.replace(R.id.container, mFrag);
                fragTrans.commit();
                break;
            case 2:
                Intent intent = new Intent(BaseActivity.this, GameList.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
                break;
            case 3:
                mFrag = new Settings();
                fragTrans.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragTrans.replace(R.id.container, mFrag);
                fragTrans.commit();
                break;
        }

        // update selected item
        mLvDrawer.setItemChecked(position, true);

        // Set action bar title
        getSupportActionBar().setTitle(mDrawerTitles[position]);

        // close navigation menu
        closeDrawer();

        showOrHideMenu(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_login).setVisible(displayMenu && ParseUser.getCurrentUser() == null);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment frag = null;

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_login:
                frag = new Login();
                showOrHideMenu(false);
                break;
        }

        // switch fragment with animation
        FragmentTransaction fragTrans = this.getSupportFragmentManager().beginTransaction();
        fragTrans.setCustomAnimations(R.anim.anim_slide_in_bottom, R.anim.anim_null, R.anim.anim_null, R.anim.anim_slide_out_bottom);
        fragTrans.addToBackStack(null);
        fragTrans.replace(R.id.container, frag);
        fragTrans.commit();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START | Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        showOrHideMenu(true);

        super.onBackPressed();
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    private void showOrHideMenu(boolean display) {
        displayMenu = display;
        supportInvalidateOptionsMenu();
    }
}
