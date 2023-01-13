package com.theanhdev.rshare.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class UserCustomActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
    private FirebaseUser user = mAuth.getCurrentUser();
    private String encodedImage = "";
    private TextView save;
    private ImageView back, userImage;
    private EditText Uname, Ubio, Utag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        back = findViewById(R.id.backBtn);
        save = findViewById(R.id.save);
        Uname = findViewById(R.id.UserNameInput);
        Ubio = findViewById(R.id.userBioInput);
        Utag = findViewById(R.id.userTagNameInput);
        userImage = findViewById(R.id.userImage);
        userImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        back.setOnClickListener(view -> {
            onBackPressed();
        });
        save.setOnClickListener(v -> {sendData();});

    }

    private void sendData() {
        String name    = Uname.getText().toString().trim();
        String tagName = Utag.getText().toString().trim();
        String bio     = Ubio.getText().toString().trim();
        DatabaseReference UserRef = database.getReference().child(Constants.KEY_LIST_USERS);
        DatabaseReference PostRef = database.getReference().child(Constants.KEY_COLLECTION_POSTS);
        String idPost = PostRef.push().getKey();
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (users.uid.equals(user.getUid())) {
                        if (!encodedImage.isEmpty()) users.UserImage = encodedImage;
                        if (!name.isEmpty()) users.UserName = name;
                        if (!tagName.isEmpty()) users.tagName = tagName;
                        if (!bio.isEmpty()) users.bio = bio;
                        UserRef.child(users.uid).setValue(users).addOnCompleteListener(task -> {
                            SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                            //make a post when user update avt

                            Toast.makeText(UserCustomActivity.this, "success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserCustomActivity.this, MainActivity.class);
                            Posts posts = new Posts();
                            posts.idPost = idPost;
                            posts.caption = "Update new Avt";
                            posts.timeStamp = getTimeCurrent();
                            posts.uid = FirebaseAuth.getInstance().getUid();
                            try {
                                posts.dateObject = format.parse(getTimeCurrent());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            posts.image = encodedImage;
                            PostRef.child(posts.idPost).setValue(posts).addOnCompleteListener(v -> {
                                Toast.makeText(UserCustomActivity.this, "successPost", Toast.LENGTH_SHORT).show();
                            });
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        Log.d("Compressed dimensions xxx", bitmap.getWidth()+" "+bitmap.getHeight());
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            Log.d("Original   dimensions", bitmap.getWidth()+" "+bitmap.getHeight());
                            encodedImage = encodeImage(bitmap);
                            loadImage();
                            Log.d("encode string", encodedImage + encodedImage.length());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(this, "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }
            }
    );
    private void loadImage() {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        userImage.setImageBitmap(bitmap);
    }


    private String getTimeCurrent() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        return format.format(date);
    }
}