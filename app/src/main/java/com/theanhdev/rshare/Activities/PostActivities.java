package com.theanhdev.rshare.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.theanhdev.rshare.databinding.ActivityPostActivitiesBinding;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.ulities.Constants;

public class PostActivities extends AppCompatActivity {
    ActivityPostActivitiesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostActivitiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Posts posts = new Posts();
        getDataFromOtherIntent();

    }

    void getDataFromOtherIntent() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Posts posts = (Posts) bundle.getSerializable(Constants.KEY_POST);
    }
}