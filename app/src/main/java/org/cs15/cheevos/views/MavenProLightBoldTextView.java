package org.cs15.cheevos.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Class that extends TextView, to change font to Roboto - Light
 */
public class MavenProLightBoldTextView extends TextView {

    public MavenProLightBoldTextView(Context mContext, AttributeSet mAttrs) {
        super(mContext, mAttrs);

        if (!isInEditMode()) {
            // set text view font
            Typeface mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/MavenProLight-300.otf");
            this.setTypeface(mFont);
        }
    }

    public MavenProLightBoldTextView(Context mContext) {
        super(mContext);

        if (!isInEditMode()) {
            // set text view font
            Typeface mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/MavenProLight-300.otf");
            this.setTypeface(mFont);
        }
    }
}