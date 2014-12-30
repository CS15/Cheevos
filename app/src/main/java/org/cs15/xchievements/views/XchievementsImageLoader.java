package org.cs15.xchievements.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.HelperClass;

public class XchievementsImageLoader extends NetworkImageView {
    private boolean mIsGrayScale;

    public XchievementsImageLoader(Context context) {
        super(context);
    }

    public XchievementsImageLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XchievementsImageLoader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setIsGrayScale(boolean isGrayScale) {
        mIsGrayScale = isGrayScale;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {

            if (mIsGrayScale) {
                bitmap = HelperClass.toGrayScale(bitmap);
            }

            super.setImageBitmap(bitmap);

        } else {
            super.setImageBitmap(bitmap);
        }
    }
}
