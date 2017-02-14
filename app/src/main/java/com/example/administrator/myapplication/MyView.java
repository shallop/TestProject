package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2016/12/5.
 */
public class MyView extends View {

    private static final String TAG = "MyView";
    private View mBall;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "MyView: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.colorPrimary, null));
        canvas.drawCircle(100, 100, 100, p);
    }
}
