package com.ming.imagetailor_lib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

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
    ImageView circleImageView;

    String cameraFilePath = Environment.getExternalStorageDirectory().toString();//照片保存路径

    public ImageTailor(Activity activity, ImageView circleImageView) {
        this.activity = activity;
        this.circleImageView = circleImageView;
        accessPermissionUtil = new AccessPermissionUtil(activity);
    }

    /**
     * 开始选择照片
     */
    private void tailor() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startChooser();
        } else {
            accessPermissionUtil.checkPermissions(new AccessPermissionUtil.RequestPerssionCallBack() {
                @Override
                public void onPermissionDenied(int requestCode, String[] permissions) {
                    //拒绝权限获取
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
        useCamera = new UseCamera(activity);
        Intent intent_camera = useCamera.Photograph(cameraFilePath + "/" + System.currentTimeMillis() + ".jpg");
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
     * 设置照片保存路径
     *
     * @param cameraFilePath /storage/emulated/0
     */
    public void setCameraFilePath(String cameraFilePath) {
        this.cameraFilePath = cameraFilePath;
    }

    /**
     * 提交使用选择器选取图片裁剪
     */
    public void commit() {
        tailor();
    }

    /**
     * 使用系统相机获取照片的回执
     *
     * @param requestCode:返回
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String imagePath = Utils.getImagePath(useCamera, data);
                //检测是否有读取sd卡的权限
                accessPermissionUtil.checkPermissions(new AccessPermissionUtil.RequestPerssionCallBack() {
                    @Override
                    public void onPermissionDenied(int requestCode, String[] permissions) {
                        //拒绝权限获取
                    }

                    @Override
                    public void onPermissionAllow(int requestCode, String[] permissions) {
                        //权限获取成功
                        try {
                            FileInputStream fileInputStream = new FileInputStream(imagePath);
                            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                            circleImageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }


}
