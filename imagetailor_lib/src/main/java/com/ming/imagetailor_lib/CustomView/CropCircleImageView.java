package com.ming.imagetailor_lib.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/27 14:13
 */
public class CropCircleImageView extends AppCompatImageView {

    private Context context;

    //蒙版颜色
    private int backgroundColor = 0xa8000000;
    //是否是固定比例的
    private boolean isConstant = true;
    //设置宽度比例
    private int widthScale = 0;
    //设置高度比例
    private int heightScale = 0;
    //裁剪边框长度
    private int clipBorderLength = dp2px(16);
    //裁剪框边框宽度
    private int clipBorderWidth = dp2px(2);
    //裁剪框颜色
    private int clipBorderColor = 0xffffff;
    //裁剪框四角颜色
    private int clipBorderCornerColor = 0xffffff;
    //裁剪框线格颜色
    private int clipBorderGridColor = 0xffffff;
    /*控件属性*/
    private int viewWidth = 1;
    private int viewHeight = 1;
    /*第一次按下的位置*/
    private int oneDownX;
    private int oneDownY;
    /*裁剪区属性*/
    private int rectLeft;
    private int rectRight;
    private int rectTop;
    private int rectBottom;
    /*第一次滑动的距离*/
    private int oneMoveY;
    private int oneMoveX;
    /*第一次滑动的当前位置*/
    private int oneNowMoveY;
    private int oneNowMoveX;
    //裁剪区域初始边距
    private int initMargin = dp2px(40);
    /**
     * 滑动状态
     */
    private final int ACTIONUP = 0;//抬起状态
    private final int ACTIONDOWN = 1;//按下状态
    private final int ACTIONMOVE = 2;//滑动状态
    /*当前状态*/
    private int NowState = ACTIONUP;


    public CropCircleImageView(Context context) {
        this(context, null);
    }

    public CropCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * @param constant
     */
    public void setConstant(boolean constant) {
        isConstant = constant;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //宽高比
        float scale;
        //获取初始宽度
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        //获取初始高度
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        //初始化裁剪区位置
        if (isConstant) {
            if (widthScale - heightScale > 0) {
                scale = widthScale / heightScale;
                //宽大于高
                rectLeft = initMargin;
                rectRight = viewWidth - initMargin;

            } else if (widthScale - heightScale < 0) {
                scale = heightScale / widthScale;
            } else {
                //宽等于高
                rectLeft = initMargin;
                rectRight = viewWidth - initMargin;
                rectTop = (viewHeight - (rectRight - rectLeft)) / 2;
                rectBottom = viewHeight - rectTop;
            }
        } else {

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      /*  Bitmap bitmap = getBitmap(getDrawable());
        if (bitmap != null) {

        } else {
            super.onDraw(canvas);
        }*/
        /*裁剪区域矩形*/
        Rect rect = new Rect(rectLeft, rectTop, rectRight, rectBottom);
        RectF rectF = new RectF(rect);
        drawBackground(canvas);
        drawOvalClipArea(canvas, rectF);
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
     * 绘制椭圆裁剪区域
     *
     * @param canvas
     */
    private void drawOvalClipArea(Canvas canvas, RectF rectF) {
        if (isConstant) {
            //固定比例
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            paint.setXfermode(xfermode);
            canvas.drawOval(rectF, paint);
        } else {
            //自由裁剪

        }
    }

    private Bitmap getBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof ColorDrawable) {
            Rect rect = drawable.getBounds();
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int color = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                color = ((ColorDrawable) drawable).getColor();
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawARGB(Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
            return bitmap;
        } else {
            return null;
        }
    }


    /**
     * dp转px
     */
    private int dp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }
}
