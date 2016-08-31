package com.app.mycustomview.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.app.mycustomview.R;

/**
 * Created by DongHao on 2016/8/31
 * Description:温度显示view
 */
public class TemperatureView extends View {
    private static final int PADDING = 15;
    private float progressWidth;
    private String tempText;
    private float tempTextSize;

    private int mSize;//最终大小
    private Paint outCirclePaint;
    private int progressRadius;
    private Paint progressPaint;

    public TemperatureView(Context context) {
        this(context, null);
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.temperatureProgress);
        progressWidth = typedArray.getDimension(R.styleable.temperatureProgress_progressWidth, PADDING);
        tempText = typedArray.getString(R.styleable.temperatureProgress_tempText);
        tempTextSize = typedArray.getDimension(R.styleable.temperatureProgress_tempTextSize, sp2px(15));
        typedArray.recycle();

        initPaint();
    }

    private void initPaint() {
        outCirclePaint = new Paint();
        outCirclePaint.setAntiAlias(true);
        outCirclePaint.setStrokeWidth(5);
        outCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);//描边并填充
        outCirclePaint.setColor(getResources().getColor(R.color.temperatureBackground));

        progressPaint=new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp2px(PADDING));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);//笔刷样式为圆形
        progressPaint.setStrokeJoin(Paint.Join.ROUND);//style为stroke时，绘制各图形的结合方式，如影响矩形角的外轮廓
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = startMeasure(widthMeasureSpec);
        int height = startMeasure(heightMeasureSpec);
        //最终以正方形为基础
        mSize = Math.min(width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画布移动到中央
        canvas.translate(mSize / 2, mSize / 2);
        //画最外面的圆
        drawOutCircle(canvas);

        //画进度
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        progressRadius=mSize/2-dp2px(10);
        canvas.save();
        RectF rectf=new RectF(-progressRadius,-progressRadius,progressRadius,progressRadius);

        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.GREEN);
        canvas.drawArc(rectf,150,120,false,progressPaint);

        progressPaint.setColor(Color.RED);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectf,330,60,false,progressPaint);

        progressPaint.setColor(Color.YELLOW);
        progressPaint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawArc(rectf,270,60,false,progressPaint);

        canvas.restore();
    }

    /**
     * 画最外面的圆
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, mSize / 2-dp2px(1), outCirclePaint);
        canvas.save();
    }

    /**
     * 测量大小
     *
     * @param measureSpec
     * @return
     */
    private int startMeasure(int measureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = dp2px(100);
        }
        return result;
    }

    /**
     * 将 dp 转换为 px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

}
