package com.theanhdev.rshare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RecentChatAdapter;
import com.theanhdev.rshare.adapters.UserOnChat;
import com.theanhdev.rshare.listeners.UsersListener;
import com.theanhdev.rshare.models.ChatMessage;
import com.theanhdev.rshare.models.RecentChat;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity implements UsersListener {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
    private ImageView backBtn;
    String TAG = "AAAB";
    List<RecentChat> recentChats = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        RecyclerView userRecycleView = findViewById(R.id.userRecycleView);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> onBackPressed());
        RecyclerView recentChatRecycleView = findViewById(R.id.recentChatRecycleView);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        List<Users> usersList = new ArrayList<>();
        UserOnChat userOnChat = new UserOnChat(usersList, this);
        userRecycleView.setAdapter(userOnChat);
        RecentChatAdapter recentChatAdapter = new RecentChatAdapter(recentChats, this);
        recentChatRecycleView.setAdapter(recentChatAdapter);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child(Constants.KEY_LIST_USERS);
        DatabaseReference ChatRef = firebaseDatabase.getReference().child(Constants.KEY_COLLECTION_CHAT);
        DatabaseReference RecentChatRef = firebaseDatabase.getReference().child(Constants.KEY_COLLECTION_CONVERSIONS);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert firebaseUser != null;
                    assert users != null;
                    if (Objects.equals(users.uid, firebaseUser.getUid())) {
                    } else {
                        ChatRef.child(FirebaseAuth.getInstance().getUid() + users.uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long count = snapshot.getChildrenCount();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    count--;
                                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                                    if (count == 0) {
                                        RecentChat recentChat = new RecentChat();
                                        recentChat.message = chatMessage.message;
                                        recentChat.uid_receiver = users.uid;
                                        recentChat.name = users.UserName;
                                        recentChat.avt = users.UserImage;
                                        recentChat.date = chatMessage.dateObj;
                                        RecentChatRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(users.uid).setValue(recentChat);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        usersList.add(users);
                    }
                }
                if (usersList.size() > 0) {
                    userOnChat.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //load conversation
        RecentChatRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentChats.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RecentChat recentChat = dataSnapshot.getValue(RecentChat.class);
                    recentChats.add(recentChat);
                }
                if (recentChats.size() > 0) {
                    recentChats.sort((recentChat, t1) -> t1.date.compareTo(recentChat.date));
                    recentChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void OnClickUser(Users users) {
        Intent intent = new Intent(ChatActivity.this, UserChatActivity.class);
        intent.putExtra(Constants.KEY_USER_RECEIVER, users.uid);
        startActivity(intent);
    }

    @Override
    public void OnClickRecentChat(RecentChat recentChat) {
        Intent intent = new Intent(ChatActivity.this, UserChatActivity.class);
        intent.putExtra(Constants.KEY_USER_RECEIVER, recentChat.uid_receiver);
        startActivity(intent);
    }
    private void UpdateMessage(Users users) {

    }
}