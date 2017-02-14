package com.example.administrator.myapplication.recyclerview;

        import android.animation.Animator;
        import android.animation.ValueAnimator;
        import android.content.Context;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.util.SparseArray;
        import android.view.View;

/**
 * Created by Administrator on 2016/12/13.
 */
public class CardLayoutManagerEL extends LinearLayoutManager {

    private static final String TAG = "CardLayoutManagerEL";

    public static final int SCROLL_DOWN = 0;
    public static final int SCROLL_UP = 1;

    public int mScrollOffset;
    private int mStartOffsetPosition = 0;
    private int mScrollDownY;
    private int mCoverDistance = 180;
    private boolean mCanScrollDown = false;
    private ValueAnimator mAnimator;
    private SparseArray<int[]> mItemsInfo = new SparseArray<>();
    private ChildOffsetListener mOffsetListener;

    interface ChildOffsetListener {
        void offsetStart(int direction);

        void offset(int scrollOffset);

        void offsetEnd(int direction);
    }

    public void setChildOffsetListener(ChildOffsetListener listener) {
        mOffsetListener = listener;
    }

    public CardLayoutManagerEL(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "onLayoutChildren:1 " + mCanScrollDown);
        if (mCanScrollDown) {
            return;
        }
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        mCanScrollDown = false;
        if (getPosition(getChildClosestToStart()) == 0) {
            View child = getChildClosestToStart();
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (getChildClosestToStart().getTop() - params.topMargin == 0) {
                mScrollOffset = 0;
            }
            if (getChildClosestToStart().getTop() - params.topMargin > 0) {
                Log.d(TAG, "scrollVerticallyBy:true ");
                mCanScrollDown = true;
            } else {
                mScrollOffset = 0;
                mItemsInfo.clear();
                mCanScrollDown = false;
            }
        }
        if (mAnimator != null && mAnimator.isRunning()) {
            return 0;
        }
        if (mCanScrollDown && mScrollOffset + dy < 0) {
            Log.d(TAG, "scrollVerticallyBy: " + canScrollDown() + "   " + (mScrollOffset + dy));
            offsetChild(dy);
            return 0;
        } else {
            return super.scrollVerticallyBy(dy, recycler, state);
        }
    }

    private void offsetChild(int dy) {
        int offset = dy;
        if (mScrollOffset + dy < -getVerticalSpace() + mScrollDownY) {
            offset = -getVerticalSpace() + mScrollDownY - mScrollOffset;
        }
        RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) getChildClosestToStart().getLayoutParams();
        if (getChildClosestToStart().getTop() - params1.topMargin >= 0) {
            mCanScrollDown = true;
        }
        //handle position and scale
        mScrollOffset += offset;
//        mOffsetListener.offset(mScrollOffset);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setElevation(-i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int height = getDecoratedMeasuredHeight(child);
            int left = getPaddingLeft();
            int right = getDecoratedMeasuredWidth(child) + left;
            int top;
            int bottom;

            int[] verticalCoordinate = new int[2];
            if (i == 0) {
                verticalCoordinate[0] = 0;
                verticalCoordinate[1] = height + params.topMargin;
            } else {
                verticalCoordinate[0] = mItemsInfo.get(i - 1)[1];
                verticalCoordinate[1] = verticalCoordinate[0] + height + params.topMargin;
            }
            mItemsInfo.put(i, verticalCoordinate);
            ///////////////
            int tempPosition = i - mStartOffsetPosition >= 0 ? i - mStartOffsetPosition : 0;
            if (Math.abs(mScrollOffset) > mCoverDistance * tempPosition) {
                top = mItemsInfo.get(i)[0] - mScrollOffset - mCoverDistance * tempPosition;
                bottom = mItemsInfo.get(i)[1] - mScrollOffset - mCoverDistance * tempPosition;
            } else {
                top = mItemsInfo.get(i)[0];
                bottom = mItemsInfo.get(i)[1];
            }
            layoutDecorated(child, left + params.leftMargin, top + params.topMargin,
                    right + params.rightMargin, bottom + params.bottomMargin);
        }
    }

    public void scrollViewSmooth(final int direction) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        final int aboveDistance = getChildAt(0).getTop() -
                ((RecyclerView.LayoutParams) getChildAt(0).getLayoutParams()).topMargin;
        final int bellowDistance = getVerticalSpace() - getChildAt(0).getTop();
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int tempY;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (direction == SCROLL_DOWN) {
                    int val = (int) (((float) animation.getAnimatedValue()) * bellowDistance);
                    offsetChild(-val - tempY);
                    tempY = -val;
                } else if (direction == SCROLL_UP) {
                    int val = (int) (((float) animation.getAnimatedValue()) * aboveDistance);
                    offsetChild(val - tempY);
                    tempY = val;
                }
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
//                mOffsetListener.offsetStart(direction);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                mOffsetListener.offsetEnd(direction);
                if (direction == SCROLL_UP) {
                    mCanScrollDown = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.setDuration(495);
        mAnimator.start();
    }

    public boolean canScrollDown() {
        return mCanScrollDown;
    }

    public void setScrollDownY(int position) {
        mScrollDownY = position;
    }

    public void setStartOffsetPosition(int position) {
        if (mStartOffsetPosition >= 0 && mStartOffsetPosition < getItemCount()) {
            mStartOffsetPosition = position;
        } else {
            Log.d(TAG, "setStartOffsetPosition:failed because position bigger than item count ");
        }
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    private View getChildClosestToStart() {
        return getChildAt(0);
    }

    private View getChildClosestToEnd() {
        return getChildAt(getChildCount() - 1);
    }


}
