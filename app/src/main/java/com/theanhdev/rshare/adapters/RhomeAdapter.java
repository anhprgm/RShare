package com.theanhdev.rshare.adapters;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theanhdev.rshare.R;
import com.theanhdev.rshare.databinding.ItemContainerPostBinding;
import com.theanhdev.rshare.listeners.PostListener;
import com.theanhdev.rshare.models.Posts;
import java.util.List;

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

            binding.timeStamp.setText(posts.timeStamp + " ago");
            binding.avt.setOnClickListener(v -> postListener.onUserImageClicked(posts));
            binding.del.setOnClickListener(v -> postListener.onDelBtn(posts));
        }
    }

    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
