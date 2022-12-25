package com.theanhdev.rshare.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.ulities.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class MakePostActivity extends AppCompatActivity {
    private ImageView openCamera, pickImageIc, image, backBtn, backImg, ImageOpen;
    private TextView SavePost;
    private EditText caption;
    private String encodedImage = "";
    private LinearLayout seeImage, postLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RelativeLayout userInf;
    private static final int pic_id = 127;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);
        binding();
        pickImageIc.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        backBtn.setOnClickListener(view -> onBackPressed());

        openCamera.setOnClickListener(view -> {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, pic_id);
            Toast.makeText(this, "developing", Toast.LENGTH_SHORT).show();
        });
        openImage();

        SavePost.setOnClickListener(v -> {
            if (caption.getText().toString().isEmpty())
                Toast.makeText(this, "fill the caption", Toast.LENGTH_SHORT).show();
            else {
                Random random = new Random();
                int ranNum = random.nextInt(0);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                HashMap<String, String> Post = new HashMap<>();
                Post.put(Constants.ID_POST, String.valueOf(ranNum));
                assert user != null;
                Post.put(Constants.UID_POST, user.getUid());
                Post.put(Constants.CAPTION_POST, caption.getText().toString());
                Post.put(Constants.ENCODED_IMAGE_POST, encodedImage);
                Post.put(Constants.SUM_LOVE_POST, "0");
                db.collection(Constants.KEY_COLLECTION_POSTS)
                        .add(Post)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
    private void binding() {
        caption = findViewById(R.id.caption);
        openCamera = findViewById(R.id.openCam);
        pickImageIc = findViewById(R.id.openPhoto);
        image = findViewById(R.id.image);
        backBtn = findViewById(R.id.back);
        seeImage = findViewById(R.id.SeeImage);
        userInf = findViewById(R.id.UserInf);
        postLayout = findViewById(R.id.postLayout);
        backImg = findViewById(R.id.backOnImage);
        ImageOpen = findViewById(R.id.imageOpen);
        SavePost = findViewById(R.id.savePost);
    }

    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
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
                            Log.d("encode string", encodedImage + encodedImage.length());
                            loadImage();
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
        image.setImageBitmap(bitmap);
        Log.d("Compressed dimensions", bitmap.getWidth()+" "+bitmap.getHeight());
    }

    private void openImage() {
        image.setOnClickListener(v -> {
            userInf.setVisibility(View.GONE);
            postLayout.setVisibility(View.GONE);
            seeImage.setVisibility(View.VISIBLE);
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            ImageOpen.setImageBitmap(bitmap);
            Log.d("Compressed dimensions", bitmap.getWidth()+" "+bitmap.getHeight());
        });
        backImg.setOnClickListener(v -> {
            userInf.setVisibility(View.VISIBLE);
            postLayout.setVisibility(View.VISIBLE);
            seeImage.setVisibility(View.GONE);
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_wrapper, fragment);
        transaction.addToBackStack("replacement");
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == pic_id) {
//            assert data != null;
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            encodedImage = encodeImage(photo);
//            loadImage();
//        }
//    }
}