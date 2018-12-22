package com.ming.imagetailor_lib;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * 权限检测工具
 * ===================
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/19 11:32
 */

public class AccessPermissionUtil {

    /**
     * 使用系统相机请求代码
     */
    public static final int CAMERA_PERMISSIONS_REQUEST_CODE = 2011;
    /**
     * 向SD中写入数据权限请求代码
     */
   // public static final int WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST_CODE = 2012;

    /**
     * ---------------------------------------------------
     */
    private Activity activity;
    private RequestPerssionCallBack callBack;
    private int PERMISSIONS_REQUEST_CODE;

    public AccessPermissionUtil(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取单个权限
     *
     * @param permission
     * @param callBack
     */
    public void checkPermission(int permission, RequestPerssionCallBack callBack) {
        this.callBack = callBack;
        String per = null;
        switch (permission) {
            case CAMERA_PERMISSIONS_REQUEST_CODE://使用系统相机权限
                per = Manifest.permission.CAMERA;
                PERMISSIONS_REQUEST_CODE = CAMERA_PERMISSIONS_REQUEST_CODE;
                break;
        }
        if (per != null) {
            if (ContextCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{per}, PERMISSIONS_REQUEST_CODE);
            } else {
                callBack.onPermissionAllow(PERMISSIONS_REQUEST_CODE, new String[]{per});
            }
        }
    }


    /**
     * 获取权限返回回执
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callBack.onPermissionAllow(requestCode, permissions);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                callBack.onPermissionDenied(requestCode, permissions);
            } else {
                //用户点击不再提示权限，获取权限窗口没有展示
                showAlert();
            }
        }
    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.create();
        dialog.setTitle("权限申请");
        dialog.setMessage("在  权限  中开启相机权限，以正常使用拍照功能");
        dialog.setCancelable(false);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xFF9B9B9B);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xFF27BC02);
    }

    public interface RequestPerssionCallBack {
        /**
         * 获取权限失败
         */
        void onPermissionDenied(int requestCode, String[] permissions);

        /**
         * 获取权限成功
         */
        void onPermissionAllow(int requestCode, String[] permissions);
    }
}
