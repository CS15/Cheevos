package org.relos.cheevos.misc;

import android.app.Application;

import com.parse.Parse;

import org.relos.cheevos.R;

/**
 * Instantiate all singleton classes
 *
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class Singleton extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // instantiate singleton volley
        SingletonVolley.instantiate(this);

        // instantiate parse sdk
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_secret_id));

    }
}
