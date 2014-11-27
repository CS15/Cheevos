package org.relos.cheevos.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

import org.relos.cheevos.misc.HelperClass;

/**
 * Circle NetworkImageView.
 * <p/>
 * Created by Christian Soler on 7/13/14.
 */
public class RoundedImageLoader extends NetworkImageView {

    public RoundedImageLoader(Context context) {
        super(context);
    }

    public RoundedImageLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageLoader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            super.setImageBitmap(HelperClass.getCircleImage(bitmap, getResources().getColor(android.R.color.white), 5));
        } else {
            super.setImageBitmap(bitmap);
        }
    }
}
