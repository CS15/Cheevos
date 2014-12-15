package org.cs15.xchievements.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.cs15.xchievements.R;

public class AddAchComment extends ActionBarActivity {
    private String mParseAchId;
    private EditText mEtComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ach_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().getThemedContext();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            mParseAchId = getIntent().getExtras().getString("parseAchId");

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


        ParseObject user = ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId());
        ParseObject ach = ParseObject.createWithoutData("Achievements", mParseAchId);

        ParseObject obj = new ParseObject("Comments");
        obj.put("user", user);
        obj.put("achievement", ach);
        obj.put("achievementId", mParseAchId);
        obj.put("comment", mEtComment.getText().toString());
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    ParseUser.getCurrentUser().increment("postCount");
                    ParseUser.getCurrentUser().saveInBackground();

                    // update counter
                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Achievements");
                    parseQuery.getInBackground(mParseAchId, new GetCallback<ParseObject>() {
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                parseObject.increment("commentCounts");

                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(AddAchComment.this, "Successfully added comment", Toast.LENGTH_LONG).show();

                                        onBackPressed();
                                    }
                                });
                            }
                        }
                    });
                }
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
