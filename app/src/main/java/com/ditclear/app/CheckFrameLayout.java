package com.ditclear.app;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * 页面描述：
 * <p>
 * Created by ditclear on 2016/11/19.
 */

public class CheckFrameLayout extends FrameLayout {
    CompoundButton cbx;

    public CheckFrameLayout(Context context) {
        super(context);
    }

    public CheckFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAddStatesFromChildren(true);
    }

    private CheckBox findCheckBox(View view) {
        //无递归广度优先遍历寻找CheckBox - -!我只是想重温一下C
        ArrayList<View> views = new ArrayList<>();
        views.add(view);
        while (!views.isEmpty()) {
            View c = views.remove(0);
            if (c instanceof CheckBox) {
                return (CheckBox) c;
            } else if (c instanceof ViewGroup) {
                ViewGroup fa = (ViewGroup) c;
                for (int i = 0; i < fa.getChildCount(); i++) {
                    views.add(fa.getChildAt(i));
                }
            }
        }
        return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            View child = findCheckBox(this);
            if (child instanceof CompoundButton) cbx = (CompoundButton) child;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (cbx != null) {
            Rect bounds = new Rect(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + getMeasuredWidth() + getPaddingRight(), getPaddingTop() + getMeasuredHeight() + getPaddingBottom());
            TouchDelegate delegate = new TouchDelegate(bounds, cbx);
            setTouchDelegate(delegate);
        }
    }
}
