package com.aliasgarmurtaza.ntuosschat;

/**
 * Created by mac on 17/10/16.
 */
public class Message {

    String message;
    String from;

    public Message(String from, String message) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
