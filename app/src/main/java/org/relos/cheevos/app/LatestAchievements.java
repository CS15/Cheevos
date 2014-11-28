package org.relos.cheevos.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.relos.cheevos.R;

/**
 * Created by Christian Soler on 9/22/2014.
 */
public class LatestAchievements extends Fragment {
    // instance variables
    private ListView mLvContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment layout
        View view = inflater.inflate(R.layout.frag_latest_achievements, container, false);

        // return view
        return view;
    }

}
