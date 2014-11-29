package org.relos.cheevos.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.relos.cheevos.R;
import org.relos.cheevos.misc.SingletonVolley;
import org.relos.cheevos.views.RoundedImageLoader;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * User profile fragment
 * <p/>
 * Created by Christian (ReloS) Soler on 11/28/2014.
 */
public class Profile extends Fragment {
    // instance
    private NetworkImageView mIvProfile;
    private TextView mTvGamerTag;
    private TextView mTvGamerScore;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);

        mIvProfile = (NetworkImageView) view.findViewById(R.id.iv_profile);
        mTvGamerTag = (TextView) view.findViewById(R.id.tv_profile_gamertag);
        mTvGamerScore = (TextView) view.findViewById(R.id.tv_profile_gamerscore);

        getUserGamertagInfo();

        return view;
    }

    private void getUserGamertagInfo() {
        // set title
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ParseUser.getCurrentUser().getString("gamertag"));

        String url = "https://xboxapi.com/v2/" + ParseUser.getCurrentUser().getString("xboxId") + "/profile";

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    mIvProfile.setImageUrl(response.getString("GameDisplayPicRaw"), SingletonVolley.getImageLoader());
                    mTvGamerTag.setText(response.getString("Gamertag"));
                    mTvGamerScore.setText(response.getString("Gamerscore") + " G");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-AUTH", "c298a7edee735d5559a219b0020a60fb9bb657dc");
                return params;
            }
        };

        queue.add(request);
    }
}
