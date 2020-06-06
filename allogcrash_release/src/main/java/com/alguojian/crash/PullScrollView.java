package com.alguojian.crash;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/7/19
 */
public class PullScrollView extends NestedScrollView {

    private View rootView;

    private int mpreY;
    private Rect mRect;
    private int mRootViewTop;

    public PullScrollView(@NonNull Context context) {
        this(context, null);
    }

    public PullScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        rootView = getChildAt(0);
        super.onFinishInflate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mpreY = (int) ev.getY();
                mRootViewTop = rootView.getTop();
                mRect = new Rect(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom());
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (rootView == null) {
            return super.onTouchEvent(ev);
        } else {
            commonTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 处理滑动监听
     *
     * @param event
     */
    private void commonTouchEvent(MotionEvent event) {

        float y = event.getY();
        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:

                int v = (int) ((y - mpreY) * 0.20);

                if (rootView.getTop() - mRootViewTop <= 300 && rootView.getTop() - mRootViewTop >= -300) {
                    rootView.layout(rootView.getLeft(), rootView.getTop() + v, rootView.getRight(), rootView.getBottom() + v);
                }
                break;

            case MotionEvent.ACTION_UP:
                int top = rootView.getTop();
                rootView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, top - mRootViewTop, 0);
                translateAnimation.setDuration(300);
                rootView.startAnimation(translateAnimation);
                break;

            case MotionEvent.ACTION_DOWN:
                break;

            default:
                break;
        }
        mpreY = (int) y;
    }
}
