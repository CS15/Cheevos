package org.relos.cheevos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.parse.ParseObject;

import org.relos.cheevos.R;
import org.relos.cheevos.misc.SingletonVolley;

import java.util.List;

public class LatestAchievementsAdapater extends BaseAdapter {
    // instance variables
    private Context mContext;
    private List<ParseObject> mData;

    public LatestAchievementsAdapater(Context context, List<ParseObject> data) {
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
            mViewHolder.mIvCover = (NetworkImageView) view.findViewById(R.id.iv_new_ach_image);
            mViewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_new_ach_title);
            mViewHolder.mTvAchAmount = (TextView) view.findViewById(R.id.tv_new_ach_ach_amount);
            mViewHolder.mTvAuthor = (TextView) view.findViewById(R.id.tv_new_ach_author);
            mViewHolder.mTvCommentAmount = (TextView) view.findViewById(R.id.tv_new_ach_comments);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        // set cover image
        mViewHolder.mIvCover.setImageUrl(mData.get(position).getString("CoverImage"), SingletonVolley.getImageLoader());

        // set titles
        mViewHolder.mTvTitle.setText(mData.get(position).getString("Title"));

        // return view
        return view;
    }

    class ViewHolder {
        // instance views
        NetworkImageView mIvCover;
        TextView mTvTitle;
        TextView mTvAchAmount;
        TextView mTvAuthor;
        TextView mTvCommentAmount;
    }
}
