package com.ming.imagetailor_lib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ming.imagetailor_lib.Activity.CropImageActivity;

import java.io.File;
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
    /**
     * 裁剪类型
     */
    //固定比例椭圆
    public static final int TYPEFIXEDOVAL = 0x0001;
    //固定比例矩形
    public static final int TYPEFIXEDOSQUARE = 0x0002;
    //矩形
    public static final int TYPESQUARE = 0x0003;
    //椭圆
    public static final int TYPEOVAL = 0x0004;


    public static final int PICK_IMAGE_CHOOSER_REQUEST_CODE = 200;
    private Activity activity;
    private final AccessPermissionUtil accessPermissionUtil;
    private UseCamera useCamera;

    String cameraFilePath = Environment.getExternalStorageDirectory().toString();//照片保存路径
    private final ClipInfo clipInfo;

    public ImageTailor(Activity activity) {
        this.activity = activity;
        accessPermissionUtil = new AccessPermissionUtil(activity);
        clipInfo = new ClipInfo();
    }

    /**
     * 设置宽高比例
     *
     * @param widthScale
     * @param heightScale
     */
    public void setWidthHeightScale(float widthScale, float heightScale) {
        clipInfo.setWidthHeightScale(widthScale, heightScale);
    }

    /**
     * 设置裁剪类型
     *
     * @param clipType
     */
    public void setClipType(int clipType) {
        clipInfo.setClipType(clipType);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Uri imageUri = Utils.getPickImageResultUri(useCamera, data);
                final Uri imagepath = handleUri(activity, imageUri);
                //检测是否有读取sd卡的权限
                accessPermissionUtil.checkPermissions(new AccessPermissionUtil.RequestPerssionCallBack() {
                    @Override
                    public void onPermissionDenied(int requestCode, String[] permissions) {
                        //拒绝权限获取
                    }

                    @Override
                    public void onPermissionAllow(int requestCode, String[] permissions) {
                        //权限获取成功
                        Intent intent = new Intent(activity, CropImageActivity.class);
                        intent.setData(imagepath);
                        intent.putExtra("clipInfo", clipInfo);
                        activity.startActivity(intent);
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------------------------------
     */

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
     * 获取图片选择器
     *
     * @return
     */
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
     * 获取content：//格式的uri
     *
     * @param context
     * @param imageUri
     * @return
     */
    private Uri handleUri(Context context, Uri imageUri) {
        if ("content".equals(imageUri.getScheme())) {
            String realPath = getRealPathFromUri(context, imageUri);
            if (!TextUtils.isEmpty(realPath)) {
                return Uri.fromFile(new File(realPath));
            }
        }
        return imageUri;
    }

    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
