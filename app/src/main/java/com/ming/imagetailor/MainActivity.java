package com.ming.imagetailor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ming.circleimageview_lib.CircleImageView;
import com.ming.imagetailor_lib.PickImageChooser;


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

                PickImageChooser pickImageChooser = new PickImageChooser();
                Intent chooser = pickImageChooser.startPickImageChooser(MainActivity.this);
                startActivity(chooser);
            }
        });
    }
}
