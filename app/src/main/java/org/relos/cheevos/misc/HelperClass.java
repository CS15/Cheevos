package org.relos.cheevos.misc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.ViewConfiguration;

import org.relos.cheevos.views.ActionBarFont;

import java.lang.reflect.Field;

/**
 * Created by Christian Soler on 9/22/2014.
 *
 * Helper Class
 */
public class HelperClass {

    /**
     * Force overflow menu in samsung devices
     *
     * @param context The activity context.
     */
    public static void forceOverFlowMenu(Context context){
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyFields = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyFields != null) {
                menuKeyFields.setAccessible(true);
                menuKeyFields.setBoolean(config, false);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Return the base URL of XBA
     *
     * @return Returns Base url od XBA
     */
    public static String baseUrl() {
        return "https://www.xboxachievements.com/";
    }

    /**
     * Create circle image.
     *
     * @param bitmap Bitmap to be cropped
     * @param resColor Resource color
     * @param strokeWidth Thickness of stroke
     *
     * @return Returns the circle image with border
     */
    public static Bitmap getCircleImage(Bitmap bitmap, int resColor, int strokeWidth){
        // create Bitmap to draw
        Bitmap mBitmap = Bitmap.createBitmap(bitmap.getWidth() + 8, bitmap.getHeight() + 8, Bitmap.Config.ARGB_8888);

        // create Rect to hold image
        final Rect mRec = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // create Canvas
        Canvas mCanvas = new Canvas(mBitmap);
        mCanvas.drawARGB(0, 0, 0, 0);

        // create Paint
        final Paint mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        // get the half size of the image
        int mHalfWidth = bitmap.getWidth() / 2;
        int mHalfHeight = bitmap.getHeight() / 2;

        // draw circle
        mCanvas.drawCircle((mHalfWidth + 4), (mHalfHeight + 4), Math.min(mHalfWidth, mHalfHeight), mPaint);

        // unknown
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // draw the image
        mCanvas.drawBitmap(bitmap, mRec, mRec, mPaint);

        // set border mode
        mPaint.setXfermode(null);

        // set stroke
        mPaint.setStyle(Paint.Style.STROKE);

        // set stroke color
        mPaint.setColor(resColor);

        // set stroke width
        mPaint.setStrokeWidth(strokeWidth);

        // draw stroke
        mCanvas.drawCircle((mHalfWidth + 4), (mHalfHeight + 4), Math.min(mHalfWidth, mHalfHeight), mPaint);

        // return the circle image
        return mBitmap;
    }

    /**
     * Set action bar background
     *
     * @param activity The activity to set ActionBar background to.
     * @param resColorId The Drawable color to set the ActionBar to.
     *
     */
    public static void setActionBarBackground(Activity activity, int resColorId){
        // change action bar color
        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(activity.getResources().getInteger(resColorId)));
        activity.getActionBar().setDisplayShowTitleEnabled(false);
        activity.getActionBar().setDisplayShowTitleEnabled(true);
    }

    /**
     * Set action bar to custom font
     *
     * @param activity The activity to set ActionBar title to.
     * @param title The title to display.
     * @param fontName The name of the font with extension file.
     * font must be in the assets/fonts directory
     */
    public static SpannableString setActionbarTitle(Activity activity, String title, String fontName) {
        // create custom font for action bar
        SpannableString customActionBarFont = new SpannableString(title);
        customActionBarFont.setSpan(new ActionBarFont(activity, fontName), 0, customActionBarFont.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return customActionBarFont;
    }

    /**
     * Reload activity
     *
     * @param activity Activity to be reloaded
     */
    public static void reloadActivity(Activity activity) {
        // reload activity
        activity.finish();
        activity.startActivity(activity.getIntent());
    }

}
