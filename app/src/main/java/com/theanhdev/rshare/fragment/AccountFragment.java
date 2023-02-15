package com.theanhdev.rshare.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theanhdev.rshare.Activities.ChatActivity;
import com.theanhdev.rshare.Activities.UserChatActivity;
import com.theanhdev.rshare.Activities.UserCustomActivity;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RhomeAdapter;
import com.theanhdev.rshare.funtionUsing.FuntionUsing;
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
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment implements PostListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE_REALTIME);
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
        ImageView moreBtn = view.findViewById(R.id.moreBtn);
        inbox = view.findViewById(R.id.inbox);
        follow = view.findViewById(R.id.follow);
        RecyclerView recyclerPostUser = view.findViewById(R.id.recyclePostUser);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference usersRef = database.getReference().child(Constants.KEY_LIST_USERS);
        DatabaseReference postsRef = database.getReference().child(Constants.KEY_COLLECTION_POSTS);

        //get bundle
        assert getArguments() != null;
        String id = getArguments().getString(Constants.KEY_USER_ID);
        assert user != null;
        Log.d("AAA", id + "\n" +user.getUid());
        inbox.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserChatActivity.class);
            intent.putExtra(Constants.KEY_USER_RECEIVER, id);
            startActivity(intent);
        });
        follow.setOnClickListener(v -> {
            usersRef.child(user.getUid()).setValue(Constants.KEY_LIST_USER_FOLLOWERS);
        });
        //if id == uid hide inbox
        inbox.setVisibility(View.VISIBLE);
        follow.setVisibility(View.VISIBLE);
        if (id.equals(user.getUid())) {
            inbox.setVisibility(View.INVISIBLE);
            follow.setVisibility(View.INVISIBLE);
        }
        List<Posts> postsList = new ArrayList<>();
        RhomeAdapter rhomeAdapter = new RhomeAdapter(postsList, this);
        recyclerPostUser.setAdapter(rhomeAdapter);
        //Load user post
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //get user info and break;
                    Users users = dataSnapshot.getValue(Users.class);
                    assert users != null;
                    if (id.equals(users.uid)) {
                        if (!Objects.equals(users.UserImage, "")) {
                            avt.setImageBitmap(getBitmapImage(users.UserImage));
                        } else avt.setImageResource(R.drawable.user_blank_img);
                        uName.setText(users.UserName);
                        postsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                postsList.clear();
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    //load post user
                                    Posts posts = dataSnapshot1.getValue(Posts.class);
                                    assert posts != null;
                                    posts.love = false;
                                    if (id.equals(posts.uid)) {
                                        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
                                        try {
                                            Date current = format.parse(FuntionUsing.getTimeCurrent());
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
                                        postsList.add(posts);
                                    }
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

        avt.setOnClickListener(v -> {

        });

        moreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCustomActivity.class);
            startActivity(intent);
        });
        return view;
    }
    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onPostClicked(Posts posts) {
    }

    @Override
    public void onUserImageClicked(Posts posts) {

    }

    @Override
    public void onLoveBtn(Posts posts) {
        DatabaseReference PostRef = database.getReference(Constants.KEY_COLLECTION_POSTS).child(posts.idPost);
        PostInf postInf = new PostInf();
        postInf.uidLovePost = FirebaseAuth.getInstance().getUid();
        if (posts.love) PostRef.child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).child(postInf.uidLovePost).removeValue();
        else PostRef.child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).child(postInf.uidLovePost).setValue(postInf);
    }


}