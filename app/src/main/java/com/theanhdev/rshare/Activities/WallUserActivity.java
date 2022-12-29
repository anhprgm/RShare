package com.theanhdev.rshare.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RhomeAdapter;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WallUserActivity extends AppCompatActivity implements PostListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_user);
        RoundedImageView avt = findViewById(R.id.userImage);
        TextView uName = findViewById(R.id.userNameAcc);
        TextView inbox = findViewById(R.id.inboxx);
        TextView follow = findViewById(R.id.followx);
        RecyclerView recyclerPostUser = findViewById(R.id.recyclePostUser);
        Bundle bundle = getIntent().getExtras();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        if (user.getUid().equals(user.getUid())) {
            inbox.setVisibility(View.GONE);
            follow.setVisibility(View.GONE);
        }
        if (bundle != null) {
            if (bundle.getString(Constants.UID_POST) != null) {
                Toast.makeText(this, bundle.getString(Constants.UID_POST), Toast.LENGTH_SHORT).show();
                Users users = new Users();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USERS)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    if (Objects.equals(queryDocumentSnapshot.getString(Constants.KEY_USER_ID), bundle.getString(Constants.UID_POST))) {
                                        users.UserImage = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                                        avt.setImageBitmap(getBitmapImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE)));
                                        users.UserName = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                                        uName.setText(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                        break;
                                    }
                                }
                            }
                        });
                avt.setOnClickListener(v -> {
                    Intent intent = new Intent(this, OpenImageActivity.class);
                    intent.putExtra(Constants.KEY_IMAGE, users.UserImage);
                    startActivity(intent);
                });
                db.collection(Constants.KEY_COLLECTION_POSTS)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                List<Posts> postsList = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    if (Objects.equals(queryDocumentSnapshot.getString(Constants.UID_POST), bundle.getString(Constants.UID_POST))) {
                                        Posts posts = new Posts();
                                        posts.caption = queryDocumentSnapshot.getString(Constants.CAPTION_POST);
                                        posts.image = queryDocumentSnapshot.getString(Constants.ENCODED_IMAGE_POST);
                                        posts.timeStamp = queryDocumentSnapshot.getString(Constants.KEY_TIMESTAMP);
                                        posts.userName = users.UserName;
                                        posts.userImage = users.UserImage;
                                        postsList.add(posts);
                                    }

                                    if (postsList.size() > 0) {
                                        Collections.sort(postsList, ((posts, t1) -> t1.timeStamp.compareTo(posts.timeStamp)));
//                                        RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
//                                        recyclerPostUser.setAdapter(rhomeAdapter);
                                    } else Toast.makeText(this, "min", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onPostClicked(Posts posts) {
        Intent intent = new Intent(this, OpenImageActivity.class);
        intent.putExtra(Constants.KEY_IMAGE, posts.image);
        startActivity(intent);
    }

    @Override
    public void onUserImageClicked(Posts posts) {
    }

    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}