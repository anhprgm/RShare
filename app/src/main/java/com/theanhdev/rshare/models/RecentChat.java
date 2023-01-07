package com.theanhdev.rshare.models;


import java.io.Serializable;
import java.util.Date;

public class RecentChat implements Serializable {
    public String uid_receiver, name, message, avt, id_chat;
    public Date date;
    public boolean seen;
}
