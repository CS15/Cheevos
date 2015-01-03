package org.cs15.xchievements.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;

import org.cs15.xchievements.R;
import org.cs15.xchievements.repository.Database;
import org.cs15.xchievements.adapters.NewsFeedAdapter;
import org.cs15.xchievements.misc.HelperClass;

import java.util.List;

public class NewsFeed extends Fragment {
    private ListView mLvContent;
    private NewsFeedAdapter mAdapter;
    private List<ParseObject> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_news_feed, container, false);

        mLvContent = (ListView) view.findViewById(R.id.lv_content);
        mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(getActivity(), Comments.class);
                intent.putExtra("coverUrl", mList.get(i).getString("imageUrl"));
                intent.putExtra("title", mList.get(i).getString("title"));
                intent.putExtra("subtitle", mList.get(i).getString("subtitle"));
                intent.putExtra("gameTitle", mList.get(i).getParseObject("game").getString("title"));
                intent.putExtra("newsFeedId", mList.get(i).getObjectId());
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_null);
            }
        });

        getData();

        return view;
    }

    private void getData() {
        new Database().getNewsFeed(new Database.INewsFeeds() {
            @Override
            public void onSuccess(List<ParseObject> data) {
                mList = data;

                mAdapter = new NewsFeedAdapter(mList, getActivity());

                mLvContent.setAdapter(mAdapter);
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(getActivity(), error);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
}
