package com.theanhdev.rshare.models;


import java.io.Serializable;
import java.util.Date;

public class RecentChat implements Serializable {
    public String uid_sender, name, message, avt, id_chat, uid_receiver;
    public Date date;
    public boolean seen, hasImage;
}
