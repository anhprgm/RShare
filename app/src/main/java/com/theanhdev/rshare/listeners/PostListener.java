package com.theanhdev.rshare.listeners;

import com.theanhdev.rshare.models.Posts;

public interface PostListener {
    void onPostClicked(Posts posts);
    void onUserImageClicked(Posts posts);
    void onDelBtn(Posts posts);
}
