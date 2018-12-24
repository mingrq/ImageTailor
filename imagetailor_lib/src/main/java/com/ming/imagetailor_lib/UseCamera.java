package com.ming.imagetailor_lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * 使用系统相机
 * ==============
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/20 10:41
 */
public class UseCamera {
    public static final int CAMERA_PERMISSIONS_REQUEST_CODE = 2011;
    private String imagePath;//照片存储路径
    private Uri imageUri;//照片存储路径

    private Activity activity;
    private Intent intent;

    public UseCamera(Activity activity) {
        this.activity = activity;
    }


    /**
     * ------------------------------------------------------------------------------------
     */
    /**
     * 获取拍照后照片的Uri
     *
     * @return
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * 拍照并存储到本地
     *
     * @param imagePath 存储地址
     */
    public Intent Photograph(String imagePath) {
        this.imageUri = Utils.getIntentUri(activity, Uri.parse(imagePath));
        this.imagePath = imagePath;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return intent;
    }


}
