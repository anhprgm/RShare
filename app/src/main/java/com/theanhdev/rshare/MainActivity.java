package com.theanhdev.rshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theanhdev.rshare.fragment.AccountFragment;
import com.theanhdev.rshare.fragment.HomeFragment;
import com.theanhdev.rshare.fragment.NotiFragment;
import com.theanhdev.rshare.fragment.RvidFragment;
import com.theanhdev.rshare.fragment.SearchFragment;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            loadFragment(new HomeFragment());
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putString(Constants.KEY_USER_ID, bundle.getString(Constants.KEY_USER_ID));
            AccountFragment accountFragment = new AccountFragment();
            accountFragment.setArguments(bundle2);
            loadFragment(accountFragment);
        }


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", token);
                    DatabaseReference UserRef = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE).getReference();
                    UserRef.child(Constants.KEY_LIST_USERS)
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users users = snapshot.getValue(Users.class);
                                    assert users != null;
                                    users.fcm_token = token;
                                    UserRef.child(Constants.KEY_LIST_USERS)
                                            .child(FirebaseAuth.getInstance().getUid()).setValue(users);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                });
        BottomNavigationView bottomNavigationView = findViewById(R.id.menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    loadFragment(new HomeFragment());
                    break;
                case R.id.search:
                    loadFragment(new SearchFragment());
                    break;
                case R.id.rvideo:
                    loadFragment(new RvidFragment());
                    break;
                case R.id.notification:
                    loadFragment(new NotiFragment());
                    break;
                case R.id.Me:
                    Bundle bundle1 = new Bundle();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    assert user != null;
                    bundle1.putString(Constants.KEY_USER_ID, user.getUid());
                    AccountFragment accountFragment = new AccountFragment();
                    accountFragment.setArguments(bundle1);
                    loadFragment(accountFragment);
                    break;
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        int fragment = getSupportFragmentManager().getBackStackEntryCount();
        if (fragment == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void loadFragment(Fragment fragment) {
        String back = fragment.getClass().getName();
        boolean fragmentPopped = false;
        try {
            fragmentPopped = getSupportFragmentManager().popBackStackImmediate(back, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragmentPopped) {
            transaction.replace(R.id.fl_wrapper, fragment);
            transaction.addToBackStack(back);
            transaction.setReorderingAllowed(true);
        }
        try {
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}