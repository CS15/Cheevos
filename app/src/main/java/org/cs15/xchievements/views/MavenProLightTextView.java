package org.cs15.xchievements.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Class that extends TextView, to change font to Roboto - Light
 */
public class MavenProLightTextView extends TextView {

    public MavenProLightTextView(Context mContext, AttributeSet mAttrs) {
        super(mContext, mAttrs);

        if (!isInEditMode()) {
            // set text view font
            Typeface mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/MavenProLight-200.otf");
            this.setTypeface(mFont);
        }
    }

    public MavenProLightTextView(Context mContext) {
        super(mContext);

        if (!isInEditMode()) {
            // set text view font
            Typeface mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/MavenProLight-200.otf");
            this.setTypeface(mFont);
        }
    }
}