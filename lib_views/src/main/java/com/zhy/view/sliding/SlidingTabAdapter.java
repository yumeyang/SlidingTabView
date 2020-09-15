package com.zhy.view.sliding;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class SlidingTabAdapter extends BaseAdapter {

    private ArrayList<String> mData = new ArrayList<>();
    private int mSingleSelected;
    private ViewGroup mParentView;

    public int getSingleSelected() {
        return mSingleSelected;
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
    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (mParentView == null) {
            mParentView = parent;
        }

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(getLayoutID(), null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean click = onClickBefore();
                    if (click) {
                        return;
                    }
                    setSelected(position);
                }
            });
        }

        if (mSingleSelected == position) {
            onSelectedView(convertView, position);
        } else {
            onNormalView(convertView, position);
        }
        return convertView;
    }

    protected abstract int getLayoutID();

    public abstract void onSelectedView(View view, int position);

    public abstract void onNormalView(View view, int position);

    public void setSelected(int position) {
        if (mParentView == null || position > mParentView.getChildCount() - 1) {
            return;
        }

        View normal = mParentView.getChildAt(mSingleSelected);
        onNormalView(normal, mSingleSelected);

        View selected = mParentView.getChildAt(position);
        onSelectedView(selected, position);

        mSingleSelected = position;

        if (mCallBack == null) {
            return;
        }
        mCallBack.onClicked(position, selected);
    }

    private CallBack mCallBack;

    protected final void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    protected interface CallBack {
        void onClicked(int position, View view);
    }

    protected boolean onClickBefore() {
        return false;
    }
}
