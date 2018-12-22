package com.ming.imagetailor_lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择图片
 * ===============
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/22 9:47
 */
public class SelectImage {
    public SelectImage(Context context) {
        this.context = context;
    }

    private Context context;

    protected List<Intent> getImageIntent(String action) {
        PackageManager packageManager = context.getPackageManager();
        List<Intent> intents = new ArrayList<>();
        Intent galleryIntent = action == Intent.ACTION_GET_CONTENT ? new Intent(action) : new Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : resolveInfos) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                intent.setPackage(res.activityInfo.packageName);
            }
            intents.add(intent);
        }
        for (Intent i:intents){
            if (i.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")){
                intents.remove(i);
                break;
            }
        }
        return intents;
    }
}
