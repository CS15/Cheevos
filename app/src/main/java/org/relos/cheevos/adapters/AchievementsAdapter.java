package org.relos.cheevos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.relos.cheevos.R;
import org.relos.cheevos.misc.SingletonVolley;
import org.relos.cheevos.objects.Achievement;

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
    public View getView(int position, View view, ViewGroup parent) {
        // variables
        ViewHolder mViewHolder;

        // check if view is null
        if (view == null) {
            // inflate layout
            LayoutInflater viewInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.row_achievements, null);

            // view holder
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mIvCover = (NetworkImageView) view.findViewById(R.id.iv_ach_cover);
            mViewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_ach_title);
            mViewHolder.mTvDesc = (TextView) view.findViewById(R.id.tv_ach_subtitle);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.mIvCover.setImageUrl(mList.get(position).getCoverUrl(), SingletonVolley.getImageLoader());
        mViewHolder.mTvTitle.setText(mList.get(position).getTitle());
        mViewHolder.mTvDesc.setText(mList.get(position).getDescription());

        // return view
        return view;
    }

    class ViewHolder {
        NetworkImageView mIvCover;
        TextView mTvTitle;
        TextView mTvDesc;
    }
}
