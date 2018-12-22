package com.ming.imagetailor_lib;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

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
        List<Intent> intents = new ArrayList<>();
        SelectImage selectImage = new SelectImage(context);
        intents.addAll(selectImage.getImageIntent(Intent.ACTION_GET_CONTENT));
        Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intents.add(intent_camera);
        Intent target;
        if (intents.isEmpty()) {
            target = new Intent();
        } else {
            target = intents.get(intents.size()-1);
            intents.remove(intents.size()-1);
        }
        Intent chooser = Intent.createChooser(target, "打开图片方式");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
        return chooser;
    }
}
