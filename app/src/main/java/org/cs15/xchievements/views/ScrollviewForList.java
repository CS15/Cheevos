package org.cs15.xchievements.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Scroll viewer that works with a list view
 * <p/>
 * Created by Twenty on 7/13/2014.
 */
public class ScrollviewForList extends ScrollView {
    private float startY;

    public ScrollviewForList(Context context) {
        super(context);
    }

    public ScrollviewForList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollviewForList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            startY = ev.getY();
        }
        return (ev.getAction() == MotionEvent.ACTION_MOVE) && (Math.abs(startY - ev.getY()) > 50);
    }
}
