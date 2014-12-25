package org.cs15.xchievements.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.cs15.xchievements.R;
import org.cs15.xchievements.Repository.Database;
import org.cs15.xchievements.misc.HelperClass;

public class AddComment extends ActionBarActivity {
    private String mParseAchId;
    private String mParseNewsFeedId;
    private EditText mEtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mParseAchId = getIntent().getExtras().getString("parseAchId");
            mParseNewsFeedId = getIntent().getExtras().getString("parseNewsFeedId");

            final Button btSubmit = (Button) findViewById(R.id.bt_submit);
            mEtComment = (EditText) findViewById(R.id.et_comment);

            btSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mEtComment.getText().toString().equals("")) {
                        btSubmit.setEnabled(false);
                        btSubmit.setClickable(false);

                        addCommentToParse();
                    }
                }
            });
        }
    }

    private void addCommentToParse() {
        new Database().addComment(mParseAchId, mParseNewsFeedId, mEtComment.getText().toString(), new Database.IAddComment() {
            @Override
            public void onSuccess(String message) {
                HelperClass.toast(AddComment.this, message);
                onBackPressed();
            }

            @Override
            public void onError(String error) {
                HelperClass.toast(AddComment.this, error);
            }
        });
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
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_null, R.anim.anim_slide_out_right);
    }
}
