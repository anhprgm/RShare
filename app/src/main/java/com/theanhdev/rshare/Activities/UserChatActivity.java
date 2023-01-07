package com.theanhdev.rshare.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.ChatAdapter;
import com.theanhdev.rshare.funtionUsing.Funtion;
import com.theanhdev.rshare.models.ChatMessage;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UserChatActivity extends AppCompatActivity {
    private String uid_receiver;
    private RoundedImageView avt;
    private TextView UserName;
    private EditText inputMessage;
    private ImageView backBtn, sent;
    RecyclerView messageRecycleView;
    String avtReceiver = "";
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
    private String senderRoom, receiverRoom;
    List<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        binding();
        DatabaseReference UserRef = database.getReference().child(Constants.KEY_LIST_USERS);
        Bundle bundle = getIntent().getExtras();
        uid_receiver = bundle.getString(Constants.KEY_USER_RECEIVER, uid_receiver);
        Log.d("AAA", "onDataChange: " + uid_receiver);
        backBtn.setOnClickListener(v -> onBackPressed());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.uid.equals(uid_receiver)) {
                        Log.d("AAA", "onDataChange: ");
                        avt.setImageBitmap(setImageBitmap(users.UserImage));
                        UserName.setText(users.UserName);
                        avtReceiver = users.UserImage;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sent.setOnClickListener(v -> sendMessage());
        //set Adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, firebaseUser.getUid());
        messageRecycleView = findViewById(R.id.messageRecycleView);
        messageRecycleView.setAdapter(chatAdapter);
//        scrollListener();
        loadMessage();
    }

    private void binding() {
        avt = findViewById(R.id.avt);
        UserName = findViewById(R.id.userName);
        backBtn = findViewById(R.id.backBtn);
        inputMessage = findViewById(R.id.inputMessage);
        sent = findViewById(R.id.sent);
    }
    public Bitmap setImageBitmap(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void sendMessage() {
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        senderRoom = firebaseUser.getUid()+uid_receiver;
        receiverRoom = uid_receiver+firebaseUser.getUid();
        DatabaseReference messageSentRef = database.getReference(Constants.KEY_COLLECTION_CHAT).child(senderRoom);
        DatabaseReference messageReceiveRef = database.getReference(Constants.KEY_COLLECTION_CHAT).child(receiverRoom);
        String messageid = database.getReference().push().getKey();
        assert messageid != null;
        ChatMessage messageSent = new ChatMessage();
        messageSent.senderId = firebaseUser.getUid();
        try {
            messageSent.dateObj = format.parse(getTimeCurrent());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        messageSent.dateTime = getTimeCurrent();
        messageSent.receiverId = uid_receiver;
        messageSent.conversionId = messageid;
        messageSent.message = inputMessage.getText().toString().trim();
        messageSentRef.child(messageid).setValue(messageSent).addOnCompleteListener(task -> inputMessage.setText(""));
        ChatMessage messageRev = new ChatMessage();
        messageRev.senderId = firebaseUser.getUid();
        messageRev.receiverId = uid_receiver;
        messageRev.dateObj = messageSent.dateObj;
        messageRev.dateTime = getTimeCurrent();
        messageRev.conversionId = messageid;
        messageRev.message = inputMessage.getText().toString().trim();
        messageReceiveRef.child(messageid).setValue(messageRev);
    }
    
    
    private void loadMessage() {
        senderRoom = firebaseUser.getUid()+uid_receiver;
        receiverRoom = uid_receiver+firebaseUser.getUid();
        DatabaseReference messageSentRef = database.getReference(Constants.KEY_COLLECTION_CHAT).child(senderRoom);
        messageSentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    assert message != null;
                    message.avtReceiver = avtReceiver;
                    chatMessages.add(message);
                }
                if (chatMessages.size() > 0) {
                    messageRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String getTimeCurrent() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        return format.format(date);
    }

}