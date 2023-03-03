package com.theanhdev.rshare.models;

import java.io.Serializable;

public class NotificationModel implements Serializable {
    public String uid, image, caption, idPost, timeStamp, userImage, userName, uidLovePost, uidCmt, cmt;
    public Boolean love;
    public int sumLove;
}
