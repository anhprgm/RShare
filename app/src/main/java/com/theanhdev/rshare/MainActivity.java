package com.theanhdev.rshare;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.theanhdev.rshare.models.RecentChat;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        BottomNavigationView bottomNavigationView = findViewById(R.id.menu);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem UserImage = menu.findItem(R.id.Me);
        if (bundle == null) {
            loadFragment(new HomeFragment());
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putString(Constants.KEY_USER_ID, bundle.getString(Constants.KEY_USER_ID));
            AccountFragment accountFragment = new AccountFragment();
            accountFragment.setArguments(bundle2);
            loadFragment(accountFragment);
        }
        startService(new Intent(getApplicationContext(), MyService.class));
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", token);
                    DatabaseReference UserRef = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE_REALTIME).getReference();
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
        createNotificationChannel();
        DatabaseReference conversationRef = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE_REALTIME).getReference(Constants.KEY_COLLECTION_CONVERSIONS).child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        conversationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                RecentChat recentChat = snapshot.getValue(RecentChat.class);
                assert recentChat != null;
                if (FirebaseAuth.getInstance().getUid().equals(recentChat.uid_sender)) {
                    pushNotification(recentChat.name, recentChat.message);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("changexxx", "sd");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("changexxx", "s");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("changexxx", "j");
            }
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

    private void pushNotification(String userName, String conversation) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logorshare)
                .setContentTitle(userName)
                .setContentText(conversation)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setFullScreenIntent(pendingIntent, true);

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1234, builder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "rshare";
        String description = "rshare_noti";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    private boolean checkBackgroundService() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (processInfo.processName.equals(getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}