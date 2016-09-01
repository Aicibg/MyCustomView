package com.app.mycustomview.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private int scaleArcRadius;//刻度弧半径

    private int mTickCount=40;
    private String scale;//刻度数
    private int mLongTikeHeight=dp2px(10);
    private int mShortTickHeight=dp2px(5);

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
        outCirclePaint.setStrokeWidth(1);
        outCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);//描边并填充
        outCirclePaint.setColor(getResources().getColor(R.color.temperatureBackground));

        progressPaint=new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(dp2px(PADDING));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);//笔刷样式为圆形
        progressPaint.setStrokeJoin(Paint.Join.ROUND);//style为stroke时，绘制各图形的结合方式，如影响矩形角的外轮廓

        progressTextPaint=new Paint();
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setStyle(Paint.Style.FILL);
        progressTextPaint.setColor(Color.BLACK);
        progressTextPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体

        scaleArcPaint=new Paint();
        scaleArcPaint.setStyle(Paint.Style.STROKE);
        scaleArcPaint.setAntiAlias(true);
        scaleArcPaint.setStrokeWidth(dp2px(2));

        panelTextPaint=new Paint();
        panelTextPaint.setAntiAlias(true);
        panelTextPaint.setColor(Color.BLACK);
        panelTextPaint.setStyle(Paint.Style.FILL);
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
    }

    private void drawScaleArc(Canvas canvas) {
        canvas.save();
        RectF rectf=new RectF(-scaleArcRadius,-scaleArcRadius,scaleArcRadius,scaleArcRadius);
        canvas.drawArc(rectf,150,240,false,scaleArcPaint);

        float mAngle=240f/mTickCount;
        //画右半部分的刻度
        for(int i=0;i<=mTickCount/2;i++){
            if (i%5==0){
                scale=20+i+"";
                panelTextPaint.setTextSize(sp2px(15));
                 float scaleWidth=panelTextPaint.measureText(scale);
                canvas.drawLine(0,-scaleArcRadius,0,-scaleArcRadius+mLongTikeHeight,panelTextPaint);
                canvas.drawText(scale,-scaleWidth/2,-scaleArcRadius+mLongTikeHeight+dp2px(15),panelTextPaint);
            }else {
                canvas.drawLine(0,-scaleArcRadius,0,-scaleArcRadius+mShortTickHeight,panelTextPaint);
            }
            canvas.rotate(mAngle,0,0);
        }
        canvas.rotate(-mAngle*mTickCount/2-6,0,0);
        for(int i=0;i<=mTickCount/2;i++){
            if (i%5==0){
                scale=20-i+"";
                panelTextPaint.setTextSize(sp2px(15));
                float scaleWidth=panelTextPaint.measureText(scale);
                canvas.drawLine(0,-scaleArcRadius,0,-scaleArcRadius+mLongTikeHeight,panelTextPaint);
                canvas.drawText(scale,-scaleWidth/2,-scaleArcRadius+mLongTikeHeight+dp2px(15),panelTextPaint);
            }else {
                canvas.drawLine(0,-scaleArcRadius,0,-scaleArcRadius+mShortTickHeight,panelTextPaint);
            }
            canvas.rotate(-mAngle,0,0);
        }
        canvas.rotate(-mAngle+mTickCount/2+6,0,0);
        canvas.restore();
    }

    private void drawProgressText(Canvas canvas) {
        canvas.save();
        String normal="正常";
        String warn="预警";
        String danger="警告";
        canvas.rotate(-60,0,0);
        progressTextPaint.setTextAlign(Paint.Align.CENTER);
        progressTextPaint.setTextSize(sp2px(12));
        scaleArcRadius=mSize/2-(dp2px(15)+dp2px(PADDING)/4);
        Rect bounds=new Rect();
        progressTextPaint.getTextBounds(normal,0,normal.length(),bounds);
        canvas.drawText(warn,dp2px(12),-scaleArcRadius-dp2px(4),progressTextPaint);
        canvas.rotate(90,0,0);
        canvas.drawText(warn,dp2px(12),-scaleArcRadius-dp2px(4),progressTextPaint);
        canvas.rotate(60,0,0);
        canvas.drawText(danger,dp2px(12),-scaleArcRadius-dp2px(4),progressTextPaint);
        canvas.rotate(-60,0,0);
        canvas.restore();
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
        int result = 0;
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

}
