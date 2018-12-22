package com.ming.imagetailor_lib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片自由裁剪工具类
 * =================
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/22 10:16
 */
public class ImageTailor {
    public static final int PICK_IMAGE_CHOOSER_REQUEST_CODE = 200;
    private Activity activity;
    private final AccessPermissionUtil accessPermissionUtil;
    private UseCamera useCamera;

    public ImageTailor(Activity activity) {
        this.activity = activity;
        accessPermissionUtil = new AccessPermissionUtil(activity);
    }

    public void tailor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startChooser();
        } else {
            accessPermissionUtil.checkPermissions(new AccessPermissionUtil.RequestPerssionCallBack() {
                @Override
                public void onPermissionDenied(int requestCode, String[] permissions) {

                }

                @Override
                public void onPermissionAllow(int requestCode, String[] permissions) {
                    //权限获取成功
                    startChooser();
                }
            }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * 开始选择器
     */
    private void startChooser() {
        Intent chooser = startPickImageChooser();
        activity.startActivityForResult(chooser, PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    /**
     * 获取权限返回回执
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        accessPermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected Intent startPickImageChooser() {
        List<Intent> intents = new ArrayList<>();
        SelectImage selectImage = new SelectImage(activity);
        intents.addAll(selectImage.getImageIntent(Intent.ACTION_GET_CONTENT));
        useCamera = new UseCamera(activity, new UseCamera.PhotographCallBack() {
            @Override
            public void Failure(Intent data) {

            }

            @Override
            public void Cancel() {

            }

            @Override
            public void Success(Bitmap bitmap) {

            }
        });
        Intent intent_camera = useCamera.Photograph();
        intents.add(intent_camera);
        Intent target;
        if (intents.isEmpty()) {
            target = new Intent();
        } else {
            target = intents.get(intents.size() - 1);
            intents.remove(intents.size() - 1);
        }
        Intent chooser = Intent.createChooser(target, "打开图片方式");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
        return chooser;
    }

    /**
     * 使用系统相机获取照片的回执
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == UseCamera.CAMERA_PERMISSIONS_REQUEST_CODE) {
            useCamera.onActivityResult(requestCode, resultCode, data);
        } else {

        }
    }
}
