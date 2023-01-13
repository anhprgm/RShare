package com.theanhdev.rshare.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.databinding.ItemContainerPostBinding;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.PostInf;
import com.theanhdev.rshare.models.Posts;
import com.theanhdev.rshare.ulities.Constants;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RhomeAdapter extends RecyclerView.Adapter<RhomeAdapter.RhomeViewHolder>{
    private final List<Posts> postsList;
    private final PostListener postListener;

    public RhomeAdapter(List<Posts> postsList, PostListener postListener) {
        this.postsList = postsList;
        this.postListener = postListener;
    }

    @NonNull
    @Override
    public RhomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerPostBinding itemContainerPostBinding = ItemContainerPostBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RhomeViewHolder(itemContainerPostBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RhomeViewHolder holder, int position) {
            holder.setData(postsList.get(position));

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class RhomeViewHolder extends RecyclerView.ViewHolder{
        ItemContainerPostBinding binding;
        RhomeViewHolder(ItemContainerPostBinding itemContainerPostBinding) {
            super(itemContainerPostBinding.getRoot());
            binding = itemContainerPostBinding;
        }
        void setData(Posts posts) {
            binding.caption.setText(posts.caption);
            binding.imagePost.setImageBitmap(getBitmapImage(posts.image));
            binding.userName.setText(posts.userName);
            if (posts.userImage.equals("")) {
                binding.avt.setImageResource(R.drawable.user_blank_img);
            } else binding.avt.setImageBitmap(getBitmapImage(posts.userImage));

            DatabaseReference PostInfRef = FirebaseDatabase.getInstance(Constants.KEY_FIREBASE).getReference(Constants.KEY_COLLECTION_POSTS);
            PostInfRef.child(posts.idPost).child(Constants.KEY_POST_INF).child(Constants.KEY_LOVE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long sumLove = snapshot.getChildrenCount();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PostInf postInf = dataSnapshot.getValue(PostInf.class);
                        assert postInf != null;
                        if (postInf.uidLovePost.equals(FirebaseAuth.getInstance().getUid())) {
                            binding.love.setImageResource(R.drawable.love_fill);
                            posts.love = true;
                            Log.d("love", true + "");
                            break;
                        }
                    }
                    binding.totalLove.setText(String.valueOf(sumLove));
                    if (!posts.love) binding.love.setImageResource(R.drawable.love_48px);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            AtomicInteger clickCount = new AtomicInteger();
            binding.timeStamp.setText(posts.timeStamp + " ago");
            binding.avt.setOnClickListener(v -> postListener.onUserImageClicked(posts));
            binding.love.setOnClickListener(v -> postListener.onLoveBtn(posts));
            binding.imagePost.setOnClickListener(v -> {
                binding.loveAnim.setVisibility(View.VISIBLE);
                clickCount.getAndIncrement();
                if (clickCount.get() == 2) {
                    clickCount.set(0);
                    postListener.onPostClicked(posts);
                } else {
                    new Handler().postDelayed(() -> {
                        clickCount.set(0);
                        binding.loveAnim.setVisibility(View.GONE);
                    }, 500);
                }
            });
        }
    }

    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
