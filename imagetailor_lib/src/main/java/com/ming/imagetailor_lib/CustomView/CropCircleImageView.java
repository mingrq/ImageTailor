package com.ming.imagetailor_lib.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

/**
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/27 14:13
 */
public class CropCircleImageView extends AppCompatImageView {

    private Context context;
    /**
     * 裁剪类型
     */
    private int clipType;
    //固定比例椭圆
    public static final int TYPEFIXEDOVAL = 0x0001;
    //固定比例矩形
    public static final int TYPEFIXEDOSQUARE = 0x0002;
    //矩形
    public static final int TYPESQUARE = 0x0003;
    //椭圆
    public static final int TYPEOVAL = 0x0004;


    //蒙版颜色
    private int backgroundColor = 0xa8000000;
    //是否是固定比例的
    private boolean isConstant = true;
    //设置宽度比例
    private float widthScale = 1f;
    //设置高度比例
    private float heightScale = 1f;
    //裁剪边框四角长度
    private int clipBorderLength = dp2px(14);
    //裁剪框边框宽度
    private int clipBorderWidth = dp2px(3);
    //裁剪框颜色
    private int clipBorderColor = 0xAFFFFFFF;
    //裁剪框四角颜色
    private int clipBorderCornerColor = 0xFFFFFFFF;
    //裁剪框四角宽度
    private int clipBorderCornerWidth = dp2px(2);
    //裁剪框线格颜色
    private int clipBorderGridColor = 0xFFFFFFFF;
    /*控件属性*/
    private int viewWidth = 1;
    private int viewHeight = 1;
    /*第一点按下的位置*/
    private float oneDownX;
    private float oneDownY;
    /*裁剪区属性*/
    private float rectLeft;
    private float rectRight;
    private float rectTop;
    private float rectBottom;
    /*第一点滑动的距离*/
    private float oneMoveY;
    private float oneMoveX;
    /*第一点滑动的当前位置*/
    private float oneNowMoveY;
    private float oneNowMoveX;
    //裁剪区域初始边距
    private int initMargin = dp2px(40);
    /**
     * 滑动状态
     */
    private final int ACTIONUP = 0;//抬起状态
    private final int ACTIONDOWN = 1;//按下状态
    private final int ACTIONMOVE = 2;//滑动状态
    /*当前状态*/
    private int NOWSTATE = ACTIONUP;
    //宽高比
    private float ratio;
    //比率模式
    private int ratioType;
    private final int RATIO_WIDTH = 0x000482;//宽大于高
    private final int RATIO_HEIGHT = 0x000483;//宽小于高
    private final int RATIO_EQUAL = 0x000484;//宽等于高
    //四角边距
    private int clipBorderCornerMargin = dp2px(10);
    //是否是缩放 true：缩放 false：移动
    private boolean isScale;

    /**
     * 缩放模式
     */
    private int scaleType;
    private final int SCALE_LEFTBOTTOM = 0x000412;//缩放左下角
    private final int SCALE_LEFTTOP = 0x000874;//缩放左上角
    private final int SCALE_RIGHTTOP = 0x000698;//缩放右上角
    private final int SCALE_RIGHTBOTTOM = 0x000236;//缩放右下角
    private final int SCALE_LEFT = 0x000004;//缩放左边
    private final int SCALE_TOP = 0x000008;//缩放上边
    private final int SCALE_RIGHT = 0x000006;//缩放右边
    private final int SCALE_BOTTOM = 0x000002;//缩放下边
    private int bitmapWidth;//图片宽度
    private int bitmapHeight;//图片高度


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
     * 设置宽高比例
     *
     * @param widthScale
     * @param heightScale
     */
    public void setWidthHeightScale(float widthScale, float heightScale) {
        this.widthScale = widthScale;
        this.heightScale = heightScale;
    }

    /**
     * 设置裁剪类型
     *
     * @param clipType
     */
    public void setClipType(int clipType) {
        this.clipType = clipType;
        //设置是否等比例
        switch (clipType) {
            case TYPEFIXEDOSQUARE:
            case TYPEFIXEDOVAL:
                isConstant = true;
                break;
            case TYPEOVAL:
            case TYPESQUARE:
                isConstant = false;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                NOWSTATE = ACTIONDOWN;
                oneNowMoveX = oneDownX = event.getX();
                oneNowMoveY = oneDownY = event.getY();
                isScale = actionIsScale();
                //获取缩放缩放模式
                scaleType = actionScaleType();
                break;
            case MotionEvent.ACTION_MOVE:
                NOWSTATE = ACTIONMOVE;
                oneMoveX = event.getX() - oneNowMoveX;
                oneMoveY = event.getY() - oneNowMoveY;
                oneNowMoveX = event.getX();
                oneNowMoveY = event.getY();
                adjustDraw();
                break;
            case MotionEvent.ACTION_UP:
                NOWSTATE = ACTIONUP;
                break;
        }
        invalidate();//重绘
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获取图片信息
         */
        Bitmap bitmap = getBitmap(getDrawable());
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        //获取初始宽度
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        //获取初始高度
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        //初始化裁剪区位置
        if (isConstant) {
            //固定比例
            if (widthScale - heightScale > 0) {
                ratioType = RATIO_WIDTH;
                ratio = widthScale / heightScale;
                //宽大于高
                rectLeft = initMargin;
                rectRight = viewWidth - rectLeft;
                float clipHeight = (rectRight - rectLeft) / ratio;//裁剪区高度
                rectTop = (viewHeight - clipHeight) / 2;
                rectBottom = viewHeight - rectTop;
            } else if (widthScale - heightScale < 0) {
                ratioType = RATIO_HEIGHT;
                ratio = heightScale / widthScale;
                //宽小于高
                float clipHeight = viewHeight / 2;//裁剪区高度
                rectTop = (viewHeight - clipHeight) / 2;
                rectBottom = viewHeight - rectTop;
                float clipWidth = clipHeight / ratio;
                rectLeft = (viewWidth - clipWidth) / 2;
                rectRight = viewWidth - rectLeft;
            }
        }
        if (!isConstant || widthScale - heightScale == 0) {
            //不固定比例或宽高相等
            ratioType = RATIO_EQUAL;
            rectLeft = initMargin;
            rectRight = viewWidth - rectLeft;
            rectTop = (viewHeight - (rectRight - rectLeft)) / 2;
            rectBottom = viewHeight - rectTop;
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
        RectF rectF = new RectF(rectLeft, rectTop, rectRight, rectBottom);
        drawBackground(canvas);
        drawClipArea(canvas, rectF);
        drawBorder(canvas, rectF);
        drawNook(canvas, rectF);
        if (NOWSTATE != ACTIONUP) {
            drawGridding(canvas, rectF);
        }
    }

    private void test(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rectF, paint);
    }

    /**
     * 根据手指动作调整裁剪区
     */
    private void adjustDraw() {
        if (!isScale) {
            //移动
            rectTop += oneMoveY;
            rectBottom += oneMoveY;
            rectLeft += oneMoveX;
            rectRight += oneMoveX;
        } else {
            //缩放
            switch (scaleType) {
                case SCALE_LEFTBOTTOM://缩放左下角
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectLeft += oneMoveX;
                                rectBottom -= oneMoveX;
                                break;
                            case RATIO_HEIGHT:
                                rectLeft += oneMoveX;
                                rectBottom -= oneMoveX * ratio;
                                break;
                            case RATIO_WIDTH:
                                rectLeft += oneMoveX * ratio;
                                rectBottom -= oneMoveX;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectLeft += oneMoveX;
                        rectBottom += oneMoveY;
                    }
                    break;
                case SCALE_LEFTTOP://缩放左上角
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectLeft += oneMoveX;
                                rectTop += oneMoveX;
                                break;
                            case RATIO_HEIGHT:
                                rectLeft += oneMoveX;
                                rectTop += oneMoveX * ratio;
                                break;
                            case RATIO_WIDTH:
                                rectLeft += oneMoveX * ratio;
                                rectTop += oneMoveX;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectLeft += oneMoveX;
                        rectTop += oneMoveY;
                    }
                    break;
                case SCALE_RIGHTTOP://缩放右上角
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectRight += oneMoveX;
                                rectTop -= oneMoveX;
                                break;
                            case RATIO_HEIGHT:
                                rectRight += oneMoveX;
                                rectTop -= oneMoveX * ratio;
                                break;
                            case RATIO_WIDTH:
                                rectRight += oneMoveX * ratio;
                                rectTop -= oneMoveX;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectRight += oneMoveX;
                        rectTop += oneMoveY;
                    }
                    break;
                case SCALE_RIGHTBOTTOM://缩放右下角
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectRight += oneMoveX;
                                rectBottom += oneMoveX;
                                break;
                            case RATIO_HEIGHT:
                                rectRight += oneMoveX;
                                rectBottom += oneMoveX * ratio;
                                break;
                            case RATIO_WIDTH:
                                rectRight += oneMoveX * ratio;
                                rectBottom += oneMoveX;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectRight += oneMoveX;
                        rectBottom += oneMoveY;
                    }
                    break;
                case SCALE_LEFT://缩放左边
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectLeft += oneMoveX;
                                rectTop += oneMoveX / 2;
                                rectBottom -= oneMoveX / 2;
                                break;
                            case RATIO_HEIGHT:
                                rectLeft += oneMoveX;
                                rectTop += (oneMoveX * ratio) / 2;
                                rectBottom -= (oneMoveX * ratio) / 2;
                                break;
                            case RATIO_WIDTH:
                                rectLeft += oneMoveX * ratio;
                                rectTop += oneMoveX / 2;
                                rectBottom -= oneMoveX / 2;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectLeft += oneMoveX;
                    }
                    break;
                case SCALE_TOP://缩放上边
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectRight -= oneMoveY / 2;
                                rectTop += oneMoveY;
                                rectLeft += oneMoveY / 2;
                                break;
                            case RATIO_HEIGHT:
                                rectRight -= oneMoveY / 2;
                                rectTop += oneMoveY * ratio;
                                rectLeft += oneMoveY / 2;
                                break;
                            case RATIO_WIDTH:
                                rectRight -= (oneMoveY * ratio) / 2;
                                rectTop += oneMoveY;
                                rectLeft += (oneMoveY * ratio) / 2;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectTop += oneMoveY;
                    }
                    break;
                case SCALE_RIGHT://缩放右边
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectRight += oneMoveX;
                                rectBottom += oneMoveX / 2;
                                rectTop -= oneMoveX / 2;
                                break;
                            case RATIO_HEIGHT:
                                rectRight += oneMoveX;
                                rectBottom += (oneMoveX * ratio) / 2;
                                rectTop -= (oneMoveX * ratio) / 2;
                                break;
                            case RATIO_WIDTH:
                                rectRight += oneMoveX * ratio;
                                rectBottom += oneMoveX / 2;
                                rectTop -= oneMoveX / 2;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectRight += oneMoveX;
                    }
                    break;
                case SCALE_BOTTOM://缩放下边
                    if (isConstant) {
                        //固定比例
                        switch (ratioType) {
                            case RATIO_EQUAL://宽高相等
                                rectRight += oneMoveY / 2;
                                rectBottom += oneMoveY;
                                rectLeft -= oneMoveY / 2;
                                break;
                            case RATIO_HEIGHT:
                                rectRight += oneMoveY / 2;
                                rectBottom += oneMoveY * ratio;
                                rectLeft -= oneMoveY / 2;
                                break;
                            case RATIO_WIDTH:
                                rectRight += (oneMoveY * ratio) / 2;
                                rectBottom += oneMoveY;
                                rectLeft -= (oneMoveY * ratio) / 2;
                                break;
                        }
                    } else {
                        //不固定比例
                        rectBottom += oneMoveY;
                    }
                    break;
            }
        }
    }

    /**
     * 获取缩放缩放模式
     *
     * @return
     */
    private int actionScaleType() {
        int type = 0;
        if (oneDownX > rectRight - (rectRight - rectLeft) / 3) {
            //点击的右部分
            if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                //点击的下部分
                type = SCALE_RIGHTBOTTOM;
            } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                //点击的上部分
                type = SCALE_RIGHTTOP;
            } else {
                //点击中间部分
                type = SCALE_RIGHT;
            }
        } else if (oneDownX < rectLeft + (rectRight - rectLeft) / 3) {
            //点击的左部分
            if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                //点击的下部分
                type = SCALE_LEFTBOTTOM;
            } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                //点击的上部分
                type = SCALE_LEFTTOP;
            } else {
                //点击中间部分
                type = SCALE_LEFT;
            }
        } else {
            //点击的水平中间部分
            if (oneDownY > rectBottom - (rectBottom - rectTop) / 3) {
                //点击的下部分
                type = SCALE_BOTTOM;
            } else if (oneDownY < rectTop + (rectBottom - rectTop) / 3) {
                //点击的上部分
                type = SCALE_TOP;
            }
        }
        return type;
    }

    /**
     * 判断按下的位置是缩放还是移动
     *
     * @return
     */
    private boolean actionIsScale() {
        return oneDownX > rectLeft && oneDownX < rectRight && oneDownY > rectTop && oneDownY < rectBottom ? false : true;
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
    private void drawClipArea(Canvas canvas, RectF rectF) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        paint.setXfermode(xfermode);
        switch (clipType) {
            case TYPEFIXEDOVAL:
            case TYPEOVAL: //椭圆裁剪
                canvas.drawOval(rectF, paint);
                break;
            case TYPEFIXEDOSQUARE:
            case TYPESQUARE://矩形裁剪
                canvas.drawRect(rectF, paint);
                break;
        }

    }

    /**
     * 绘制边框
     */
    private void drawBorder(Canvas canvas, RectF rectF) {
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(clipBorderColor);
        borderPaint.setStrokeWidth(clipBorderWidth);
        borderPaint.setAntiAlias(true);
        switch (clipType) {
            case TYPEFIXEDOVAL:
            case TYPEOVAL: //椭圆裁剪
                canvas.drawOval(new RectF(rectF.left + clipBorderWidth / 2, rectF.top + clipBorderWidth / 2, rectF.right - clipBorderWidth / 2, rectBottom - clipBorderWidth / 2), borderPaint);
                break;
            case TYPEFIXEDOSQUARE:
            case TYPESQUARE://矩形裁剪
                canvas.drawRect(new RectF(rectF.left + clipBorderWidth / 2, rectF.top + clipBorderWidth / 2, rectF.right - clipBorderWidth / 2, rectBottom - clipBorderWidth / 2), borderPaint);
                break;
        }
    }

    /**
     * 绘制四角
     *
     * @param canvas
     * @param rectF
     */
    private void drawNook(Canvas canvas, RectF rectF) {
        Paint nookPaint = new Paint();
        nookPaint.setAntiAlias(true);
        nookPaint.setColor(clipBorderCornerColor);
        nookPaint.setStyle(Paint.Style.STROKE);
        nookPaint.setStrokeWidth(clipBorderCornerWidth);
        //绘制四角
        float left = rectF.left + clipBorderCornerMargin;
        float right = rectF.right - clipBorderCornerMargin;
        float top = rectF.top + clipBorderCornerMargin;
        float bottom = rectF.bottom - clipBorderCornerMargin;
        float leftTopPts[] = {left, top + clipBorderLength, left, top, left - clipBorderCornerWidth / 2, top, left + clipBorderLength, top};
        canvas.drawLines(leftTopPts, nookPaint);
        float rightTopPts[] = {right - clipBorderLength, top, right, top, right, top - clipBorderCornerWidth / 2, right, top + clipBorderLength};
        canvas.drawLines(rightTopPts, nookPaint);
        float leftBottomPts[] = {left, bottom - clipBorderLength, left, bottom, left - clipBorderCornerWidth / 2, bottom, left + clipBorderLength, bottom};
        canvas.drawLines(leftBottomPts, nookPaint);
        float rightBottomPts[] = {right - clipBorderLength, bottom, right, bottom, right, bottom + clipBorderCornerWidth / 2, right, bottom - clipBorderLength};
        canvas.drawLines(rightBottomPts, nookPaint);
    }

    /**
     * 绘制网格
     */
    private void drawGridding(Canvas canvas, RectF rectF) {
        float left = rectF.left;
        float right = rectF.right;
        float top = rectF.top;
        float bottom = rectF.bottom;
        Path path = new Path();
        switch (clipType) {
            case TYPEFIXEDOVAL:
            case TYPEOVAL: //椭圆裁剪
                path.addOval(new RectF(left + clipBorderCornerWidth, top + clipBorderCornerWidth, right - clipBorderCornerWidth, bottom - clipBorderCornerWidth), Path.Direction.CW);
                break;
            case TYPEFIXEDOSQUARE:
            case TYPESQUARE://矩形裁剪
                path.addRect(new RectF(left + clipBorderCornerWidth, top + clipBorderCornerWidth, right - clipBorderCornerWidth, bottom - clipBorderCornerWidth), Path.Direction.CW);
                break;
        }
        canvas.clipPath(path);
        Paint gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(clipBorderGridColor);
        float lineTop[] = {left, (bottom - top) / 3 + top, right, (bottom - top) / 3 + top};
        canvas.drawLines(lineTop, gridPaint);
        float lineBottom[] = {left, bottom - (bottom - top) / 3, right, bottom - (bottom - top) / 3,};
        canvas.drawLines(lineBottom, gridPaint);
        float lineLeft[] = {(right - left) / 3 + left, top, (right - left) / 3 + left, bottom};
        canvas.drawLines(lineLeft, gridPaint);
        float lineRight[] = {right - (right - left) / 3, top, right - (right - left) / 3, bottom};
        canvas.drawLines(lineRight, gridPaint);
        canvas.restore();
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
