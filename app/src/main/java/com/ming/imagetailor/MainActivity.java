package com.ming.imagetailor;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ming.imagetailor_lib.ImageTailor;


public class MainActivity extends AppCompatActivity {


    private ImageView circleImageView;
    private ImageTailor imageTailor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleImageView = findViewById(R.id.circleImage);
        imageTailor = new ImageTailor(MainActivity.this);
        imageTailor.setCameraFilePath(Environment.getExternalStorageDirectory().toString());
        //设置裁剪类型
        imageTailor.setClipType(ImageTailor.TYPEFIXEDOVAL);
        //imageTailor.setClipType(ImageTailor.TYPEOVAL);
        //imageTailor.setClipType(ImageTailor.TYPESQUARE);
        //imageTailor.setClipType(ImageTailor.TYPEFIXEDOSQUARE);
        //设置裁剪比例
        imageTailor.setWidthHeightScale(3, 3);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageTailor.commit();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageTailor.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageTailor.onActivityResult(requestCode, resultCode, data);
    }
}
