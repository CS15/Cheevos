package org.cs15.xchievements.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.adapters.FavoritesAdapter;
import org.cs15.xchievements.adapters.LatestAchievementsAdapter;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;

public class Favorites extends Fragment {
    private ListView mLvContent;
    private FavoritesAdapter mAdapter;
    private ArrayList<GameDetails> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_favorites, container, false);

        mList = new ArrayList<>();
        mAdapter = new FavoritesAdapter(getActivity(), mList);
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

        new Database().getFavorites(mList, new Database.IFavorites() {
            @Override
            public void onSuccess(ArrayList<GameDetails> data) {
                mList = data;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(getActivity(), error);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
