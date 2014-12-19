package org.cs15.xchievements.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.cs15.xchievements.R;
import org.cs15.xchievements.adapters.ScreenshotsAdapter;
import org.cs15.xchievements.misc.Singleton;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Screenshots extends ActionBarActivity {
    private String mGameId;
    private ListView mLvContent;
    private ScreenshotsAdapter mAdapter;
    private List<String> mList;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshots);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mGameId = getIntent().getExtras().getString("gbGameId");
            mTitle = getIntent().getExtras().getString("gameTitle");

            getSupportActionBar().setTitle(mTitle);

            mList = new ArrayList<String>();

            mAdapter = new ScreenshotsAdapter(mList, this);

            mLvContent = (ListView) findViewById(R.id.lv_content);
            mLvContent.setAdapter(mAdapter);
            mLvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mList.get(position)));
                    startActivity(intent);
                }
            });

            getData();
        }
    }

    private void getData() {

        String url = "http://www.giantbomb.com/api/game/3030-" + mGameId + "/?api_key=" + getResources().getString(R.string.gb_api) + "&format=json&field_list=images";

        RequestQueue queue = Singleton.getRequestQueque();

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        try {
                            int size = data.getJSONObject("results").getJSONArray("images").length();

                            for (int i = 0; i < size; i++) {
                                mList.add(data.getJSONObject("results").getJSONArray("images").getJSONObject(i).getString("super_url"));
                            }

                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(Screenshots.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(Screenshots.this, "Error: " + volleyError, Toast.LENGTH_LONG).show();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // option item click listener
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        // show/close browse menu
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
    }
}
