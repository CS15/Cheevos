package org.cs15.cheevos.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;

import org.cs15.cheevos.R;
import org.cs15.cheevos.misc.HelperClass;

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
            super.setImageBitmap(HelperClass.getCircleImage(bitmap, getResources().getColor(R.color.primary_color), 5));
        } else {
            super.setImageBitmap(bitmap);
        }
    }
}
