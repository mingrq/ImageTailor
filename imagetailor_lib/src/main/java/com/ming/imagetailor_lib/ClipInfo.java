package com.ming.imagetailor_lib;

import java.io.Serializable;

/**
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/28 14:37
 */
public class ClipInfo implements Serializable{
    //设置宽度比例
    private int widthScale;
    //设置高度比例
    private int heightScale;
    //裁剪类型
    private int clipType;

    public int getWidthScale() {
        return widthScale;
    }

    public void setWidthHeightScale(int widthScale, int heightScale) {
        this.widthScale = widthScale;
        this.heightScale = heightScale;
    }

    public int getHeightScale() {
        return heightScale;
    }

    public int getClipType() {
        return clipType;
    }

    public void setClipType(int clipType) {
        this.clipType = clipType;
    }
}
