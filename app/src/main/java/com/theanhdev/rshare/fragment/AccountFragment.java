package com.theanhdev.rshare.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.provider.ContactsContract;
import android.service.autofill.Sanitizer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theanhdev.rshare.Activities.OpenImageActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RhomeAdapter;
import com.theanhdev.rshare.funtionUsing.Funtion;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.models.Users;
import com.theanhdev.rshare.ulities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements PostListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        // Inflate the layout for this fragment
        RoundedImageView avt = view.findViewById(R.id.userImage);
        TextView uName = view.findViewById(R.id.userNameAcc);
        TextView inbox, follow;
        inbox = view.findViewById(R.id.inbox);
        follow = view.findViewById(R.id.follow);
        RecyclerView recyclerPostUser = view.findViewById(R.id.recyclePostUser);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE);
        DatabaseReference usersRef = database.getReference().child(Constants.KEY_LIST_USERS);
        DatabaseReference postsRef = database.getReference().child(Constants.KEY_COLLECTION_POSTS);

        List<Posts> postsList = new ArrayList<>();
        RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
        recyclerPostUser.setAdapter(rhomeAdapter);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    assert user != null;
                    if (user.getUid().equals(users.uid)) {
                        postsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    Posts posts = dataSnapshot1.getValue(Posts.class);
                                    SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
                                    try {
                                        Funtion funtion = new Funtion();
                                        Date current = format.parse(funtion.getTimeCurrent());
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
                                    postsList.add(posts);
                                }
                                if (postsList.size() > 0) {
                                    postsList.sort(((posts, t1) -> t1.dateObject.compareTo(posts.dateObject)));
                                    rhomeAdapter.notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        db.collection(Constants.KEY_COLLECTION_USERS)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                            assert user != null;
//                            if (Objects.equals(queryDocumentSnapshot.getString(Constants.KEY_USER_ID), user.getUid())) {
//                                users.UserImage = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
//                                avt.setImageBitmap(getBitmapImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE)));
//                                users.UserName = queryDocumentSnapshot.getString(Constants.KEY_NAME);
//                                uName.setText(queryDocumentSnapshot.getString(Constants.KEY_NAME));
//                                break;
//                            }
//                        }
//                    }
//                });
        assert user != null;
        if (user.getUid().equals(user.getUid())) {
            inbox.setVisibility(View.GONE);
            follow.setVisibility(View.GONE);
        }
        avt.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), OpenImageActivity.class);
//            intent.putExtra(Constants.KEY_IMAGE, users.UserImage);
//            startActivity(intent);
        });
//        db.collection(Constants.KEY_COLLECTION_POSTS)
//                .get()
//                .addOnCompleteListener(task -> {
//                   if (task.isSuccessful() && task.getResult() != null) {
//                       List<Posts> postsList = new ArrayList<>();
//                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                           assert user != null;
//                           if (Objects.equals(queryDocumentSnapshot.getString(Constants.UID_POST), user.getUid())) {
//                               Posts posts = new Posts();
//                               posts.caption = queryDocumentSnapshot.getString(Constants.CAPTION_POST);
//                               posts.image = queryDocumentSnapshot.getString(Constants.ENCODED_IMAGE_POST);
//                               posts.timeStamp = queryDocumentSnapshot.getString(Constants.KEY_TIMESTAMP);
//                               posts.userName = users.UserName;
//                               posts.userImage = users.UserImage;
//                               postsList.add(posts);
//                           }
//
//                           if (postsList.size() > 0) {
//                               Collections.sort(postsList, ((posts, t1) -> t1.timeStamp.compareTo(posts.timeStamp)));
//                               RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
////                               recyclerPostUser.setAdapter(rhomeAdapter);
//                           } else Toast.makeText(getActivity(), "min", Toast.LENGTH_SHORT).show();
//                       }
//                   }
//                });
        return view;
    }
    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onPostClicked(Posts posts) {
        Intent intent = new Intent(getActivity(), OpenImageActivity.class);
        intent.putExtra(Constants.KEY_IMAGE, posts.image);
        startActivity(intent);
    }

    @Override
    public void onUserImageClicked(Posts posts) {

    }
}