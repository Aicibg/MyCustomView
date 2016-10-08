package com.app.mycustomview.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.app.mycustomview.bean.PieData;

import java.util.ArrayList;

/**
 * Created by DongHao on 2016/10/8.
 * Description:
 */

public class PieView extends View {

    // 颜色表(注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    private float mStartAnger = 0;
    private ArrayList<PieData> mDatas;
    private int mWidth, mHeight;
    private Paint mPaint = new Paint();

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public PieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDatas == null) {
            return;
        }
        float currentAnger = mStartAnger;
        canvas.translate(mWidth / 2, mHeight / 2);
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
        RectF rectF = new RectF(-r, -r, r, r);

        for (int i = 0; i < mDatas.size(); i++) {
            PieData pieData = mDatas.get(i);
            mPaint.setColor(pieData.getColor());
            canvas.drawArc(rectF, currentAnger, pieData.getAnger(), true, mPaint);
            currentAnger += pieData.getAnger();
        }
    }


    public void setmStartAnger(float mStartAnger) {
        this.mStartAnger = mStartAnger;
        invalidate();
    }


    public void setmDatas(ArrayList<PieData> mDatas) {
        this.mDatas = mDatas;
        initData(mDatas);
        invalidate();
    }

    private void initData(ArrayList<PieData> mDatas) {
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        float sumValue = 0;
        for (int i = 0; i < mDatas.size(); i++) {
            PieData pieData = mDatas.get(i);
            sumValue+=pieData.getValues();

            int j=i%mColors.length;
            pieData.setColor(mColors[j]);
        }

        float sumAnger=0;
        for(int i=0;i<mDatas.size();i++){
           PieData pieData=mDatas.get(i);
            float percentage=pieData.getValues()/sumValue;
            float anger=percentage*360;

            pieData.setPercentage(percentage);
            pieData.setAnger(anger);
            sumAnger+=anger;
        }
    }
}
