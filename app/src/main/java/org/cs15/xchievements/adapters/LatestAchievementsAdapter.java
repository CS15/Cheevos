package org.cs15.xchievements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.objects.GameDetails;

import java.util.ArrayList;

public class LatestAchievementsAdapter extends BaseAdapter {
    // instance variables
    private Context mContext;
    private ArrayList<GameDetails> mData;

    public LatestAchievementsAdapter(Context context, ArrayList<GameDetails> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // variables
        ViewHolder mViewHolder;

        // check if view is null
        if (view == null) {
            // inflate layout
            LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.row_latest_achievements, null);

            // view holder
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mIvCover = (NetworkImageView) view.findViewById(R.id.iv_ach_cover);
            mViewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_game_title);
            mViewHolder.mTvAchAmount = (TextView) view.findViewById(R.id.tv_game_ach_amount);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.mIvCover.setImageUrl(mData.get(position).getCoverUrl(), SingletonVolley.getImageLoader());
        mViewHolder.mTvTitle.setText(mData.get(position).getTitle());
        mViewHolder.mTvAchAmount.setText(mData.get(position).getAchievementsAmount());

        // return view
        return view;
    }

    class ViewHolder {
        // instance views
        NetworkImageView mIvCover;
        TextView mTvTitle;
        TextView mTvAchAmount;
    }
}
