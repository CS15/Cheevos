package org.cs15.xchievements.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * List view that works with a scroll viewer
 * <p/>
 * Created by Christian Soler on 7/13/2014.
 */
public class ListViewScrollable extends ListView {
    public ListViewScrollable(Context context) {
        super(context);
    }

    public ListViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDividerHeight(1);
    }

    public ListViewScrollable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}
