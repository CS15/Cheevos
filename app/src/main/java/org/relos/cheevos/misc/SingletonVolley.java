package org.relos.cheevos.misc;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Singleton Volley
 *
 * Created by Christian (ReloS) Soler on 11/26/2014.
 */
public class SingletonVolley {
    // instances
    private static RequestQueue mRequestQueque;
    private static ImageLoader mImageLoader;
    private static BitmapLruCache mBitmapLruCache;

    private SingletonVolley(){}

    static void instantiate(Context context){
        mBitmapLruCache = new BitmapLruCache();
        mRequestQueque = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueque, mBitmapLruCache);
    }

    public static ImageLoader getImageLoader(){
        if (mImageLoader != null) return mImageLoader;

        throw new IllegalStateException("ImageLoader not instantiated");
    }

    public static RequestQueue getRequestQueque(){
        if (mRequestQueque != null) return mRequestQueque;

        throw new IllegalStateException("RequestQueue not instantiated");
    }

    public static BitmapLruCache getBitmapLruCache(){
        if (mBitmapLruCache != null) return mBitmapLruCache;

        throw new IllegalStateException("BitmapLruCache not instantiated");
    }
}
