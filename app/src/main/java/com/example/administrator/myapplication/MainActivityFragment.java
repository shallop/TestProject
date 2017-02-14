package com.example.administrator.myapplication;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.administrator.myapplication.recyclerview.CardLayoutManager;
import com.example.administrator.myapplication.recyclerview.CardLayoutManager1;
import com.example.administrator.myapplication.recyclerview.CardLayoutManager2;
import com.example.administrator.myapplication.recyclerview.CardLayoutManagerEL;
import com.example.administrator.myapplication.recyclerview.CardLayoutManagerWithoutRecycle;
import com.example.administrator.myapplication.recyclerview.CircleLayoutManager;
import com.example.administrator.myapplication.recyclerview.FixedGridLayoutManager;
import com.example.administrator.myapplication.recyclerview.MyLayoutManager;
import com.example.administrator.myapplication.recyclerview.ScrollZoomLayoutManager;
import com.example.administrator.myapplication.recyclerview.TestLayoutManager;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = "MainActivityFragment";
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
//    private LinearLayout mLinearLayout;
//    private View mBall;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
//        mLinearLayout = (LinearLayout) view.findViewById(R.id.content);
//        mBall = view.findViewById(R.id.ball);
//        mBall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBall.offsetTopAndBottom(500);
//                Toast.makeText(getContext(), " aaaa" , Toast.LENGTH_LONG).show();
//            }
//        });
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {

        MyLayoutManager myLayoutManager = new MyLayoutManager(getContext());
        mRecyclerView.setLayoutManager(myLayoutManager);
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        int[] color = {0xffffffcd, 0xffb03060, 0xffeb8e55, 0xff3d59ab, 0xffa066d3};
        int[] height;

        public MyAdapter() {
            super();
            height = new int[getItemCount()];
            for (int i = 0; i < getItemCount(); i++) {
                height[i] = (int) (200 + Math.random() * 500);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            Log.d(TAG, "onCreateViewHolder: ");
            return new ViewHolder(
                    getActivity().getLayoutInflater().inflate(R.layout.recycler_cell, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
//            Log.d(TAG, "onBindViewHolder: " + position);
            if (position == 0) {
//                holder.cell.setBackgroundColor(
//                        getResources().getColor(android.R.color.transparent, null));
                holder.cell.setBackgroundColor(0xff000000);
                holder.cell.getLayoutParams().height = 300;
            } else {
                holder.cell.setBackgroundColor(color[position % 5]);
                holder.cell.getLayoutParams().height = height[position];
            }
            holder.cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.cell.getLayoutParams().height = height[(int) (Math.random() * (getItemCount() - 1))];
                    holder.cell.requestLayout();
//                    ((CardLayoutManagerEL)mRecyclerView.getLayoutManager()).scrollViewSmooth(CardLayoutManagerEL.SCROLL_DOWN);
//                    Log.d(TAG, "onClick: " + ((CardLayoutManagerEL) mRecyclerView.getLayoutManager()).mScrollOffset);
                }
            });
            holder.cell.requestLayout();
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
//            Log.d(TAG, "onViewRecycled: ");
            super.onViewRecycled(holder);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            View cell;

            public ViewHolder(View itemView) {
                super(itemView);
                cell = itemView;
            }
        }
    }
}
