package org.cs15.xchievements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cs15.xchievements.R;

public class AlphabetAdapter extends BaseAdapter {
    // instance variables
    private Context mContext;
    private String[] mTitles;

    public AlphabetAdapter(Context mContext, String[] mTitles) {
        // extract parameters
        this.mContext = mContext;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return this.mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return this.mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // get view
        ViewHolder mViewHolder;

        // check if view needs to get inflated
        if (view == null) {
            // inflate view
            LayoutInflater viewInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = viewInflater.inflate(R.layout.row_alphabet, null);

            // instantiate views
            mViewHolder = new ViewHolder();
            assert view != null;
            mViewHolder.mTvTitles = (TextView) view.findViewById(R.id.tv_navi_alphabet_title);

            // save view holder in tag
            view.setTag(mViewHolder);
        } else {
            // get view holder from tag
            mViewHolder = (ViewHolder) view.getTag();
        }

        // set data
        mViewHolder.mTvTitles.setText(this.mTitles[position]);

        // return view
        return view;
    }

    class ViewHolder {
        // instantiate views
        TextView mTvTitles;
    }
}