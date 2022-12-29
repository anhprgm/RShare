package com.theanhdev.rshare.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.Activities.MakePostActivity;
import com.theanhdev.rshare.Activities.OpenImageActivity;
import com.theanhdev.rshare.Activities.WallUserActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RhomeAdapter;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements PostListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "AAA";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String encodedImage;
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        ImageView addPost = view.findViewById(R.id.addPost);
        RecyclerView recyclerView = view.findViewById(R.id.recyclePost);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ImageView logo = view.findViewById(R.id.logo);
        List<Posts> postsList = new ArrayList<>();
        List<Users> usersList = new ArrayList<>();
        RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
        recyclerView.setAdapter(rhomeAdapter);
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
        DatabaseReference postsRef = database.getReference().child(Constants.KEY_COLLECTION_POSTS);
        DatabaseReference usersRef = database.getReference().child(Constants.KEY_LIST_USERS);
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
                                for (Users users : usersList) {
                                    assert posts != null;
                                    if (Objects.equals(users.uid, posts.uid)) {
                                        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
                                        try {
                                            Date current = format.parse(getTimeCurrent());
                                            Date timePost = format.parse(posts.timeStamp);
                                            assert current != null;
                                            assert timePost != null;
                                            long diff = current.getTime() - timePost.getTime();
                                            int timeInSeconds = (int) (diff / 1000);
                                            int hours, minutes, seconds, days;
                                            hours = timeInSeconds / 3600;
                                            timeInSeconds = timeInSeconds - (hours * 3600);
                                            minutes = timeInSeconds / 60;
                                            timeInSeconds = timeInSeconds - (minutes * 60);
                                            seconds = timeInSeconds;
                                            days = hours / 24;
                                            if (days < 24) {
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
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        posts.userName = users.UserName;
                                        posts.userImage = users.UserImage;
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


        return view;
    }
    void isLoading(boolean isLoading) {
        ProgressBar progressBar = requireView().findViewById(R.id.progress_bar);
        if (isLoading) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onPostClicked(Posts posts) {
        Intent intent = new Intent(getActivity(), OpenImageActivity.class);
        intent.putExtra(Constants.KEY_IMAGE, posts.image);
        startActivity(intent);
    }

    @Override
    public void onUserImageClicked(Posts posts) {
        Intent intent = new Intent(getActivity(), WallUserActivity.class);
        intent.putExtra(Constants.UID_POST, posts.uid);
        startActivity(intent);
    }

   private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String getTimeCurrent() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        return format.format(date);
    }
}