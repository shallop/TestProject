package com.example.administrator.myapplication.recyclerview;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Administrator on 2016/11/30.
 */
public class CardLayoutManager2 extends RecyclerView.LayoutManager {

    private static final String TAG = "CardLayoutManager";

    private static final boolean DEBUG = true;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;
    public static final int NO_POSITION = -1;

    private LayoutState mLayoutState;
    final AnchorInfo mAnchorInfo = new AnchorInfo();

    public CardLayoutManager2(Context context) {

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        ensureLayoutState();
        mAnchorInfo.reset();
        mLayoutState.mAvailable = getHeight() - getPaddingBottom();
        Log.d(TAG, "onLayoutChildren: " + mLayoutState.mAvailable);
        removeAndRecycleAllViews(recycler);
        mLayoutState.mCurrentPosition = 0;
        mLayoutState.mOffset = 0;
        fill(recycler, mLayoutState, state, false);
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
        int travel = dy;
        return travel;
    }

    int fill(RecyclerView.Recycler recycler, LayoutState layoutState,
             RecyclerView.State state, boolean stopOnFocusable) {
        int remainingSpace = layoutState.mAvailable;
        LayoutChunkResult layoutChunkResult = new LayoutChunkResult();
        while (remainingSpace > 0 && layoutState.hasMore(state)) {
            layoutChunkResult.resetInternal();
            layoutChunk(recycler, state, layoutState, layoutChunkResult);
            if (layoutChunkResult.mFinished) {
                break;
            }
            layoutState.mOffset += layoutChunkResult.mConsumed;
            layoutState.mAvailable -= layoutChunkResult.mConsumed;
            remainingSpace -= layoutChunkResult.mConsumed;
        }
        return 0;
    }

    void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state,
                     LayoutState layoutState, LayoutChunkResult result) {
        View child = layoutState.next(recycler);
        if (child == null) {
            result.mFinished = true;
            return;
        }
        addView(child);
        measureChildWithMargins(child, 0, 0);
        result.mConsumed = getDecoratedMeasurement(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int top = layoutState.mOffset;
        int bottom = layoutState.mOffset + result.mConsumed;
        layoutDecorated(child, left + params.leftMargin, top + params.topMargin,
                right - params.rightMargin, bottom - params.bottomMargin);
        if (DEBUG) {
            Log.d(TAG, "laid out child at position " + getPosition(child) + ", with l:"
                    + (left + params.leftMargin) + ", t:" + (top + params.topMargin) + ", r:"
                    + (right - params.rightMargin) + ", b:" + (bottom - params.bottomMargin));
        }
        result.mFocusable = child.isFocusable();
    }

    void ensureLayoutState() {
        if (mLayoutState == null) {
            mLayoutState = new LayoutState();
        }
    }

    public int getDecoratedMeasurement(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }

    static class LayoutState {
        int mCurrentPosition;
        int mAvailable;
        int mOffset;
        int mItemDirection = 1;
        boolean mIsPreLayout = false;
        int mScrollingOffset;
        List<RecyclerView.ViewHolder> mScrapList = null;


        boolean hasMore(RecyclerView.State state) {
            return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
        }

        View next(RecyclerView.Recycler recycler) {
            Log.d(TAG, "next:2 " + mCurrentPosition);
            final View view = recycler.getViewForPosition(mCurrentPosition);
            mCurrentPosition += mItemDirection;
            return view;
        }
    }

    protected static class LayoutChunkResult {
        public int mConsumed;
        public boolean mFinished;
        public boolean mIgnoreConsumed;
        public boolean mFocusable;

        void resetInternal() {
            mConsumed = 0;
            mFinished = false;
            mIgnoreConsumed = false;
            mFocusable = false;
        }
    }

    class AnchorInfo {
        int mPosition;
        int mCoordinate;

        void reset() {
            mPosition = NO_POSITION;
            mCoordinate = INVALID_OFFSET;
        }
    }

}
