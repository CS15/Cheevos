package org.cs15.xchievements.misc;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.cs15.xchievements.R;
import org.cs15.xchievements.app.MainActivity;

/**
 * Instantiate all singleton classes
 * <p/>
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // instantiate singleton volley
        Singleton.instantiate(this);

        // instantiate parse sdk
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_secret_id));

        ParsePush.subscribeInBackground("broadcast", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}
