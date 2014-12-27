package org.cs15.xchievements.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.Singleton;
import org.cs15.xchievements.misc.XchievementsImageLoader;
import org.cs15.xchievements.objects.Achievement;

import java.io.IOException;
import java.util.List;

public class AchievementsAdapter extends BaseAdapter {
    private List<Achievement> mList;
    private Context mContext;

    public AchievementsAdapter(List<Achievement> data, Context context) {
        mList = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        // variables
        final ViewHolder mViewHolder;

        // check if view is null
        if (view == null) {
            // inflate layout
            LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.row_achievements, null);

            // view holder
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mIvCover = (XchievementsImageLoader) view.findViewById(R.id.iv_ach_cover);
            mViewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_ach_title);
            mViewHolder.mTvDesc = (TextView) view.findViewById(R.id.tv_ach_subtitle);
            mViewHolder.mTvCommentsCount = (TextView) view.findViewById(R.id.tv_comment_counts);
            mViewHolder.mTvGamerscore = (TextView) view.findViewById(R.id.tv_gamerscore);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.mIvCover.setImageUrl(mList.get(position).getCoverUrl(), Singleton.getImageLoader(), mList.get(position).isCompleted());
        mViewHolder.mTvTitle.setText(mList.get(position).getTitle());
        mViewHolder.mTvDesc.setText(mList.get(position).getDescription());
        mViewHolder.mTvCommentsCount.setText(String.format("(%s)", mList.get(position).getCommentsCount()));
        mViewHolder.mTvGamerscore.setText(String.format("%s", mList.get(position).getGamerscore()));

        // return view
        return view;
    }

    class ViewHolder {
        XchievementsImageLoader mIvCover;
        TextView mTvTitle;
        TextView mTvDesc;
        TextView mTvCommentsCount;
        TextView mTvGamerscore;
    }
}
