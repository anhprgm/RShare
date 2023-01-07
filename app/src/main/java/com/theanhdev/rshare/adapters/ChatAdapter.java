package com.theanhdev.rshare.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.theanhdev.rshare.Activities.ChatActivity;
import com.theanhdev.rshare.databinding.MessageContainerBinding;
import com.theanhdev.rshare.databinding.MessageSenderContainerBinding;
import com.theanhdev.rshare.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private String uid;

    public  static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public ChatAdapter(List<ChatMessage> chatMessages, String uid) {
        this.chatMessages = chatMessages;
        this.uid = uid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessViewHolder(
                    MessageSenderContainerBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else
            return new RecMessViewHolder(
                    MessageContainerBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessViewHolder) (holder)).setData(chatMessages.get(position));
        } else ((RecMessViewHolder) (holder)).setData(chatMessages.get(position), position);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(uid)) {
            return VIEW_TYPE_SENT;
        } else return VIEW_TYPE_RECEIVED;
    }

    private class SentMessViewHolder extends RecyclerView.ViewHolder {
        private final MessageSenderContainerBinding binding;
        SentMessViewHolder(MessageSenderContainerBinding messageSenderContainerBinding) {
            super(messageSenderContainerBinding.getRoot());
            binding = messageSenderContainerBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.message.setText(chatMessage.message);
        }
    }

    private class RecMessViewHolder extends RecyclerView.ViewHolder {
        private final MessageContainerBinding binding;
        RecMessViewHolder(MessageContainerBinding messageContainerBinding) {
            super(messageContainerBinding.getRoot());
            binding = messageContainerBinding;
        }

        void setData(ChatMessage chatMessage, int pos) {
            binding.message.setText(chatMessage.message);
            if (pos != 0) {
                if (!chatMessages.get(pos - 1).receiverId.equals(chatMessage.receiverId))
                    binding.avtReceiver.setImageBitmap(setImageBitmapString(chatMessage.avtReceiver));
            } else binding.avtReceiver.setImageBitmap(setImageBitmapString(chatMessage.avtReceiver));
        }
    }

    public Bitmap setImageBitmapString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
