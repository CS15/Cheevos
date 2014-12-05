package org.relos.cheevos.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.relos.cheevos.objects.Game;

import java.util.List;

public class AchievementsAdapter extends BaseAdapter {
    private List<Game> mList;
    private Context mContext;

    public AchievementsAdapter(List<Game> data, Context context) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    class ViewHolder {

    }
}
