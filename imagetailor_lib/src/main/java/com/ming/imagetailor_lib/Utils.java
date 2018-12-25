package com.ming.imagetailor_lib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 工具类
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/22 17:14
 */
public class Utils {
    /**
     * 兼容 Android N，Intent中不能使用 file:///*
     *
     * @param context
     * @param uri
     * @return
     */
    public static Uri getIntentUri(Context context, Uri uri) {
        //support android N+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getContentUri(context, uri);
        } else {
            return uri;
        }
    }

    private static Uri getContentUri(Context context, Uri fileUri) {
        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName(), new File(fileUri.getPath()));
        return uri;
    }
    /*---------------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * 解析以什么方式获取的图片，并返回对应的Uri
     *
     * @param useCamera
     * @param data
     * @return
     */
    public static Uri getPickImageResultUri(UseCamera useCamera, Intent data) {

        boolean isCamera = true;
        Uri uri = null;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        uri=isCamera || data.getData() == null ? useCamera.getImageUri() : data.getData();
        return uri;
    }
}
