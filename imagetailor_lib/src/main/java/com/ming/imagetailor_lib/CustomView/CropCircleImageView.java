package com.ming.imagetailor_lib.CustomView;

import android.animation.ValueAnimator;
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
    private float viewWidth;
    private float viewHeight;
    /*第一点按下的位置*/
    private float oneDownX;
    private float oneDownY;
    /*第一点抬起的位置*/
    private float oneUpX;
    private float oneUpY;
    /*图片初始位置属性*/
    RectF bitmapInitRect;
    /*图片初始位置属性*/
    RectF bitmapRectF;
    /*第一点滑动的距离*/
    private float oneMoveY;
    private float oneMoveX;
    /*第一点滑动的当前位置*/
    private float oneNowMoveY;
    private float oneNowMoveX;
    //裁剪区域初始边距
    private float initMargin = dp2px(40);
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

    //四角边距
    private int clipBorderCornerMargin = dp2px(10);
    //是否是缩放 true：缩放 false：移动
    private boolean isScale;
    //裁剪区最小尺寸
    private float clipMinSize = (clipBorderCornerMargin + clipBorderLength) * 2 + 2;
    //是否是第一次初始化
    private boolean isInit = true;

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

    /**
     * 图片缩放
     */
    private float bitmapMinScaleRatio;//图片最小缩放比例
    private float bitmapMaxScaleRatio = 2f;//图片最大缩放比例
    private float bitmapScaleRatio;//图片缩放比例
    //裁剪区域矩形
    private RectF clipRectF;
    //裁剪活动区域矩形
    private RectF clipEventRectF;
    //放大临界值
    private float magnifyCritical;
    //缩小临界值
    private float shrinkCritical;

    private Matrix matrix;
    Drawable drawable = null;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private ValueAnimator animator;
    private Matrix clipMatrix;

    public CropCircleImageView(Context context) {
        this(context, null);
    }

    public CropCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        clipEventRectF = new RectF();
        clipRectF = new RectF();
        bitmapInitRect = new RectF();
        bitmapRectF = new RectF();
        animator = new ValueAnimator();
        clipMatrix = new Matrix();
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
                isScale = clipRectF.contains(oneDownX, oneDownY);
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
                oneUpX = event.getX();
                oneUpY = event.getY();
                if (!isScale) {
                    Zoom();
                }
                break;
        }
        invalidate();//重绘
        return true;
    }

    /**
     * 缩放--图片与裁剪区域 计算
     */
    private void Zoom() {

        liratio = 1f;
        float scale = 0;
        if (clipRectF.width() < magnifyCritical) {
            scale = magnifyCritical / clipRectF.width();
        }
        if (clipRectF.width() > shrinkCritical) {
            scale = shrinkCritical / clipRectF.width();
        }
        animator.setFloatValues(1, scale);
        animator.setDuration(500);
        animator.setRepeatCount(0);
        if (animatorUpdateListener == null) {
            animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

                private float py;
                private float px;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float currentValue = (float) animation.getAnimatedValue();
                    switch (scaleType) {
                        case SCALE_LEFTBOTTOM:

                            px = clipRectF.right;
                            py = clipRectF.top;
                            break;
                        case SCALE_LEFTTOP:

                            px = clipRectF.right;
                            py = clipRectF.bottom;
                            break;
                        case SCALE_RIGHTTOP:

                            px = clipRectF.left;
                            py = clipRectF.bottom;
                            break;
                        case SCALE_RIGHTBOTTOM:

                            px = clipRectF.left;
                            py = clipRectF.top;
                            break;
                        case SCALE_LEFT:

                            px = clipRectF.right;
                            py = (clipRectF.bottom - clipRectF.top) / 2 + clipRectF.top;
                            break;
                    }
                    float scaleRatio = currentValue / liratio;
                    liratio = currentValue;
                    clipMatrix.setScale(scaleRatio, scaleRatio, px, py);
                    clipMatrix.mapRect(clipRectF);
                    bitmapZoom(currentValue, px, py);
                    invalidate();//重绘
                }
            };
        }
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();
    }

    /**
     * 图片缩放
     */
    float liratio = 1f;

    private void bitmapZoom(float scale, float px, float py) {
        float scaleRatio = scale / liratio;
        liratio = scale;
        matrix.postScale(scaleRatio, scaleRatio, px, py);
        bitmapRectF.set(0f, 0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(bitmapRectF);
        if (bitmapRectF.contains(0, 0, viewWidth, viewHeight)) {
            clipEventRectF.set(0, 0, viewWidth, viewHeight);
        } else if (bitmapRectF.width() > viewWidth) {
            clipEventRectF.set(0, bitmapRectF.top, viewWidth, bitmapRectF.bottom);
        } else if (bitmapRectF.height() > viewHeight) {
            clipEventRectF.set(bitmapRectF.left, 0, bitmapRectF.right, viewHeight);
        } else {
            clipEventRectF.set(bitmapRectF);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInit) {
            //获取初始宽度
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            //获取初始高度
            viewHeight = MeasureSpec.getSize(heightMeasureSpec);
            magnifyCritical = viewWidth / 2;
            shrinkCritical = viewWidth / 2 + dp2px(10);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取图片信息
        if (isInit) {
            matrix = getImageMatrix();
            if (drawable == null) {
                drawable = getDrawable();
            }
            bitmapInitRect.set(0f, 0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(bitmapInitRect);
            clipEventRectF.set(bitmapInitRect);
            bitmapRectF.set(bitmapInitRect);
            bitmapMinScaleRatio = (bitmapInitRect.right - bitmapInitRect.left) / (drawable.getBounds().right - drawable.getBounds().left);
            //初始化裁剪区域
            initClip();
            isInit = false;
        }

        drawBackground(canvas);
        drawClipArea(canvas, clipRectF);
        drawBorder(canvas, clipRectF);
        drawNook(canvas, clipRectF);
        if (NOWSTATE != ACTIONUP) {
            drawGridding(canvas, clipRectF);
        }
    }

    /**
     * 初始化裁剪区
     */
    private void initClip() {
        //初始化裁剪区位置
        if (isConstant) {
            ratio = widthScale / heightScale;
        } else {
            ratio = 1f;
        }
        //宽大于高
        clipRectF.left = initMargin;
        clipRectF.right = viewWidth - clipRectF.left;
        if (clipRectF.right - clipRectF.left > bitmapInitRect.right - bitmapInitRect.left) {
            clipRectF.left = bitmapInitRect.left;
            clipRectF.right = bitmapInitRect.right;
        }
        float clipHeight = (clipRectF.right - clipRectF.left) / ratio;//裁剪区高度
        clipRectF.top = (viewHeight - clipHeight) / 2;
        clipRectF.bottom = viewHeight - clipRectF.top;
        if (clipRectF.bottom - clipRectF.top > bitmapInitRect.bottom - bitmapInitRect.top) {
            clipRectF.top = bitmapInitRect.top;
            clipRectF.bottom = bitmapInitRect.bottom;
            float clipWidth = (clipRectF.bottom - clipRectF.top) * ratio;//裁剪去宽度
            clipRectF.left = (viewWidth - clipWidth) / 2;
            clipRectF.right = viewWidth - clipRectF.left;
        }
    }

    /**
     * 根据手指动作调整裁剪区
     */
    private void adjustDraw() {
        if (isScale) {
            //移动
            if (clipEventRectF.top <= clipRectF.top + oneMoveY && clipRectF.bottom + oneMoveY <= clipEventRectF.bottom) {
                clipRectF.top += oneMoveY;
                clipRectF.bottom += oneMoveY;
            }
            if (clipEventRectF.left <= clipRectF.left + oneMoveX && clipRectF.right + oneMoveX <= clipEventRectF.right) {
                clipRectF.left += oneMoveX;
                clipRectF.right += oneMoveX;
            }

            //裁剪区域已在屏幕边缘，移动图片
            if (clipRectF.top + oneMoveY <= 0 || clipRectF.bottom + oneMoveY >= viewHeight) {
                if (bitmapRectF.height() > viewHeight && bitmapRectF.top - oneMoveY <= 0 && bitmapRectF.bottom - oneMoveY >= viewHeight) {
                    matrix.postTranslate(0, -oneMoveY);
                    bitmapRectF.set(0f, 0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    matrix.mapRect(bitmapRectF);
                }
            }
            if (clipRectF.right + oneMoveX >= viewWidth || clipRectF.left + oneMoveX <= 0) {
                if (bitmapRectF.width() > viewWidth && bitmapRectF.left - oneMoveX <= 0 && bitmapRectF.right - oneMoveX >= viewWidth) {
                    matrix.postTranslate(-oneMoveX, 0);
                    bitmapRectF.set(0f, 0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    matrix.mapRect(bitmapRectF);
                }
            }
        } else {
            //缩放
            switch (scaleType) {
                case SCALE_LEFTBOTTOM://缩放左下角
                    if (isConstant) {
                        //固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX * ratio && clipRectF.bottom - oneMoveX * ratio <= bitmapInitRect.bottom
                                && clipRectF.right - (clipRectF.left + oneMoveX) >= clipMinSize && clipRectF.bottom - oneMoveX - clipRectF.top >= clipMinSize) {
                            clipRectF.left += oneMoveX * ratio;
                            clipRectF.bottom -= oneMoveX;
                        }
                    } else {
                        //不固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX && clipRectF.bottom + oneMoveY <= bitmapInitRect.bottom
                                && clipRectF.right - (clipRectF.left + oneMoveX) >= clipMinSize && clipRectF.bottom + oneMoveY - clipRectF.top >= clipMinSize) {
                            clipRectF.left += oneMoveX;
                            clipRectF.bottom += oneMoveY;
                        }
                    }
                    break;
                case SCALE_LEFTTOP://缩放左上角
                    if (isConstant) {
                        //固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX * ratio && bitmapInitRect.top <= clipRectF.top + oneMoveX
                                && clipRectF.right - (clipRectF.left + oneMoveX * ratio) >= clipMinSize && clipRectF.bottom - (oneMoveX + clipRectF.top) >= clipMinSize) {
                            clipRectF.left += oneMoveX * ratio;
                            clipRectF.top += oneMoveX;
                        }
                    } else {
                        //不固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX && bitmapInitRect.top <= clipRectF.top + oneMoveY
                                && clipRectF.right - (clipRectF.left + oneMoveX) >= clipMinSize && clipRectF.bottom - (oneMoveY + clipRectF.top) >= clipMinSize) {
                            clipRectF.left += oneMoveX;
                            clipRectF.top += oneMoveY;
                        }
                    }
                    break;
                case SCALE_RIGHTTOP://缩放右上角
                    if (isConstant) {
                        //固定比例
                        if (clipRectF.right + oneMoveX * ratio <= bitmapInitRect.right && bitmapInitRect.top <= clipRectF.top - oneMoveX
                                && (clipRectF.right + oneMoveX * ratio) - clipRectF.left >= clipMinSize && clipRectF.bottom - (clipRectF.top - oneMoveX) >= clipMinSize) {
                            clipRectF.right += oneMoveX * ratio;
                            clipRectF.top -= oneMoveX;
                        }
                    } else {
                        //不固定比例
                        if (clipRectF.right + oneMoveX <= bitmapInitRect.right && bitmapInitRect.top <= clipRectF.top + oneMoveY
                                && (clipRectF.right + oneMoveX) - clipRectF.left >= clipMinSize && clipRectF.bottom - (clipRectF.top + oneMoveY) >= clipMinSize) {
                            clipRectF.right += oneMoveX;
                            clipRectF.top += oneMoveY;
                        }
                    }
                    break;
                case SCALE_RIGHTBOTTOM://缩放右下角
                    if (isConstant) {
                        //固定比例
                        if (clipRectF.right + oneMoveX * ratio <= bitmapInitRect.right && clipRectF.bottom + oneMoveX <= bitmapInitRect.bottom
                                && (clipRectF.right + oneMoveX * ratio) - clipRectF.left >= clipMinSize && (clipRectF.bottom + oneMoveX) - clipRectF.top >= clipMinSize) {
                            clipRectF.right += oneMoveX * ratio;
                            clipRectF.bottom += oneMoveX;
                        }
                    } else {
                        //不固定比例
                        if (clipRectF.right + oneMoveX <= bitmapInitRect.right && clipRectF.bottom + oneMoveY <= bitmapInitRect.bottom
                                && (clipRectF.right + oneMoveX) - clipRectF.left >= clipMinSize && (clipRectF.bottom + oneMoveY) - clipRectF.top >= clipMinSize) {
                            clipRectF.right += oneMoveX;
                            clipRectF.bottom += oneMoveY;
                        }
                    }
                    break;
                case SCALE_LEFT://缩放左边
                    if (isConstant) {
                        //固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX * ratio && bitmapInitRect.top <= clipRectF.top + oneMoveX / 2 && clipRectF.bottom - oneMoveX / 2 <= bitmapInitRect.bottom
                                && clipRectF.right - (clipRectF.left + oneMoveX * ratio) >= clipMinSize && (clipRectF.bottom - oneMoveX / 2) - (clipRectF.top + oneMoveX / 2) >= clipMinSize) {
                            clipRectF.left += oneMoveX * ratio;
                            clipRectF.top += oneMoveX / 2;
                            clipRectF.bottom -= oneMoveX / 2;
                        }
                    } else {
                        //不固定比例
                        if (bitmapInitRect.left <= clipRectF.left + oneMoveX
                                && clipRectF.right - (clipRectF.left + oneMoveX) >= clipMinSize)
                            clipRectF.left += oneMoveX;
                    }
                    break;
                case SCALE_TOP://缩放上边
                    if (isConstant) {
                        //固定比例
                        if (clipRectF.right - (oneMoveY * ratio) / 2 <= bitmapInitRect.right && bitmapInitRect.top <= clipRectF.top + oneMoveY && bitmapInitRect.left <= clipRectF.left + (oneMoveY * ratio) / 2
                                && (clipRectF.right - (oneMoveY * ratio) / 2) - (clipRectF.left + (oneMoveY * ratio) / 2) >= clipMinSize && clipRectF.bottom - (clipRectF.top + oneMoveY) >= clipMinSize) {
                            clipRectF.right -= (oneMoveY * ratio) / 2;
                            clipRectF.top += oneMoveY;
                            clipRectF.left += (oneMoveY * ratio) / 2;
                        }
                    } else {
                        //不固定比例
                        if (bitmapInitRect.top <= clipRectF.top + oneMoveY
                                && clipRectF.bottom - (clipRectF.top + oneMoveY) >= clipMinSize)
                            clipRectF.top += oneMoveY;
                    }
                    break;
                case SCALE_RIGHT://缩放右边
                    if (isConstant) {
                        //固定比例
                        if (clipRectF.right + oneMoveX * ratio <= bitmapInitRect.right && clipRectF.bottom + oneMoveX / 2 <= bitmapInitRect.bottom && bitmapInitRect.top <= clipRectF.top - oneMoveX / 2
                                && (clipRectF.right + oneMoveX * ratio) - clipRectF.left >= clipMinSize && (clipRectF.bottom + oneMoveX / 2) - (clipRectF.top - oneMoveX / 2) >= clipMinSize) {
                            clipRectF.right += oneMoveX * ratio;
                            clipRectF.bottom += oneMoveX / 2;
                            clipRectF.top -= oneMoveX / 2;
                        }
                    } else {
                        //不固定比例
                        if (clipRectF.right + oneMoveX <= bitmapInitRect.right
                                && (clipRectF.right + oneMoveX) - clipRectF.left >= clipMinSize)
                            clipRectF.right += oneMoveX;
                    }
                    break;
                case SCALE_BOTTOM://缩放下边
                    if (isConstant) {
                        //固定比例
                        if (clipRectF.right + (oneMoveY * ratio) / 2 <= bitmapInitRect.right && clipRectF.bottom + oneMoveY <= bitmapInitRect.bottom && bitmapInitRect.left <= clipRectF.left - (oneMoveY * ratio) / 2
                                && (clipRectF.right + (oneMoveY * ratio) / 2) - (clipRectF.left - (oneMoveY * ratio) / 2) >= clipMinSize && (clipRectF.bottom + oneMoveY) - clipRectF.top >= clipMinSize) {
                            clipRectF.right += (oneMoveY * ratio) / 2;
                            clipRectF.bottom += oneMoveY;
                            clipRectF.left -= (oneMoveY * ratio) / 2;
                        }
                    } else {
                        //不固定比例
                        if (clipRectF.bottom + oneMoveY <= bitmapInitRect.bottom
                                && (clipRectF.bottom + oneMoveY) - clipRectF.top >= clipMinSize)
                            clipRectF.bottom += oneMoveY;
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
        if (oneDownX > clipRectF.right - (clipRectF.right - clipRectF.left) / 3) {
            //点击的右部分
            if (oneDownY > clipRectF.bottom - (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的下部分
                type = SCALE_RIGHTBOTTOM;
            } else if (oneDownY < clipRectF.top + (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的上部分
                type = SCALE_RIGHTTOP;
            } else {
                //点击中间部分
                type = SCALE_RIGHT;
            }
        } else if (oneDownX < clipRectF.left + (clipRectF.right - clipRectF.left) / 3) {
            //点击的左部分
            if (oneDownY > clipRectF.bottom - (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的下部分
                type = SCALE_LEFTBOTTOM;
            } else if (oneDownY < clipRectF.top + (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的上部分
                type = SCALE_LEFTTOP;
            } else {
                //点击中间部分
                type = SCALE_LEFT;
            }
        } else {
            //点击的水平中间部分
            if (oneDownY > clipRectF.bottom - (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的下部分
                type = SCALE_BOTTOM;
            } else if (oneDownY < clipRectF.top + (clipRectF.bottom - clipRectF.top) / 3) {
                //点击的上部分
                type = SCALE_TOP;
            }
        }
        return type;
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
                canvas.drawOval(new RectF(rectF.left + clipBorderWidth / 2, rectF.top + clipBorderWidth / 2, rectF.right - clipBorderWidth / 2, clipRectF.bottom - clipBorderWidth / 2), borderPaint);
                break;
            case TYPEFIXEDOSQUARE:
            case TYPESQUARE://矩形裁剪
                canvas.drawRect(new RectF(rectF.left + clipBorderWidth / 2, rectF.top + clipBorderWidth / 2, rectF.right - clipBorderWidth / 2, clipRectF.bottom - clipBorderWidth / 2), borderPaint);
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

    /**
     * dp转px
     */
    private int dp2px(float dpValues) {
        dpValues = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValues, getResources().getDisplayMetrics());
        return (int) (dpValues + 0.5f);
    }



















    /*-----------------------------------------------------------*/


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


}
