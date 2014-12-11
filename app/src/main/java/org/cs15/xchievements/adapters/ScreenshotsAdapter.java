package org.cs15.xchievements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.NetworkImageView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.SingletonVolley;

import java.util.List;


public class ScreenshotsAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;

    public ScreenshotsAdapter(List<String> list, Context context) {
        mContext = context;
        mList = list;
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
            view = viewInflater.inflate(R.layout.row_screenshots, null);

            // view holder
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mIvCover = (NetworkImageView) view.findViewById(R.id.iv_screenshot);
            // set tag
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        mViewHolder.mIvCover.setImageUrl(mList.get(position), SingletonVolley.getImageLoader());

        // return view
        return view;
    }

    class ViewHolder {
        NetworkImageView mIvCover;
    }
}
