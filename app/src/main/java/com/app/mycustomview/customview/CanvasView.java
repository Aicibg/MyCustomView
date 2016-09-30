package com.app.mycustomview.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.app.mycustomview.R;

/**
 * Created by DongHao on 2016/9/30.
 * Description:
 */

public class CanvasView extends View {
    private Paint mPaint;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画边框
        drawBorder(canvas);
        //drawARGB
       // drawArgb(canvas);
        //画弧面
       // drawarc(canvas);
        //画bitmap
      // drawbitmap(canvas);
        //画点
     //   ddrawPoint(canvas);
       //沿着path写文字
      //  drawPathtext(canvas);
        //裁剪路径
        drawClipPath(canvas);
    }

    private void drawClipPath(Canvas canvas) {
        Path path=new Path();
        RectF rectF=new RectF(0,0,300,300);
        path.addArc(rectF,0,150);
        canvas.clipPath(path);
        canvas.drawColor(Color.RED);
    }

    private void drawPathtext(Canvas canvas) {
        Path path=new Path();
        path.addCircle(150,150,100, Path.Direction.CW);
        mPaint.setTextSize(20);
        canvas.drawPath(path,mPaint);
        String text="沿着path写文字";
        canvas.drawTextOnPath(text,path,0f,-8f,mPaint);
    }

    private void ddrawPoint(Canvas canvas) {
        canvas.drawPoint(10,10,mPaint);
    }

    private void drawbitmap(Canvas canvas) {
        Bitmap mBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        canvas.drawBitmap(mBitmap,0,0,mPaint);

//        Rect src=new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight()/3);
//        RectF dst=new RectF(0,mBitmap.getHeight(),canvas.getWidth(),mBitmap.getHeight()+200);
//        canvas.drawBitmap(mBitmap,src,dst,mPaint);

        Matrix matrix=new Matrix();
        matrix.postTranslate(0,mBitmap.getHeight()+100);
        canvas.drawBitmap(mBitmap,matrix,mPaint);
    }

    private void drawarc(Canvas canvas) {
        RectF rectF=new RectF(0,0,200,200);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(rectF,0,150,false,mPaint);
    }

    private void drawArgb(Canvas canvas) {
        canvas.drawARGB(122,111,22,11);
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawRect(0,0,300,300,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        int width = 0;
        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = 300;
                break;
            case MeasureSpec.EXACTLY:
                height = heightSpecSize;
                break;
            default:
                break;
        }

        switch (widthSpecMode) {
            case MeasureSpec.AT_MOST:
                width = 300;
                break;
            case MeasureSpec.EXACTLY:
                width = widthSpecSize;
                break;
            default:
                break;
        }

        setMeasuredDimension(width, height);
    }
}
