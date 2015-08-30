package io.ololo.stip;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by ko3a4ok on 8/31/15.
 */
public class SearchView extends android.support.v7.widget.SearchView {
    public SearchView(Context context) {
        super(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            super.onLayout(changed, l, t, r, b);
        } catch (Exception ex) {}
    }
}
