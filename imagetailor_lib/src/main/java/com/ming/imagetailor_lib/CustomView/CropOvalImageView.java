package com.ming.imagetailor_lib.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/25 15:06
 */
public class CropOvalImageView extends View {

    private int backgroundColor = 0xA0333333;
    private int viewWidth;
    private int viewHeight;


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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
    }


    /**
     * 绘制背景
     */
    private void drawBackground(Canvas canvas) {
        Path path = new Path();
        path.addCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - dp2px(10f), Path.Direction.CW);
        canvas.clipPath(path, Region.Op.XOR);
        canvas.drawColor(backgroundColor);
    }


    /**
     * dp转px
     */
    private int dp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }

    /**
     * sp转px
     */
    private int sp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }
}
