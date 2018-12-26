package com.ming.imagetailor_lib.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * 椭圆图片裁剪蒙版
 * ----------------------
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/25 15:06
 */
public class CropOvalImageView extends View {

    private int backgroundColor = 0xa8000000;
    private int viewWidth;
    private int viewHeight;
    private float oneDownX;
    private float oneDownY;
    private int rectLeft = 0;
    private int rectRight = 200;
    private int rectTop = 0;
    private int rectBottom = 200;
    //裁剪边框长度
    private int clipBorderLength = 50;
    //裁剪框边框宽度
    private int clipBorderWidth = dp2px(1);


    public CropOvalImageView(Context context) {
        this(context, null);
    }

    public CropOvalImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOvalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取初始宽度
        viewWidth = getMeasuredWidth();
        //获取初始高度
        viewHeight = getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oneDownX = event.getX();
                oneDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                rectRight += event.getX() - oneDownX;
                rectBottom += event.getY() - oneDownY;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        /*裁剪区域矩形*/
        Rect rect = new Rect(rectLeft, rectTop, rectRight, rectBottom);
        RectF rectF = new RectF(rect);
        drawClipArea(canvas, rectF);
        drawBorder(canvas, rectF);
    }


    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0, 0, this.getWidth(), this.getHeight(), null);
        } else {
            canvas.saveLayer(0, 0, this.getWidth(), this.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        }
        canvas.drawColor(backgroundColor);
    }

    /**
     * 绘制裁剪区域
     */
    private void drawClipArea(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        paint.setXfermode(xfermode);
        canvas.drawOval(rectF, paint);
    }

    /**
     * 绘制边框
     */
    private void drawBorder(Canvas canvas, RectF rectF) {
        //画裁剪区域边框的画笔
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(clipBorderWidth);
        borderPaint.setAntiAlias(true);
        canvas.drawOval(rectF, borderPaint);
        //绘制四角
        float left = rectF.left;
        float right = rectF.right;
        float top = rectF.top;
        float bottom = rectF.bottom;
        float leftTopPts[] = {left, top + clipBorderLength, left, top, left, top, top + clipBorderLength, top};
        canvas.drawLines(leftTopPts, borderPaint);
        float rightTopPts[] = {right - clipBorderLength, top, right, top, right, top, right, top + clipBorderLength};
        canvas.drawLines(rightTopPts, borderPaint);
        float leftBottomPts[] = {left, bottom - clipBorderLength, left, bottom, left, bottom, left + clipBorderLength, bottom};
        canvas.drawLines(leftBottomPts, borderPaint);
        float rightBottomPts[] = {right - clipBorderLength, bottom, right, bottom, right, bottom, right, bottom - clipBorderLength};
        canvas.drawLines(rightBottomPts, borderPaint);
    }

    /**
     * dp转px
     */
    private int dp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }

}
