package org.cs15.xchievements.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.LatestAchievementsAdapter;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends Fragment {
    private ListView mLvContent;
    private LatestAchievementsAdapter mAdapter;
    private ArrayList<GameDetails> mList;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_favorites, container, false);

        mList = new ArrayList<>();
        mAdapter = new LatestAchievementsAdapter(getActivity(), mList);
        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLvContent.setAdapter(mAdapter);
        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Intent intent = new Intent(getActivity(), Achievements.class);
                intent.putExtra("title", mList.get(i).getTitle());
                intent.putExtra("url", mList.get(i).getAchievementsPageUrl());
                intent.putExtra("coverUrl", mList.get(i).getCoverUrl());
                intent.putExtra("achsAmount", mList.get(i).getAchievementsAmount());
                intent.putExtra("gamerscore", mList.get(i).getGamerscore());
                intent.putExtra("gameId", mList.get(i).getId());
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
            }
        });

        getFavorites();

        return view;
    }

    private void getFavorites() {
        // get data from database
        ParseQuery<ParseObject> parseObject = UserProfile.getCurrentUser().getRelation("favorites").getQuery();
        parseObject.orderByAscending("title");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    mList.clear();

                    for (ParseObject g : data) {
                        GameDetails game = new GameDetails();
                        game.setId(g.getInt("gameId"));
                        game.setCoverUrl(g.getString("coverUrl"));
                        game.setTitle(g.getString("title"));
                        game.setAchievementsAmount(g.getInt("achsAmount"));
                        mList.add(game);
                    }

                    mAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(), "Error loading games: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getFavorites();
    }
}
