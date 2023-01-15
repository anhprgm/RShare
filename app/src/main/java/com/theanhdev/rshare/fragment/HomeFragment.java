package com.theanhdev.rshare.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.Activities.ChatActivity;
import com.theanhdev.rshare.Activities.LoginActivity;
import com.theanhdev.rshare.Activities.MakePostActivity;
import com.theanhdev.rshare.MainActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RhomeAdapter;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.PostInf;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements PostListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE_REALTIME);
    List<Posts> postsList = new ArrayList<>();
    RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = "AAA";
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DatabaseReference postsRef = database.getReference().child(Constants.KEY_COLLECTION_POSTS);
        DatabaseReference usersRef = database.getReference().child(Constants.KEY_LIST_USERS);

        // Inflate the layout for this fragment
        ImageView addPost = view.findViewById(R.id.addPost);
        RecyclerView recyclerView = view.findViewById(R.id.recyclePost);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView logo = view.findViewById(R.id.logo);
        ImageView chat = view.findViewById(R.id.chat);
        logo.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        List<Users> usersList = new ArrayList<>();
        recyclerView.setAdapter(rhomeAdapter);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersList.add(users);
                }
                if (usersList.size() > 0) {
                    postsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            postsList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Posts posts = dataSnapshot.getValue(Posts.class);
                                assert posts != null;
                                posts.love = false;
                                for (Users users : usersList) {
                                    if (Objects.equals(users.uid, posts.uid)) {
                                        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                                        try {
                                            Date current = format.parse(getTimeCurrent());
                                            Date timePost = posts.dateObject;
                                            assert current != null;
                                            assert timePost != null;
                                            long diff = current.getTime() - timePost.getTime();
                                            int timeInSeconds = (int) (diff / 1000);
                                            int hours, minutes, seconds, days, weeks, months, years;
                                            hours = timeInSeconds / 3600;
                                            timeInSeconds = timeInSeconds - (hours * 3600);
                                            minutes = timeInSeconds / 60;
                                            timeInSeconds = timeInSeconds - (minutes * 60);
                                            seconds = timeInSeconds;
                                            days = hours / 24;
                                            weeks = days / 7;
                                            months = days / 30;
                                            years = months / 12;
                                            if (years < 1){
                                                if (months < 1) {
                                                    if (days < 7) {
                                                        if (hours < 24) {
                                                            if (hours > 1) {
                                                                posts.timeStamp = hours + "h";
                                                            } else {
                                                                if (minutes > 0) {
                                                                    posts.timeStamp = minutes + "m";
                                                                } else {
                                                                    posts.timeStamp = seconds + "s";
                                                                }
                                                            }
                                                        } else posts.timeStamp = days + " day";
                                                    } else posts.timeStamp = weeks + " week";
                                                } else posts.timeStamp = months + " month";
                                            } else posts.timeStamp = years + " year";
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        posts.userName = users.UserName;
                                        posts.userImage = users.UserImage;
                                        break;
                                    }
                                }
                                postsList.add(posts);
                            }
                            if (postsList.size() > 0) {
                                postsList.sort(((posts, t1) -> t1.dateObject.compareTo(posts.dateObject)));
                                progressBar.setVisibility(View.GONE);
                                rhomeAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, postsList.size() + "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        addPost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MakePostActivity.class);
            startActivity(intent);
        });
        chat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onPostClicked(Posts posts) {
        DatabaseReference PostRef = database.getReference(Constants.KEY_COLLECTION_POSTS).child(posts.idPost);
        PostInf postInf = new PostInf();
        postInf.uidLovePost = FirebaseAuth.getInstance().getUid();
        PostRef.child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).child(postInf.uidLovePost).setValue(postInf);
    }

    @Override
    public void onUserImageClicked(Posts posts) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra(Constants.KEY_USER_ID, posts.uid);
            startActivity(intent);
    }

    @Override
    public void onLoveBtn(Posts posts) {
        DatabaseReference PostRef = database.getReference(Constants.KEY_COLLECTION_POSTS).child(posts.idPost);
        PostInf postInf = new PostInf();
        postInf.uidLovePost = FirebaseAuth.getInstance().getUid();
        if (posts.love) PostRef.child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).child(postInf.uidLovePost).removeValue();
        else PostRef.child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).child(postInf.uidLovePost).setValue(postInf);
    }


    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String getTimeCurrent() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        return format.format(date);
    }

}