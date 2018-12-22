package com.ming.imagetailor_lib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
    private final int PHOTOGRAPH = 0;//拍照并返回照片
    private final int PHOTOGRAPHSAVE = 1;//拍照并将照片保存到本地


    private AccessPermissionUtil permissionUtil;
    private PhotographCallBack callBack;
    private Activity activity;
    private String imagePath;//照片存储路径
    private Intent intent;

    public UseCamera(Activity activity, PhotographCallBack callBack) {
        this.callBack = callBack;
        this.activity = activity;
    }

    /**
     * 开始拍照
     *
     * @return
     */
    private Intent startPhotograph(final int type) {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (type == PHOTOGRAPHSAVE) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        }
        return intent;
    }

    /**
     * ------------------------------------------------------------------------------------
     */


    /**
     * 拍照并返回照片
     *
     * @return 拍照后获取的位图
     */
    public Intent Photograph() {
        return startPhotograph(PHOTOGRAPH);
    }


    /**
     * 拍照并存储到本地
     *
     * @param imagePath 存储地址
     * @return 是否保存成功
     */
    public Intent Photograph(final String imagePath) {
        return startPhotograph(PHOTOGRAPHSAVE);
    }


    /**
     * 使用系统相机获取照片的回执
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                callBack.Success(bitmap);
            } else if (resultCode == RESULT_CANCELED) {
                callBack.Cancel();
            } else {
                callBack.Failure(data);
            }
        }
    }

    /**
     * 使用系统相机的回调
     */
    public interface PhotographCallBack {

        /**
         * 获取照片失败时操作
         */
        void Failure(Intent data);

        /**
         * 取消拍照
         */
        void Cancel();

        /**
         * 获取照片成功时才做
         */
        void Success(Bitmap bitmap);
    }
}
