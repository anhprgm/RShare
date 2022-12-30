package com.theanhdev.rshare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.UserOnChat;
import com.theanhdev.rshare.listeners.UsersListener;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements UsersListener {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
    private RecyclerView userRecycleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        userRecycleView = findViewById(R.id.userRecycleView);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        List<Users> usersList = new ArrayList<>();
        UserOnChat userOnChat = new UserOnChat(usersList, this);

        userRecycleView.setAdapter(userOnChat);


        DatabaseReference databaseReference = firebaseDatabase.getReference().child(Constants.KEY_LIST_USERS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert firebaseUser != null;
                    assert users != null;
                    if (Objects.equals(users.uid, firebaseUser.getUid())) {
                        continue;
                    } else usersList.add(users);
                    if (usersList.size() > 0) {
                        userOnChat.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void OnClickUser(Users users) {

    }
}