package org.relos.cheevos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.relos.cheevos.R;
import org.relos.cheevos.misc.SingletonVolley;

import java.util.ArrayList;
import java.util.List;

public class LatestAchievementsAdapter extends BaseAdapter {
    // instance variables
    private Context mContext;
    private ArrayList<JSONObject> mData;

    public LatestAchievementsAdapter(Context context, ArrayList<JSONObject> data) {
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
            mViewHolder.mIvCover = (NetworkImageView) view.findViewById(R.id.iv_cover);
            mViewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_game_title);
            mViewHolder.mTvAchAmount = (TextView) view.findViewById(R.id.tv_game_ach_amount);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        try {
            mViewHolder.mIvCover.setImageUrl(mData.get(position).getString("cover"), SingletonVolley.getImageLoader());
            mViewHolder.mTvTitle.setText(mData.get(position).getString("title"));
            mViewHolder.mTvAchAmount.setText(mData.get(position).getString("achsAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
