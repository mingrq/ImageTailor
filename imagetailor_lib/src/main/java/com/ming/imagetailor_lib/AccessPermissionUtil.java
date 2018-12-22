package com.ming.imagetailor_lib;

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

import java.util.ArrayList;
import java.util.List;

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
    protected static final int PERMISSIONS_REQUEST_GROUP_CODE = 1995;


    /**
     * ---------------------------------------------------
     */
    private Activity activity;
    private RequestPerssionCallBack callBack;
    private String dialogTitle = "权限申请";
    private String dialogMessage = "在  权限  中开启相应权限，以正常使用各种功能";
    private String diaBtnPosTit = "去设置";//确定按钮文字
    private String diaBtnNegTit = "取消";//取消按钮文字
    private int diaBtnNegTitCol = 0xFF9B9B9B;//取消按钮文字颜色
    private int diaBtnPosTitCol = 0xFF27BC02;//确定按钮文字颜色

    protected AccessPermissionUtil(Activity activity) {
        this.activity = activity;
    }


    /**
     * 获取权限
     */
    protected void checkPermissions(RequestPerssionCallBack callBack, String... permission) {
        this.callBack = callBack;
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permission.length; i++) {
            String per = permission[i];
            if (per != null) {
                if (ContextCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(per);
                }
            }
        }
        if (permissionList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), PERMISSIONS_REQUEST_GROUP_CODE);
        }else {
            callBack.onPermissionAllow(PERMISSIONS_REQUEST_GROUP_CODE, permissionList.toArray(new String[permissionList.size()]));
        }
    }

    /**
     * 获取权限返回回执
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    protected void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_GROUP_CODE) {
            List<String> deniedPermissions = new ArrayList<>();//拒绝授权的集合
            List<String> noShowPermission = new ArrayList<>();//没有显示授权的集合
            for (int k = 0; k < grantResults.length; k++) {
                if (grantResults[k] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[k])) {
                        noShowPermission.add(permissions[k]);
                    }
                    deniedPermissions.add(permissions[k]);
                }
            }
            if (deniedPermissions.size() > 0) {
                //部分权限没有授权
                if (deniedPermissions.equals(noShowPermission)) {
                    //所有拒绝授权的权限点击不再提示权限，获取权限窗口没有展示
                    showAlert();
                } else {
                    callBack.onPermissionDenied(requestCode, deniedPermissions.toArray(new String[deniedPermissions.size()]));
                }
            } else {
                //用户全部授权
                callBack.onPermissionAllow(requestCode, permissions);
            }
        }
    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog dialog = builder.create();
        dialog.setTitle(dialogTitle);
        dialog.setMessage(dialogMessage);
        dialog.setCancelable(false);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, diaBtnNegTit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                dialog.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, diaBtnPosTit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialo, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(diaBtnNegTitCol);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(diaBtnPosTitCol);
    }

    protected interface RequestPerssionCallBack {
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
