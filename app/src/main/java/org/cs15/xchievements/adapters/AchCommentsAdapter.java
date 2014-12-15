package org.cs15.xchievements.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.HelperClass;
import org.cs15.xchievements.misc.SingletonVolley;
import org.cs15.xchievements.views.RoundedImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        mViewHolder.mIvUserAvatar.setImageUrl(mData.get(position).getParseObject("user").getString("gamerpic"), SingletonVolley.getImageLoader());
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
