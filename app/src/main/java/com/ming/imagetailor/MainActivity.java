package com.ming.imagetailor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ming.com.circleimageview_lib.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleImageView = findViewById(R.id.circleImage);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
