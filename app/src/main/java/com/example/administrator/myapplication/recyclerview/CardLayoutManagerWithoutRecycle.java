package com.example.administrator.myapplication.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/11/30.
 */
public class CardLayoutManagerWithoutRecycle extends RecyclerView.LayoutManager {

    private static final String TAG = "CardLayoutManager";

    public static final int MODE_NORMAL = 0;
    public static final int MODE_OFFSET = 1;
    public static final int MODE_SPEED = 2;

    private int mTotalHeight;
    private int mVerticalScrollOffset = 0;
    private int mScrollMode = 1;
    private int mStartOffsetPosition = 0;
    private int mCoverDistance = 180;

    public CardLayoutManagerWithoutRecycle(Context context) {

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() <= 0) return;
        if (state.isPreLayout()) return;
        detachAndScrapAttachedViews(recycler);
        mTotalHeight = 0;
        int offsetY = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int height = getDecoratedMeasuredHeight(child);
            int margin = ((RecyclerView.LayoutParams) child.getLayoutParams()).bottomMargin +
                    ((RecyclerView.LayoutParams) child.getLayoutParams()).topMargin;
            mTotalHeight += height + margin;
            offsetY += height + margin;
        }
        layoutItems(recycler, state);
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //实际要滑动的距离
        detachAndScrapAttachedViews(recycler);
        int travel = dy;
        if (mVerticalScrollOffset + dy > mTotalHeight - getVerticalSpace()) {
            travel = mTotalHeight - getVerticalSpace() - mVerticalScrollOffset;
        } else if (mVerticalScrollOffset + dy < -getVerticalSpace()) {
            travel = -getVerticalSpace() - mVerticalScrollOffset;
        }

        //将竖直方向的偏移量+travel
        mVerticalScrollOffset += travel;
        layoutItems(recycler, state);
        return travel;
    }

    private void layoutItems(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.isPreLayout()) return;

        int offsetY = mTotalHeight;
        //remove the views which out of range
        for (int i = getItemCount() - 1; i >= 0; i--) {
            Log.d("tianfan", "layoutItems:++++ " + mVerticalScrollOffset);
            View child = recycler.getViewForPosition(i);
            measureChildWithMargins(child, 0, 0);
            int width = getDecoratedMeasuredWidth(child);
            int height = getDecoratedMeasuredHeight(child);
            int margin = ((RecyclerView.LayoutParams) child.getLayoutParams()).bottomMargin +
                    ((RecyclerView.LayoutParams) child.getLayoutParams()).topMargin;
            offsetY -= height + margin;
            int left = 0;
            int right = width;
            int top = 0;
            int bottom = 0;
            if (mScrollMode == MODE_OFFSET) {
                if (mVerticalScrollOffset > 0) {
                    top = offsetY - mVerticalScrollOffset;
                    bottom = offsetY + height - mVerticalScrollOffset;
                } else {
                    int tempPosition = i - mStartOffsetPosition >= 0 ? i - mStartOffsetPosition : 0;
                    if (Math.abs(mVerticalScrollOffset) > mCoverDistance * tempPosition) {
                        top = offsetY - mVerticalScrollOffset - mCoverDistance * tempPosition;
                        bottom = offsetY + height - mVerticalScrollOffset - mCoverDistance * tempPosition;
                    } else {
                        top = offsetY;
                        bottom = offsetY + height;
                    }
                }
            } else if (mScrollMode == MODE_NORMAL) {
                top = offsetY - mVerticalScrollOffset;
                bottom = offsetY + height - mVerticalScrollOffset;
            } else if (mScrollMode == MODE_SPEED) {
                top = offsetY - mVerticalScrollOffset / (i + 1);
                bottom = offsetY + height - mVerticalScrollOffset / (i + 1);
            }
            layoutDecorated(child, left, top, right, bottom);
            addView(child);
        }
    }

}
