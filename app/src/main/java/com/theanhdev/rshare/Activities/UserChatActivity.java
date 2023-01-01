package com.theanhdev.rshare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

public class UserChatActivity extends AppCompatActivity {
    private String uid_receiver;
    private Users users = new Users();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        Bundle bundle = getIntent().getExtras();
        bundle.getString(Constants.KEY_USER_RECEIVER, uid_receiver);
        getUserData(uid_receiver);
        Log.d("AAA", bundle.getString(Constants.KEY_USER_RECEIVER, users.UserName));
    }

    public void getUserData(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
        DatabaseReference UserRef = database.getReference().child(Constants.KEY_LIST_USERS);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.uid.equals(uid)) {
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}