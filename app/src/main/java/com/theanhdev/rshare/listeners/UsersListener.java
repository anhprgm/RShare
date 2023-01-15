package com.theanhdev.rshare.listeners;

import com.theanhdev.rshare.adapters.RecentChatAdapter;
import com.theanhdev.rshare.models.ChatMessage;
import com.theanhdev.rshare.models.RecentChat;
import com.theanhdev.rshare.models.Users;

public interface UsersListener {
    void OnClickUser(Users users);
    void OnClickRecentChat(RecentChat recentChat);
}
