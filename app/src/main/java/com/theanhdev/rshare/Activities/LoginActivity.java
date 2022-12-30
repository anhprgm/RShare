package com.theanhdev.rshare.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.funtionUsing.Funtion;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ImageView logo, hintBtn;
    private LinearLayout inputUser, MadeBy;
    private TextView SignBtn, SignText;
    private ProgressBar progressBar;
    private Funtion funtion;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText EmailInput, PasswordInput, ConfirmPasswordInput;
    private String CODE_SIGN = "login", email, password, confirm_password;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance("https://rshare-52ded-default-rtdb.asia-southeast1.firebasedatabase.app/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding();
        Handler handler = new Handler();
        handler.postDelayed(() -> logo.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.zoom_out_up)), 2500);
        handler.postDelayed(() -> {
            inputUser.setVisibility(View.VISIBLE);
            MadeBy.setVisibility(View.VISIBLE);
            MadeBy.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in_up));
            inputUser.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in_up));
        }, 3900);

        hintBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserCustomActivity.class);
            startActivity(intent);
        });


        SwitchSign();
        if (user != null) {

//            FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
//            DatabaseReference myRef = database.getReference().child("users");
//            myRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    myRef.child("id").setValue("new");
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

//            myRef.setValue("Hello, World!");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toast.makeText(this, user.getUid(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else
            SignActivity();

    }
    private void binding() {
        logo = findViewById(R.id.logo);
        MadeBy = findViewById(R.id.madeBy);
        inputUser = findViewById(R.id.inputUser);
        SignBtn = findViewById(R.id.SignBtn);
        SignText = findViewById(R.id.signText);
        EmailInput = findViewById(R.id.email);
        PasswordInput = findViewById(R.id.password);
        ConfirmPasswordInput = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.PROGRESS);
        hintBtn = findViewById(R.id.hintBtn);
    }
    @SuppressLint("SetTextI18n")
    private void SwitchSign() {
        SignText.setOnClickListener(v -> {
            String text = SignText.getText().toString().trim();
            switch (text){
                case "Create new account":
                    SignText.setText("Have an account");
                    SignBtn.setText("Sign Up");
                    ConfirmPasswordInput.setVisibility(View.VISIBLE);
                    CODE_SIGN = "signup";
                    break;
                case "Have an account":
                    SignText.setText("Create new account");
                    SignBtn.setText("Login");
                    ConfirmPasswordInput.setVisibility(View.GONE);
                    CODE_SIGN = "login";
                    break;
                default:
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SignActivity() {
        SignBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            switch (CODE_SIGN) {
                case "login":
                    if (formatText()) {
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Intent intent = new Intent(this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        toast(Objects.requireNonNull("Người dùng không tồn tại"));
                                    }
                                    progressBar.setVisibility(View.GONE);
                                });
                    }
                    break;
                case "signup":
                    if (formatText()) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                                        DatabaseReference databaseReference = database.getReference();
                                        assert user1 != null;
                                        Users users = new Users();
                                        int num = (int) (Math.random() * (1000000000 + 1));
                                        users.UserName = "RUser" + "-" + num;
                                        users.UserImage = "";
                                        users.tagName = "";
                                        users.bio = "";
                                        users.uid = user1.getUid();
                                        users.UserEmail = user1.getEmail();
                                        databaseReference.child(Constants.KEY_LIST_USERS).child(user1.getUid()).setValue(users);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        toast(Objects.requireNonNull(Objects.requireNonNull(task.getException()).toString()));
                                    }
                                    progressBar.setVisibility(View.GONE);
                                });

                    }
                    break;
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private boolean formatText() {
        boolean checkEmail = false, checkPass = false, checkConfirmPass = false;
        email = EmailInput.getText().toString().trim();
        if (email.isEmpty()) EmailInput.setError("Email is empty");
        else if (!validateEmail(email)) EmailInput.setError("Invalid Email!");
        else checkEmail = true;
        password = PasswordInput.getText().toString().trim();
        if (validPassword(password).equals("Password is empty")) PasswordInput.setError(validPassword(password));
        else if (validPassword(password).equals("Password less than six characters")) PasswordInput.setError(validPassword(password));
        else checkPass = true;
        if (ConfirmPasswordInput.getVisibility() == View.VISIBLE) {
            confirm_password = ConfirmPasswordInput.getText().toString().trim();
            if (!TextUtils.equals(confirm_password, password)) ConfirmPasswordInput.setError("Confirm password not match");
            else checkConfirmPass = true;
        } else checkConfirmPass = true;
        return checkEmail && checkPass && checkConfirmPass;
    }
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    public static String validPassword(String passwordStr) {
        if (TextUtils.isEmpty(passwordStr)) {
            return "Password is empty";
        } else if (passwordStr.length() < 6) {
            return "Password less than six characters";
        } else return "TRUE";
    }


    //add user to database
}