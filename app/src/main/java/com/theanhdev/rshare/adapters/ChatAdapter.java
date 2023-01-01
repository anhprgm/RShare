package com.theanhdev.rshare.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
        } else ((RecMessViewHolder) (holder)).setData(chatMessages.get(position));
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

    static class SentMessViewHolder extends RecyclerView.ViewHolder {
        private final MessageSenderContainerBinding binding;
        SentMessViewHolder(MessageSenderContainerBinding messageSenderContainerBinding) {
            super(messageSenderContainerBinding.getRoot());
            binding = messageSenderContainerBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.message.setText(chatMessage.message);
        }
    }

    static class RecMessViewHolder extends RecyclerView.ViewHolder {
        private final MessageContainerBinding binding;
        RecMessViewHolder(MessageContainerBinding messageContainerBinding) {
            super(messageContainerBinding.getRoot());
            binding = messageContainerBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.message.setText(chatMessage.message);
        }
    }
}
