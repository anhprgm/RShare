package com.theanhdev.rshare.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.theanhdev.rshare.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private ImageView logo;
    private LinearLayout inputUser;
    private TextView SignBtn, SignText;
    private FirebaseAuth mAuth;
    private EditText EmailInput, PasswordInput, ConfirmPasswordInput;
    private String CODE_SIGN = "login", email, password, confirm_password;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding();
        mAuth = FirebaseAuth.getInstance();
        Handler handler = new Handler();
        handler.postDelayed(() -> logo.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.zoom_out_up)), 2500);
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            inputUser.setVisibility(View.VISIBLE);
            inputUser.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in_up));
        }, 3900);
        SwitchSign();
    }
    private void binding() {
        logo = findViewById(R.id.logo);
        inputUser = findViewById(R.id.inputUser);
        SignBtn = findViewById(R.id.SignBtn);
        SignText = findViewById(R.id.signText);
        EmailInput = findViewById(R.id.email);
        PasswordInput = findViewById(R.id.password);
        ConfirmPasswordInput = findViewById(R.id.confirm_password);
    }
    private void SwitchSign() {
        SignText.setOnClickListener(v -> {
            String text = SignText.getText().toString().trim();
            switch (text){
                case "Create new account":
                    SignText.setText("Haved an account");
                    SignBtn.setText("Sign Up");
                    ConfirmPasswordInput.setVisibility(View.VISIBLE);
                    CODE_SIGN = "signup";
                    break;
                case "Haved an account":
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
            switch (CODE_SIGN) {
                case "login":
                    mAuth.signInWithEmailAndPassword()
                    break;
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void formatText() {
        email = EmailInput.getText().toString().trim();
        if (!validateEmail(email)) EmailInput.setError("Invalid Email!");
        else email = EmailInput.getText().toString().trim();
        password = EmailInput.getText().toString().trim();
        if (ConfirmPasswordInput.getVisibility() == View.VISIBLE) {
            confirm_password = ConfirmPasswordInput.getText().toString().trim();
        }
    }
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}