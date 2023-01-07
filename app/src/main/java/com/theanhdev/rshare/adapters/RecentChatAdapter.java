package com.theanhdev.rshare.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.collection.LLRBNode;
import com.theanhdev.rshare.R;
import com.theanhdev.rshare.databinding.RecentChatContainerBinding;
import com.theanhdev.rshare.listeners.UsersListener;
import com.theanhdev.rshare.models.RecentChat;

import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.RecentChatViewHolder> {
    private List<RecentChat> recentChats;
    private UsersListener usersListener;
    String uid;

    public RecentChatAdapter(List<RecentChat> recentChats, UsersListener usersListener) {
        this.recentChats = recentChats;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentChatContainerBinding recentChatContainerBinding = RecentChatContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RecentChatViewHolder(recentChatContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatViewHolder holder, int position) {
        holder.setData(recentChats.get(position));
    }

    @Override
    public int getItemCount() {
        return recentChats.size();
    }

    public class RecentChatViewHolder extends RecyclerView.ViewHolder{
        private final RecentChatContainerBinding binding;
        RecentChatViewHolder(RecentChatContainerBinding recentChatContainerBinding) {
            super(recentChatContainerBinding.getRoot());
            binding = recentChatContainerBinding;
        }

        void setData(RecentChat message) {
            binding.nameReceiver.setText(message.name);
            binding.lastMessage.setText(message.message);
            binding.avt.setImageBitmap(setImageBitmapString(message.avt));
//            int red = android.graphics.Color.RED,
//                    black = Color.BLACK;
//            if (!message.seen) {
//                binding.lastMessage.setTextColor(red);
//            } else binding.lastMessage.setTextColor(black);
            binding.recentChat.setOnClickListener(view -> usersListener.OnClickRecentChat(message));

        }
    }

    public Bitmap setImageBitmapString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
