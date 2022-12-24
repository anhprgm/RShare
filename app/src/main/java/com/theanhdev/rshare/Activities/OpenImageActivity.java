package com.theanhdev.rshare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.theanhdev.rshare.R;

import java.util.ArrayList;

public class OpenImageActivity extends AppCompatActivity {
    private String encodedImage;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        image = findViewById(R.id.image);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("encodedList") != null) {
                encodedImage = bundle.getString("encodedImage");
                Log.d("encoded", encodedImage);
            }
        }
        loadImage();
    }

    private void loadImage() {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        image.setImageBitmap(bitmap);
    }
}