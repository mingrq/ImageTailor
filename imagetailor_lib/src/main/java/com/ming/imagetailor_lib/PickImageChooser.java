package com.ming.imagetailor_lib;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * 获取图片方式选择器
 * ===========================
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/20 9:58
 */
public class PickImageChooser {

    private Context context;

    public Intent startPickImageChooser(Context context) {
        this.context = context;
        //创建ChooserIntent
        Intent intent = new Intent(Intent.ACTION_CHOOSER);
//创建相机Intent
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{captureIntent});

//将相机Intent以数组形式放入Intent.EXTRA_INITIAL_INTENTS
//创建相册Intent
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//将相册Intent放入Intent.EXTRA_INTENT
        intent.putExtra(Intent.EXTRA_INTENT, albumIntent);
        context.startActivity(intent);
        return intent;
    }
}
