package com.theanhdev.rshare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.theanhdev.rshare.R;
import com.theanhdev.rshare.funtionUsing.FuntionUsing;
import com.theanhdev.rshare.ulities.Constants;

public class OpenImageActivity extends AppCompatActivity {
    private final String TAG = "OPEN_IMAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        FuntionUsing funtion = new FuntionUsing();
        ImageView seeImg = findViewById(R.id.SeeImage);
        ImageView backBtn = findViewById(R.id.backBtn);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(Constants.KEY_IMAGE) != null) {
                seeImg.setImageBitmap(funtion.setImageBitmap(bundle.getString(Constants.KEY_IMAGE)));
            }
        }
        backBtn.setOnClickListener(view -> onBackPressed());
    }
}