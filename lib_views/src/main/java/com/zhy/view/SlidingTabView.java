package com.zhy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.zhy.adapter.SlidingTabAdapter;

public class SlidingTabView extends HorizontalScrollView {

    private LinearLayout mTabContent;
    private SlidingTabAdapter mAdapter;

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
        initListener();
    }

    private void initListener() {

        mDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
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
                if (begin == null) {
                    return false;
                }
                if (end == null) {
                    return false;
                }

                float min_move = 60;//最小滑动距离
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

                return false;
            }
        });

        mTabContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!mIsGlobalLayout) {
                    mIsGlobalLayout = true;
                    View first = mTabContent.getChildAt(0);
                    View last = mTabContent.getChildAt(mTabContent.getChildCount() - 1);
                    int first_width = first.getWidth();
                    int last_width = last.getWidth();
                    int left = getWidth() / 2 - first_width / 2;
                    int right = getWidth() / 2 - last_width / 2;
                    mTabContent.setPadding(left, 0, right, 0);
                }
            }
        });
    }

    protected final void setAdapter(SlidingTabAdapter adapter) {
        mTabContent.removeAllViews();
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mObserver);
            mAdapter.setCallBack(null);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mObserver);
        mAdapter.setCallBack(new SlidingTabAdapter.CallBack() {
            @Override
            public void onClicked(final int position, final View view) {
                if (mIsGlobalLayout) {
                    scrollToTab(position);
                } else {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setSelected(position);
                        }
                    }, 100);
                }
            }
        });
    }

    private void scrollToTab(int index) {
        View child = mTabContent.getChildAt(index);
        //当前item的偏移量
        int left = child.getLeft();
        int width = child.getWidth();
        Log.e("scrollToTab", "left =" + left + " width = " + width + " count =" + mTabContent.getChildCount());
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
        return mAdapter.getSingleSelected();
    }

    private boolean mIsGlobalLayout;

    private void addItemView() {
        mTabContent.removeAllViews();

        if (getCount() == 0) {
            return;
        }

        for (int i = 0; i < getCount(); i++) {
            View view = mAdapter.getView(i, null, mTabContent);
            mTabContent.addView(view);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return true;
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            addItemView();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    private GestureDetector mDetector;
}