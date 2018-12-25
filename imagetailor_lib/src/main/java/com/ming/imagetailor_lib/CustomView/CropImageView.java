package com.ming.imagetailor_lib.CustomView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 *
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/24 16:10
 */
public class CropImageView extends FrameLayout {

    private static final int TYPE_RECTANGLE = 0x45851185;//矩形
    private static final int TYPE_OVAL = 0x48424813;//椭圆

    /**
     * 裁剪方式
     */
    private int cropType = -1;


    public CropImageView(@NonNull Context context) {
        this(context, null);
    }

    public CropImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
