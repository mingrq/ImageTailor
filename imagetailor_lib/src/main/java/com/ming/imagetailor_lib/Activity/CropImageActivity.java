package com.ming.imagetailor_lib.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

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
       /* ImageView imageView = findViewById(R.id.cropImage);*/
        CropCircleImageView imageView = findViewById(R.id.circle);
        Uri imagepath = getIntent().getData();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(imagepath.getPath()));
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
