package com.theanhdev.rshare.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.databinding.ActivitySignUpBinding;
import com.theanhdev.rshare.ulities.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
        save.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            HashMap<String, String> users = new HashMap<>();
            users.put(Constants.KEY_EMAIL, user.getEmail());
            users.put(Constants.KEY_IMAGE, encodedImage);
            users.put(Constants.KEY_USER_ID, user.getUid());
            users.put(Constants.KEY_NAME, Uname.getText().toString().trim());
            users.put(Constants.KEY_USER_BIO, Ubio.getText().toString().trim());
            users.put(Constants.KEY_TAG_NAME, Utag.getText().toString().trim());
            db.collection(Constants.KEY_COLLECTION_USERS)
                    .add(users)
                    .addOnCompleteListener(documentReference -> {
                        Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    });
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
}