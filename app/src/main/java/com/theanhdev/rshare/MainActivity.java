package com.theanhdev.rshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theanhdev.rshare.fragment.AccountFragment;
import com.theanhdev.rshare.fragment.HomeFragment;
import com.theanhdev.rshare.fragment.NotiFragment;
import com.theanhdev.rshare.fragment.RvidFragment;
import com.theanhdev.rshare.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new HomeFragment());
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
                    loadFragment(new AccountFragment());
                    break;
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_wrapper, fragment);
        transaction.addToBackStack("replacement");
        transaction.setReorderingAllowed(true);
        transaction.commit();
    }
}