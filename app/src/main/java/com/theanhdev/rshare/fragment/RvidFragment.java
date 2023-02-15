package com.theanhdev.rshare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.adapters.RvidAdapter;
import com.theanhdev.rshare.listeners.VideoListener;
import com.theanhdev.rshare.models.Video;
import com.theanhdev.rshare.ulities.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RvidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RvidFragment extends Fragment implements VideoListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RvidFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RvidFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RvidFragment newInstance(String param1, String param2) {
        RvidFragment fragment = new RvidFragment();
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
        // Inflate the layout for this fragment
        List<Video> videoList = new ArrayList<>();



        View view = inflater.inflate(R.layout.fragment_rvid, container, false);
        RvidAdapter rvidAdapter = new RvidAdapter(videoList, this);
        ViewPager2 viewPager2 = view.findViewById(R.id.pageVideo);
        viewPager2.setAdapter(rvidAdapter);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE_REALTIME);
        DatabaseReference storageRef = database.getReference(Constants.KEY_LIST_VIDEO);

        StorageReference storageReference = storage.getReference().child("video");
        storageReference.listAll().addOnSuccessListener(listResult -> {

            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Video video = new Video();
                    video.UrlVideo = downloadUrl;
                    storageRef.child(deleteDot(item.getName())).setValue(video);
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                });
            }
        }).addOnFailureListener(e -> {
            // Handle any errors
        });

        storageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Video video = dataSnapshot.getValue(Video.class);
                    videoList.add(video);
                }
                if (videoList.size() > 0) {
                    rvidAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }
    private String deleteDot(String s) {
        return s.replaceAll("\\.","");
    }
    @Override
    public void onClickVideo(Video video) {

    }
}