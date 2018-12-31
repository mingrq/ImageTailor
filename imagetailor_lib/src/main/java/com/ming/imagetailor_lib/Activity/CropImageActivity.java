package com.ming.imagetailor_lib.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ming.imagetailor_lib.ClipInfo;
import com.ming.imagetailor_lib.CustomView.CropCircleImageView;
import com.ming.imagetailor_lib.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 图片裁剪
 * ==============
 * Author MingRuQi
 * E-mail mingruqi@sina.cn
 * DateTime 2018/12/24 16:08
 */
public class CropImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropimage);
        CropCircleImageView imageView = findViewById(R.id.circle);

        Uri imagepath = getIntent().getData();
        Intent intent = getIntent();
        ClipInfo clipInfo = (ClipInfo) intent.getSerializableExtra("clipInfo");

        //设置裁剪类型
        imageView.setClipType(clipInfo.getClipType());

        //设置宽高比
        imageView.setWidthHeightScale(clipInfo.getWidthScale(),clipInfo.getHeightScale());

       /* ImageView imageView = findViewById(R.id.cropImage);*/
        imageView.setImageURI(imagepath);
      /*  try {
            FileInputStream fileInputStream = new FileInputStream(new File(imagepath.getPath()));
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }
}
