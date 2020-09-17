package com.zhy.view.sliding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class SlidingTabAdapter extends BaseAdapter {

    private ArrayList<String> mData = new ArrayList<>();
    private int mSelectedPosition;
    private ViewGroup mParentView;

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setData(ArrayList<String> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return mData;
    }

    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        mParentView = parent;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutID(), null);
            convertView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (getCallBack() == null) {
                        return;
                    }
                    if (position == mSelectedPosition) {
                        getCallBack().onClickTab(position, parent.getChildAt(position));
                    }
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean enable = onClickEnable();
                    if (enable) {
                        setSelected(position);
                    }
                }
            });
        }
        if (mSelectedPosition == position) {
            onSelectedView(convertView, position);
        } else {
            onNormalView(convertView, position);
        }
        return convertView;
    }

    public abstract int getLayoutID();

    public abstract void onSelectedView(View view, int position);

    public abstract void onNormalView(View view, int position);

    public void setSelected(int position) {
        if (mParentView == null || position > mParentView.getChildCount() - 1) {
            return;
        }
        int old_position = mSelectedPosition;
        View normal = mParentView.getChildAt(old_position);
        View selected = mParentView.getChildAt(position);

        mSelectedPosition = position;

        onNormalView(normal, old_position);
        onSelectedView(selected, position);
    }

    private CallBack mCallBack;

    public CallBack getCallBack() {
        if (mCallBack == null) {
            mCallBack = new CallBack() {
                @Override
                public void onClickTab(int position, View view) {

                }
            };
        }
        return mCallBack;
    }

    public void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    public interface CallBack {
        void onClickTab(int position, View view);
    }

    public boolean onClickEnable() {
        return true;
    }
}
