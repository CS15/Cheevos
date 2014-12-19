package org.cs15.xchievements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.Singleton;
import org.cs15.xchievements.views.RoundedImageLoader;

import java.util.List;

public class AchCommentsAdapter extends BaseAdapter {
    // instance variables
    private Context mContext;
    private List<ParseObject> mData;

    public AchCommentsAdapter(Context context, List<ParseObject> data) {
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
            view = viewInflater.inflate(R.layout.row_ach_comments, null);

            // view holder
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mIvUserAvatar = (RoundedImageLoader) view.findViewById(R.id.iv_user_cover);
            mViewHolder.mTvUsername = (TextView) view.findViewById(R.id.tv_user_name);
            mViewHolder.mTvComment = (TextView) view.findViewById(R.id.tv_user_comment);

            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.mIvUserAvatar.setImageUrl(mData.get(position).getParseObject("user").getString("gamerpic"), Singleton.getImageLoader());
        mViewHolder.mTvUsername.setText(mData.get(position).getParseObject("user").getString("gamertag"));
        mViewHolder.mTvComment.setText(mData.get(position).getString("comment"));

        // return view
        return view;
    }

    class ViewHolder {
        // instance views
        RoundedImageLoader mIvUserAvatar;
        TextView mTvUsername;
        TextView mTvComment;
    }
}
