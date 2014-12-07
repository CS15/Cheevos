package org.cs15.cheevos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cs15.cheevos.R;


/**
 * Navigation drawer adapter.
 */
public class NavigationDrawerAdapter extends BaseAdapter {
    // instance variables
    private Context mContext;
    private String[] mTitles;

    /**
     * Constructor.
     *
     * @param context Activity context.
     * @param titles  Array on titles.
     */
    public NavigationDrawerAdapter(Context context, String[] titles) {
        this.mContext = context;
        this.mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // variables
        ViewHolder viewHolder;

        // Check if view is null
        if (convertView == null) {
            // inflate view
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_navigation_drawer, null);

            // instantiate views
            viewHolder = new ViewHolder();
            assert convertView != null;
            viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_drawer_title);

            // save view holder in tag
            convertView.setTag(viewHolder);
        } else {
            // get view from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // set titles
        viewHolder.mTvTitle.setText(mTitles[position].toLowerCase());

        // return view
        return convertView;
    }

    class ViewHolder {
        TextView mTvTitle;
    }
}
