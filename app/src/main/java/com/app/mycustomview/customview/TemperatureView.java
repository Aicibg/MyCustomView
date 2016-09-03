package com.app.mycustomview.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    private static final int OFFSET =5;
    private float progressWidth;
    private String tempText;
    private float tempTextSize;

    private int mSize;//最终大小
    private Paint outCirclePaint;
    private int progressRadius;
    private Paint progressPaint;
    private Paint progressTextPaint;
    private Paint scaleArcPaint;
    private Paint panelTextPaint;
    private Paint pointPaint;
    private Paint leftPointPaint;
    private Paint rightPointPaint;
    private Paint pointCirclePaint;

    private int scaleArcRadius;//刻度弧半径
    private int mTickCount = 40;//刻度个数
    private String scale;//刻度数
    private int mLongTickHeight = dp2px(10);//长刻度
    private int mShortTickHeight = dp2px(5);//短刻度
    private int pointRadius = dp2px(17);//中心圆半径

    private float currentTemp;


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
        outCirclePaint.setAntiAlias(true);//抗锯齿
        outCirclePaint.setStrokeWidth(1);//
        outCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);//描边并填充
        outCirclePaint.setColor(getResources().getColor(R.color.temperatureBackground));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp2px(PADDING));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);//笔刷样式为圆形
        progressPaint.setStrokeJoin(Paint.Join.ROUND);//style为stroke时，绘制各图形的结合方式，如影响矩形角的外轮廓

        progressTextPaint = new Paint();
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setStyle(Paint.Style.FILL);
        progressTextPaint.setColor(Color.BLACK);
        progressTextPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体

        scaleArcPaint = new Paint();
        scaleArcPaint.setStyle(Paint.Style.STROKE);
        scaleArcPaint.setAntiAlias(true);
        scaleArcPaint.setStrokeWidth(dp2px(2));

        panelTextPaint = new Paint();
        panelTextPaint.setAntiAlias(true);
        panelTextPaint.setColor(Color.BLACK);
        panelTextPaint.setStyle(Paint.Style.FILL);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(Color.GRAY);

        leftPointPaint=new Paint();
        leftPointPaint.setAntiAlias(true);
        leftPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        leftPointPaint.setColor(getResources().getColor(R.color.leftPointer));

        rightPointPaint=new Paint();
        rightPointPaint.setAntiAlias(true);
        rightPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rightPointPaint.setColor(getResources().getColor(R.color.rightPointer));

        pointCirclePaint=new Paint();
        pointCirclePaint.setAntiAlias(true);
        pointCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        pointCirclePaint.setColor(Color.GRAY);
        pointCirclePaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = startMeasure(widthMeasureSpec);
        int height = startMeasure(heightMeasureSpec);
        //最终以正方形为基础
        mSize = Math.min(width, height);
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画布移动到中央
        canvas.translate(mSize / 2, mSize / 2);
        //画最外面的圆
        drawOutCircle(canvas);

        //画进度
        drawProgress(canvas);
        //画进度上的文字
        drawProgressText(canvas);

        //画表盘
        drawPanel(canvas);
    }

    private void drawPanel(Canvas canvas) {
        //画刻度弧
        drawScaleArc(canvas);
        //画中间圆
        drawInPoint(canvas);
        //画指针
        drawPointer(canvas);
        //绘制文字
        drawPanelText(canvas);
    }

    /**
     * 表盘上的文字
     * @param canvas
     */
    private void drawPanelText(Canvas canvas) {
        canvas.save();
        String text="当前温度";
        float length=panelTextPaint.measureText(text);
        panelTextPaint.setTextSize(sp2px(15));
        canvas.drawText(text,-length/2,scaleArcRadius/2+dp2px(20),panelTextPaint);

        String temp=currentTemp+"℃";
        panelTextPaint.setTextSize(sp2px(15));
        float tempLength=panelTextPaint.measureText(temp);
        canvas.drawText(temp,-tempLength/2,scaleArcRadius,panelTextPaint);
        canvas.restore();
    }

    /**
     * 画指针
     *
     * @param canvas
     */
    private void drawPointer(Canvas canvas) {
        RectF rectF = new RectF(-pointRadius / 2, -pointRadius / 2, pointRadius / 2, pointRadius / 2);
        canvas.save();
        canvas.rotate(60, 0, 0);
        float angle=currentTemp*6.0f;
        canvas.rotate(angle,0,0);
        //表针左半部分
        Path leftPointPath=new Path();
        leftPointPath.moveTo(pointRadius/2,0);
        leftPointPath.addArc(rectF,0,360);
        leftPointPath.lineTo(0,scaleArcRadius-mLongTickHeight-dp2px(OFFSET)-dp2px(15));
        leftPointPath.lineTo(-pointRadius/2,0);
        leftPointPath.close();
        //表针右半部分
        Path rightPointPath=new Path();
        rightPointPath.moveTo(-pointRadius/2,0);
        rightPointPath.addArc(rectF,0,-180);
        rightPointPath.lineTo(0,scaleArcRadius-mLongTickHeight-dp2px(OFFSET)-dp2px(15));
        rightPointPath.lineTo(0,pointRadius/2);
        rightPointPath.close();
        //表针圆
        Path circlePath=new Path();
        circlePath.addCircle(0,0,pointRadius/4, Path.Direction.CW);//Path.Direction.CW 顺时针

        canvas.drawPath(leftPointPath,leftPointPaint);
        canvas.drawPath(rightPointPath,rightPointPaint);
        canvas.drawPath(circlePath,pointCirclePaint);
        canvas.restore();
    }

    /**
     * 画中间圆
     *
     * @param canvas
     */
    private void drawInPoint(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(0, 0, pointRadius, pointPaint);
        canvas.restore();
    }

    /**
     * 画刻度线
     *
     * @param canvas
     */
    private void drawScaleArc(Canvas canvas) {
        canvas.save();
        RectF rectf = new RectF(-scaleArcRadius, -scaleArcRadius, scaleArcRadius, scaleArcRadius);
        canvas.drawArc(rectf, 150, 240, false, scaleArcPaint);

        float mAngle = 240f / mTickCount;
        //画右半部分的刻度
        for (int i = 0; i <= mTickCount / 2; i++) {
            if (i % 5 == 0) {
                scale = 20 + i + "";
                panelTextPaint.setTextSize(sp2px(15));
                float scaleWidth = panelTextPaint.measureText(scale);
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mLongTickHeight, panelTextPaint);
                canvas.drawText(scale, -scaleWidth / 2, -scaleArcRadius + mLongTickHeight + dp2px(15), panelTextPaint);
            } else {
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mShortTickHeight, panelTextPaint);
            }
            canvas.rotate(mAngle, 0, 0);
        }
        canvas.rotate(-mAngle * mTickCount / 2 - 6, 0, 0);//canvas回正
        //画右半部分的刻度
        for (int i = 0; i <= mTickCount / 2; i++) {
            if (i % 5 == 0) {
                scale = 20 - i + "";
                panelTextPaint.setTextSize(sp2px(15));
                float scaleWidth = panelTextPaint.measureText(scale);
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mLongTickHeight, panelTextPaint);
                canvas.drawText(scale, -scaleWidth / 2, -scaleArcRadius + mLongTickHeight + dp2px(15), panelTextPaint);
            } else {
                canvas.drawLine(0, -scaleArcRadius, 0, -scaleArcRadius + mShortTickHeight, panelTextPaint);
            }
            canvas.rotate(-mAngle, 0, 0);
        }
        canvas.rotate(-mAngle + mTickCount / 2 + 6, 0, 0);
        canvas.restore();
    }

    /**
     * 画进度上的文字
     *
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        canvas.save();
        String normal = "正常";
        String warn = "预警";
        String danger = "警告";
        canvas.rotate(-60, 0, 0);
        progressTextPaint.setTextAlign(Paint.Align.CENTER);
        progressTextPaint.setTextSize(sp2px(12));
        scaleArcRadius = mSize / 2 - (dp2px(15) + dp2px(PADDING) / 4);
        Rect bounds = new Rect();
        progressTextPaint.getTextBounds(normal, 0, normal.length(), bounds);
        canvas.drawText(normal, dp2px(12), -scaleArcRadius - dp2px(4), progressTextPaint);
        canvas.rotate(90, 0, 0);
        canvas.drawText(warn, dp2px(12), -scaleArcRadius - dp2px(4), progressTextPaint);
        canvas.rotate(60, 0, 0);
        canvas.drawText(danger, dp2px(12), -scaleArcRadius - dp2px(4), progressTextPaint);
        canvas.rotate(-60, 0, 0);
        canvas.restore();
    }

    /**
     * 画进度
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        progressRadius = mSize / 2 - dp2px(10);
        canvas.save();
        //画圆弧所在的矩形区域
        RectF rectf = new RectF(-progressRadius, -progressRadius, progressRadius, progressRadius);

        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.GREEN);
        canvas.drawArc(rectf, 150, 120, false, progressPaint);

        progressPaint.setColor(Color.RED);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(rectf, 330, 60, false, progressPaint);

        progressPaint.setColor(Color.YELLOW);
        progressPaint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawArc(rectf, 270, 60, false, progressPaint);

        canvas.restore();
    }

    /**
     * 画最外面的圆
     *
     * @param canvas
     */
    private void drawOutCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, mSize / 2, outCirclePaint);
        canvas.save();
    }

    /**
     * 测量大小
     *
     * @param measureSpec
     * @return
     */
    private int startMeasure(int measureSpec) {
        int result;
        int size = MeasureSpec.getSize(measureSpec);
        int mode = MeasureSpec.getMode(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = dp2px(200);
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

    public void setCurrentTemp(float currentTemp){
        if (currentTemp<0){
            this.currentTemp=0;
        }else if(currentTemp>40){
            this.currentTemp=40;
        }else {
            this.currentTemp=currentTemp;
            postInvalidate();
        }
    }

    public String getTempText() {
        return tempText;
    }

    public void setTempText(String tempText) {
        this.tempText = tempText;
    }

    public float getTempTextSize() {
        return tempTextSize;
    }

    public void setTempTextSize(float tempTextSize) {
        this.tempTextSize = tempTextSize;
    }

    public float getProgressWidth() {
        return progressWidth;
    }

    public void setProgressWidth(float progressWidth) {
        this.progressWidth = progressWidth;
    }
}
