package com.theanhdev.rshare.models;

import java.io.Serializable;

public class Users implements Serializable {
    public String UserName, UserImage, bio, UserEmail, tagName, uid, fcm_token, follower;
    public boolean available;
}
