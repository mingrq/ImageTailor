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
    private int oneDownX;
    private int oneDownY;
    private int rectLeft;
    private int rectRight;
    private int rectTop;
    private int rectBottom;
    //裁剪边框长度
    private int clipBorderLength = dp2px(16);
    //裁剪框边框宽度
    private int clipBorderWidth = dp2px(2);
    private boolean isDrawGridLines = false;
    private int oneMoveY;
    private int oneMoveX;
    private RectF rectF;


    public CropOvalImageView(Context context) {
        this(context, null);
    }

    public CropOvalImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropOvalImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取初始宽度
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        //获取初始高度
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        /*初始化裁剪区位置*/
        rectLeft = dp2px(30);
        rectRight = viewWidth - dp2px(30);
        rectTop = (viewHeight - (rectRight - rectLeft)) / 2;
        rectBottom = viewHeight - rectTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oneDownX = (int) (event.getX() + 0.5f);
                oneDownY = (int) (event.getY() + 0.5f);
                isDrawGridLines = true;
                break;
            case MotionEvent.ACTION_MOVE:
                oneMoveX = (int) (event.getX() - oneDownX + 0.5f);
                oneMoveY = (int) (event.getY() - oneDownY + 0.5f);
                if (oneDownX > rectLeft && oneDownX < rectRight && oneDownY > rectTop && oneDownY < rectBottom) {
                    //移动
                        rectLeft = rectLeft + oneMoveX;
                        rectRight = rectRight + oneMoveX;
                        rectTop = rectTop + oneMoveY;
                        rectBottom = rectBottom + oneMoveY;

                } else {
                    //缩放
                    if (oneDownX > rectRight - (rectRight - rectLeft) / 3) {
                        //点击的右部分
                        if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                            //点击的下部分
                            rectRight = rectRight + oneMoveX;
                            rectBottom = rectBottom + oneMoveY;
                        } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                            //点击的上部分
                            rectRight = rectRight + oneMoveX;
                            rectTop = rectTop + oneMoveY;
                        } else {
                            //点击中间部分
                            rectRight = rectRight + oneMoveX;
                            rectTop = rectTop - oneMoveX / 2;
                            rectBottom = rectBottom + oneMoveX / 2;
                        }
                    } else if (oneDownX < rectLeft + (rectRight - rectLeft) / 3) {
                        //点击的左部分
                        if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                            //点击的下部分
                            rectLeft = rectLeft + oneMoveX;
                            rectBottom = rectBottom + oneMoveY;
                        } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                            //点击的上部分
                            rectLeft = rectLeft + oneMoveX;
                            rectTop = rectTop + oneMoveY;
                        } else {
                            //点击中间部分
                            rectLeft = rectLeft + oneMoveX;
                            rectTop = rectTop + oneMoveX / 2;
                            rectBottom = rectBottom - oneMoveX / 2;
                        }
                    } else {
                        //点击的水平中间部分
                        if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                            //点击的下部分
                            rectLeft = rectLeft - oneMoveY / 2;
                            rectRight = rectRight + oneMoveY / 2;
                            rectBottom = rectBottom + oneMoveY;

                        } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                            //点击的上部分
                            rectTop = rectTop + oneMoveY;
                            rectLeft = rectLeft + oneMoveY / 2;
                            rectRight = rectRight - oneMoveY / 2;
                        }
                    }
                }
                oneDownX = (int) (event.getX() + 0.5f);
                oneDownY = (int) (event.getY() + 0.5f);
                break;
            case MotionEvent.ACTION_UP:
                isDrawGridLines = false;
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
        rectF = new RectF(rect);
        drawClipArea(canvas, rectF);
        drawBorder(canvas, rectF);
        if (isDrawGridLines) {
            drawGridding(canvas, rectF);
        }
    }


    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(0, 0, viewWidth, viewHeight, null);
        } else {
            canvas.saveLayer(0, 0, viewWidth, viewHeight, null, Canvas.ALL_SAVE_FLAG);
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
        float leftTopPts[] = {left, top + clipBorderLength, left, top, left, top, left + clipBorderLength, top};
        canvas.drawLines(leftTopPts, borderPaint);
        float rightTopPts[] = {right - clipBorderLength, top, right, top, right, top, right, top + clipBorderLength};
        canvas.drawLines(rightTopPts, borderPaint);
        float leftBottomPts[] = {left, bottom - clipBorderLength, left, bottom, left, bottom, left + clipBorderLength, bottom};
        canvas.drawLines(leftBottomPts, borderPaint);
        float rightBottomPts[] = {right - clipBorderLength, bottom, right, bottom, right, bottom, right, bottom - clipBorderLength};
        canvas.drawLines(rightBottomPts, borderPaint);
    }

    /**
     * 绘制网格
     */
    private void drawGridding(Canvas canvas, RectF rectF) {
        Paint gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(Color.WHITE);
        //Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        // gridPaint.setXfermode(xfermode);
        float left = rectF.left;
        float right = rectF.right;
        float top = rectF.top;
        float bottom = rectF.bottom;
        float lineTop[] = {left, (bottom - top) / 3 + top, right, (bottom - top) / 3 + top};
        canvas.drawLines(lineTop, gridPaint);
        float lineBottom[] = {left, bottom - (bottom - top) / 3, right, bottom - (bottom - top) / 3,};
        canvas.drawLines(lineBottom, gridPaint);
        float lineLeft[] = {(right - left) / 3 + left, top, (right - left) / 3 + left, bottom};
        canvas.drawLines(lineLeft, gridPaint);
        float lineRight[] = {right - (right - left) / 3, top, right - (right - left) / 3, bottom};
        canvas.drawLines(lineRight, gridPaint);
    }

    /**
     * dp转px
     */
    private int dp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }

}
