package com.theanhdev.rshare.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.ulities.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MakePostActivity extends AppCompatActivity {
    private ImageView openCamera, pickImageIc, image, backBtn, backImg, ImageOpen;
    private TextView SavePost;
    private EditText caption;
    private String encodedImage = "";
    private LinearLayout seeImage, postLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RelativeLayout userInf;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
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
            DatabaseReference list_post = database.getReference().child(Constants.KEY_COLLECTION_POSTS);
            list_post.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Posts posts = dataSnapshot.getValue(Posts.class);
                        assert posts != null;
                        Log.d("AAA", posts.caption);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        openImage();

        SavePost.setOnClickListener(v -> {
            if (caption.getText().toString().isEmpty())
                Toast.makeText(this, "fill the caption", Toast.LENGTH_SHORT).show();
            else {
                FirebaseUser user = mAuth.getCurrentUser();
                SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                DatabaseReference databaseReference = database.getReference();
                String idPost = databaseReference.child(Constants.KEY_COLLECTION_POSTS).push().getKey();
                assert user != null;
                Posts posts = new Posts();
                posts.image = encodedImage;
                posts.uid = user.getUid();
                posts.caption = caption.getText().toString();
                posts.sumLove = 0;
                posts.timeStamp = getTimeCurrent();
                posts.idPost = idPost;
                try {
                    posts.dateObject = format.parse(getTimeCurrent());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                assert idPost != null;
                databaseReference.child(Constants.KEY_COLLECTION_POSTS).child(idPost).setValue(posts).addOnCompleteListener(task -> {
                    Toast.makeText(this, "Complete", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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

    private String getTimeCurrent() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        return format.format(date);
    }

    private Bitmap setUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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