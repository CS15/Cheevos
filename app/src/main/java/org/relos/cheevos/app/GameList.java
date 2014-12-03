package org.relos.cheevos.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.relos.cheevos.R;
import org.relos.cheevos.adapters.AlphabetAdapter;
import org.relos.cheevos.loaders.GameListLoader;
import org.relos.cheevos.objects.Game;

import java.util.ArrayList;
import java.util.List;

public class GameList extends Fragment implements LoaderManager.LoaderCallbacks<List<Game>> {
    // field
    private List<Game> mList;
    private ListView mAlphabetMenu;
    private SlidingPaneLayout mSlidingPane;
    private String url = "http://www.xboxachievements.com/games/xbox-one/";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_game_list, container, false);

        // browsing menu titles
        final String[] mAlphabetTitles = getResources().getStringArray(R.array.slide_menu_alphabet);

        onBrowsingMenu(view);

        mAlphabetMenu = (ListView) view.findViewById(R.id.lv_alphabet_content);
        mAlphabetMenu.setAdapter(new AlphabetAdapter(getActivity(), mAlphabetTitles));

        mList = new ArrayList<Game>();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        boolean isAnAdmin = true;

        if (isAnAdmin) {
            this.getLoaderManager().initLoader(0, null, this);
        }
    }

    private void onBrowsingMenu(View view) {
        // instantiate browsing menu
        mSlidingPane = new SlidingPaneLayout(getActivity());
        mSlidingPane = (SlidingPaneLayout) view.findViewById(R.id.slp_game_list);
        mSlidingPane.setParallaxDistance(30);
        mSlidingPane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View view, float v) {
            }

            @Override
            public void onPanelOpened(View view) {
                // set actionbar title
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(R.string.ab_title_game_list);
            }

            @Override
            public void onPanelClosed(View view) {
                // set actionbar title
                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("");
            }
        });
    }

    @Override
    public Loader<List<Game>> onCreateLoader(int i, Bundle bundle) {
        return new GameListLoader(getActivity(), mList, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Game>> listLoader, List<Game> games) {
        mList = games;
    }

    @Override
    public void onLoaderReset(Loader<List<Game>> listLoader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate options menu
        inflater.inflate(R.menu.game_list, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // enable/disable action bar menus
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case R.id.menu_browse:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
