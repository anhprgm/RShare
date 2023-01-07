package com.theanhdev.rshare.listeners;

import com.theanhdev.rshare.adapters.RecentChatAdapter;
import com.theanhdev.rshare.models.ChatMessage;
import com.theanhdev.rshare.models.RecentChat;
import com.theanhdev.rshare.models.Users;

public interface UsersListener {
    public void OnClickUser(Users users);
    public void OnClickRecentChat(RecentChat recentChat);
}
