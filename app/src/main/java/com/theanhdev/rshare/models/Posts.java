package com.theanhdev.rshare.models;

import java.io.Serializable;
import java.util.Date;

public class Posts implements Serializable {
    public String uid, image, caption, idPost, timeStamp, userImage, userName;
    public Date dateObject;
    public Boolean love;
    public int sumLove;
}
