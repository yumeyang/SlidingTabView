package com.zhy.view.sliding;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingTabView extends HorizontalScrollView {

    private LinearLayout mTabContent;
    private SlidingTabAdapter mAdapter;

    private boolean mEnableScroll = true;

    public SlidingTabView(Context context) {
        this(context, null);
    }

    public SlidingTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTabContent = new LinearLayout(context);
        addView(mTabContent);
    }

    public void setAdapter(SlidingTabAdapter adapter) {
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mObserver);
        mAdapter.setCallBack(new SlidingTabAdapter.CallBack() {
            @Override
            public void onClickTab(final int position, final View view) {
                scrollToTab(position);
            }
        });
    }

    private void scrollToTab(int index) {
        View child = mTabContent.getChildAt(index);
        int left = child.getLeft();
        int width = child.getWidth();
        //item距离正中间的偏移量
        int offset = (int) ((getWidth() - width) / 2.0f);
        left -= offset;
        scrollTo(left, 0);
    }

    private int getCount() {
        if (mAdapter == null) {
            return -1;
        }
        return mAdapter.getCount();
    }

    private int getSelected() {
        if (mAdapter == null) {
            return -1;
        }
        return mAdapter.getSelectedPosition();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return true;
    }

    private GestureDetector mDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent begin, MotionEvent end, float velocity_x, float velocity_y) {
            getListener().onScrollStart();

            if (!mEnableScroll) {
                return false;
            }

            if (begin == null) {
                return false;
            }
            if (end == null) {
                return false;
            }

            float min_move = 50;//最小滑动距离
            float min_velocity = 0;//最小滑动速度
            float begin_x = begin.getX();
            float end_x = end.getX();

            int selected = getSelected();
            if (begin_x - end_x > min_move && Math.abs(velocity_x) > min_velocity) {
                //左滑
                if (selected >= getCount() - 1) {
                    return false;
                }

                selected = selected + 1;
                mAdapter.setSelected(selected);
            } else if (end_x - begin_x > min_move && Math.abs(velocity_x) > min_velocity) {
                //右滑
                if (selected <= 0) {
                    return false;
                }

                selected = selected - 1;
                mAdapter.setSelected(selected);
            }
            getListener().onScrollEnd();
            return false;
        }
    });

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (getCount() == 0) {
                return;
            }
            mTabContent.removeAllViews();
            for (int i = 0; i < getCount(); i++) {
                View view = mAdapter.getView(i, null, mTabContent);
                mTabContent.addView(view);
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        measureContent();
    }

    private void measureContent() {
        int width = getWidth();
        if (width > 0) {
            int count = mTabContent.getChildCount();
            if (count == 0) {
                return;
            }

            View first = mTabContent.getChildAt(0);
            View last = mTabContent.getChildAt(mTabContent.getChildCount() - 1);
            int first_width = first.getWidth();
            int last_width = last.getWidth();
            int left = width / 2 - first_width / 2;
            int right = width / 2 - last_width / 2;
            mTabContent.setPadding(left, 0, right, 0);
        }
    }

    private OnSlidingScrollListener mListener;

    public OnSlidingScrollListener getListener() {
        if (mListener == null) {
            mListener = new OnSlidingScrollListener() {
                @Override
                public void onScrollStart() {

                }

                @Override
                public void onScrollEnd() {

                }
            };
        }
        return mListener;
    }

    public void setListener(OnSlidingScrollListener mListener) {
        this.mListener = mListener;
    }

    public interface OnSlidingScrollListener {
        void onScrollStart();

        void onScrollEnd();
    }

    public void setEnableScroll(boolean enable) {
        mEnableScroll = enable;
    }
}