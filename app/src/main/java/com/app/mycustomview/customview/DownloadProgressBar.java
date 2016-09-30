package com.app.mycustomview.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.app.mycustomview.R;

/**
 * Created by DongHao on 2016/9/29.
 * Description:
 */

public class DownloadProgressBar extends View implements Runnable {

    private int textSize;
    private int loadingColor;
    private int stopColor;
    private int progressColor;
    private int downloadLeft;
    private int DEFAULT_HEIGHT = 40;

    private Paint bgPaint;
    private Paint textPain;
    private Rect textBouds;
    private Bitmap downloadBitmap;
    private Bitmap pgBitmap;
    private Canvas pgCanvas;

    private float currentProgress;
    private float MAX_PROGRESS = 100f;

    private String progressText;

    private boolean isStop;

    private PorterDuffXfermode xFermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private Thread thread;
    private boolean isFinish;


    public DownloadProgressBar(Context context) {
        this(context, null);
    }

    public DownloadProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typeArray = getContext().obtainStyledAttributes(attrs, R.styleable.DownloadProgressBar);
            textSize = (int) typeArray.getDimension(R.styleable.DownloadProgressBar_textSize, dp2px(12));
            loadingColor = typeArray.getColor(R.styleable.DownloadProgressBar_loading_color, Color.parseColor("#40c4ff"));
            stopColor = typeArray.getColor(R.styleable.DownloadProgressBar_stop_color, Color.parseColor("#ff9800"));
            typeArray.recycle();
        }
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPain.setTextSize(textSize);
        textBouds = new Rect();

        progressColor = loadingColor;
        downloadBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.flicker);
        downloadLeft = -downloadBitmap.getWidth();

        pgBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        pgCanvas = new Canvas(pgBitmap);

        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = (int) dp2px(DEFAULT_HEIGHT);
                break;
            case MeasureSpec.UNSPECIFIED:
                height = heightSpecSize;
                break;
            default:
                break;
        }
        setMeasuredDimension(widthSpecSize, height);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //边框
        drawBorder(canvas);
        //进度
        drawProgress();
        canvas.drawBitmap(pgBitmap, 0, 0, null);
        //进度文本
        drawProgressText(canvas);
        //文本变色处理
        drawColorProgressText(canvas);
    }

    private void drawColorProgressText(Canvas canvas) {
        textPain.setColor(Color.WHITE);
        int width=textBouds.width();
        int height=textBouds.height();
        float xWidth=(getMeasuredWidth()-width)/2;
        float yHeight=(getMeasuredHeight()+height)/2;
        float progressWidth=(currentProgress/MAX_PROGRESS)*getMeasuredWidth();
        if (progressWidth>xWidth){
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            float right=Math.min(progressWidth,xWidth+width);
            canvas.clipRect(xWidth,0,right,getMeasuredHeight());
            canvas.drawText(progressText,xWidth,yHeight,textPain);
            canvas.restore();
        }
    }

    private void drawProgressText(Canvas canvas) {
        textPain.setColor(progressColor);
        progressText = getProgressText();
        textPain.getTextBounds(progressText,0,progressText.length(),textBouds);
        int width=textBouds.width();
        int height=textBouds.height();
        float xWidth=(getMeasuredWidth()-width)/2;
        float yHeight=(getMeasuredHeight()+height)/2;
        canvas.drawText(progressText,xWidth,yHeight,textPain);
    }

    private void drawProgress() {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(0);
        bgPaint.setColor(progressColor);

        float right = (currentProgress / MAX_PROGRESS) * getMeasuredWidth();
        pgCanvas.save(Canvas.CLIP_SAVE_FLAG);
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();

        if (!isStop) {
            bgPaint.setXfermode(xFermode);
            pgCanvas.drawBitmap(downloadBitmap, downloadLeft, 0, bgPaint);
            bgPaint.setXfermode(null);
        }
    }

    private void drawBorder(Canvas canvas) {
        bgPaint.setColor(progressColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
    }

    @Override
    public void run() {
       int width=downloadBitmap.getWidth();
        if (!isStop){
            downloadLeft+=dp2px(5);
            float progressWidth=(currentProgress/MAX_PROGRESS)*getMeasuredWidth();
            if (progressWidth<=downloadLeft){
                downloadLeft=-width;
            }
            postInvalidate();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop){
            progressColor=stopColor;
        }else {
            progressColor=loadingColor;
            thread=new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public void toggle(){
        if (!isFinish){
            if (isStop){
                setStop(false);
            }else {
                setStop(true);
            }
        }
    }

    public float getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(float currentProgress) {
        if (!isStop) {
            this.currentProgress = currentProgress;
            invalidate();
        }
    }

    private float dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return dp * density;
    }

    public String getProgressText() {
        String str = null;
        if (!isFinish) {
            if (!isStop) {
                str = "下载中"+currentProgress+"%";
            } else {
                str = "继续";
            }
        } else {
            str = "下载完成";
        }
        return str;
    }
}
