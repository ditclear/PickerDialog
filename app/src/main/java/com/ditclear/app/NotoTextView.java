package com.ditclear.app;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 页面描述：
 * <p>
 * Created by ditclear on 2016/11/19.
 */

public class NotoTextView extends TextView {
    public NotoTextView(Context context) {
        super(context);
        init(context);
    }

    public NotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "NotoSansUI-Regular.ttf");
        setTypeface(tf);
    }
}
