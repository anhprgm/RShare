package com.theanhdev.rshare.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.theanhdev.rshare.R;
import com.theanhdev.rshare.databinding.UserOnChatContainerBinding;
import com.theanhdev.rshare.listeners.UsersListener;
import com.theanhdev.rshare.models.Users;

import java.util.List;

public class UserOnChat extends RecyclerView.Adapter<UserOnChat.UserViewHolder> {
    private final List<Users> usersList;
    private final UsersListener usersListener;

    public UserOnChat(List<Users> usersList, UsersListener usersListener) {
        this.usersList = usersList;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserOnChatContainerBinding userOnChatContainerBinding = UserOnChatContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(userOnChatContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setData(usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        UserOnChatContainerBinding binding;
        UserViewHolder(UserOnChatContainerBinding userOnChatContainerBinding) {
            super(userOnChatContainerBinding.getRoot());
            binding = userOnChatContainerBinding;
        }
        void setData(Users users) {
            if (users.UserImage.equals("")) {
                binding.avt.setImageResource(R.drawable.user_blank_img);
            } else binding.avt.setImageBitmap(getBitmapImage(users.UserImage));

            binding.userName.setText(users.UserName);
            binding.avt.setOnClickListener(v -> usersListener.OnClickUser(users));
        }
    }

    private Bitmap getBitmapImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
