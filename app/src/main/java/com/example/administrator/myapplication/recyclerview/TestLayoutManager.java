package com.example.administrator.myapplication.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/1.
 */
public class TestLayoutManager extends LinearLayoutManager {
    public TestLayoutManager(Context context) {
        super(context);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d("tianfan", "scrollVerticallyBy: " +getChildCount());
        return super.scrollVerticallyBy(dy, recycler, state);
    }
}
