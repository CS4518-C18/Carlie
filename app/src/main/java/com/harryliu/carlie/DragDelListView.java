package com.harryliu.carlie;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author Haofan Zhang
 * @version 2/22/18
 */

public class DragDelListView extends ListView {

    private boolean closed = true;
    private int mDownX, mDownY;
    private int mTouchPosition, oldPosition = -1;
    private DragDelItem mTouchView, oldView;
    private Context mContext;
    private boolean moved;

    public DragDelListView(Context context) {
        super(context);
        init(context);
    }

    public DragDelListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public DragDelListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    private void init(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                mTouchView = (DragDelItem) getChildAt(mTouchPosition);
                if (mTouchView == null) {
                    break;
                }
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                mTouchView.mDownX = mDownX;
                if (oldPosition != mTouchPosition && !closed) {
                    if (oldView != null) {
                        oldView.smoothCloseMenu();
                    }
                }
                oldPosition = mTouchPosition;
                oldView = mTouchView;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchView == null) {
                    break;
                }
                /*
                if (Math.abs(mDownX-ev.getX()) < Math.abs(mDownY-ev.getY()) * dp2px(2, mContext)) {
                    break;
                }
                */
                int dis = (int) (mTouchView.mDownX - ev.getX());
                if (mTouchView.state == DragDelItem.STATE_OPEN)
                    dis += mTouchView.mMenuView.getWidth();
                mTouchView.swipe(dis);
                ev.setAction(MotionEvent.ACTION_CANCEL);
                moved = true;
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchView == null) {
                    break;
                }
                if (moved) {
                    if ((mTouchView.mDownX - ev.getX()) > mTouchView.mMenuView.getWidth() / 5) {
                        // open
                        mTouchView.smoothOpenMenu();
                        closed = false;
                    } else {
                        // close
                        mTouchView.smoothCloseMenu();
                        closed = true;
                    }
                    //ev.setAction(MotionEvent.ACTION_CANCEL);
                    moved = false;
                } else {
                    performClick();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }


}